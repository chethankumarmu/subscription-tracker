package com.subscriptiontracker.subscription;

import com.subscriptiontracker.catalog.ProductVariant;
import com.subscriptiontracker.catalog.ProductVariantRepository;
import com.subscriptiontracker.plan.*;
import com.subscriptiontracker.subscription.Subscription.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** T3.4 quotations + T3.5 subscription state machine. */
@Service
@Transactional
public class SubscriptionService {

    /** The only legal forward moves: DRAFT -> QUOTATION -> CONFIRMED -> ACTIVE -> CLOSED. */
    private static final Map<Status, Status> NEXT = Map.of(
            Status.DRAFT, Status.QUOTATION,
            Status.QUOTATION, Status.CONFIRMED,
            Status.CONFIRMED, Status.ACTIVE,
            Status.ACTIVE, Status.CLOSED);

    public record CreateRequest(@NotNull Long customerId, @NotNull Long planId, Long variantId,
                                @Positive Integer quantity, LocalDate startDate) {}

    private final SubscriptionRepository subs;
    private final QuotationTemplateRepository templates;
    private final PlanService planService;
    private final PlanRepository plans;
    private final PlanVersionRepository versions;
    private final ProductVariantRepository variants;

    public SubscriptionService(SubscriptionRepository subs, QuotationTemplateRepository templates,
                               PlanService planService, PlanRepository plans,
                               PlanVersionRepository versions, ProductVariantRepository variants) {
        this.subs = subs;
        this.templates = templates;
        this.planService = planService;
        this.plans = plans;
        this.versions = versions;
        this.variants = variants;
    }

    // ---------- create / read ----------

    /** Locks the plan version and unit price at creation (price = plan price + variant delta). */
    public Subscription createDraft(CreateRequest r) {
        LocalDate start = r.startDate() != null ? r.startDate() : LocalDate.now();
        // throws 409 if the plan is not PUBLISHED (retired plans cannot get new subscriptions)
        PlanVersion v = planService.currentVersion(r.planId(), start).version();
        Plan plan = plans.findById(r.planId()).orElseThrow(() -> notFound("Plan"));

        int qty = r.quantity() != null ? r.quantity() : v.getMinQuantity();
        if (qty < v.getMinQuantity())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Minimum quantity for this plan is " + v.getMinQuantity());

        long unitPrice = v.getPrice();
        if (r.variantId() != null) {
            ProductVariant variant = variants.findById(r.variantId()).orElseThrow(() -> notFound("Variant"));
            if (!variant.getProductId().equals(plan.getProductId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Variant does not belong to this plan's product");
            unitPrice += variant.getPriceDelta();
        }

        Subscription s = new Subscription();
        s.setNumber(String.format("SUB-%06d", subs.nextNumber()));
        s.setCustomerId(r.customerId());
        s.setPlanVersionId(v.getId());
        s.setVariantId(r.variantId());
        s.setQuantity(qty);
        s.setUnitPrice(unitPrice);
        s.setStartDate(start);
        s.setStatus(Status.DRAFT);
        return subs.saveAndFlush(s);
    }

    @Transactional(readOnly = true)
    public Subscription get(Long id) {
        return subs.findById(id).orElseThrow(() -> notFound("Subscription"));
    }

    @Transactional(readOnly = true)
    public List<Subscription> list(Long customerId) {
        return customerId == null ? subs.findAll() : subs.findByCustomerIdOrderByIdDesc(customerId);
    }

    // ---------- state machine ----------

    /** DRAFT -> QUOTATION: attaches template, price stays locked for validity_days. */
    public Subscription generateQuotation(Long id, Long templateId) {
        Subscription s = get(id);
        move(s, Status.QUOTATION);
        QuotationTemplate t = templates.findById(templateId).orElseThrow(() -> notFound("Quotation template"));
        s.setQuotationTemplateId(t.getId());
        s.setQuotationValidUntil(LocalDate.now().plusDays(t.getValidityDays()));
        return subs.saveAndFlush(s);
    }

    /** QUOTATION -> CONFIRMED (customer accepted, quotation must still be valid). */
    public Subscription confirm(Long id) {
        Subscription s = get(id);
        move(s, Status.CONFIRMED);
        if (s.getQuotationValidUntil() != null && LocalDate.now().isAfter(s.getQuotationValidUntil()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Quotation has expired");
        return subs.saveAndFlush(s);
    }

    /** CONFIRMED -> ACTIVE: sets the first expiry and the first billing date. */
    public Subscription activate(Long id) {
        Subscription s = get(id);
        move(s, Status.ACTIVE);
        PlanVersion v = versionOf(s);
        if (s.getStartDate() == null) s.setStartDate(LocalDate.now());
        s.setExpiryDate(CycleMath.plus(v.getBillingCycle(), s.getStartDate()));
        s.setNextBillingDate(s.getStartDate());
        return subs.saveAndFlush(s);
    }

    public Subscription pause(Long id) {
        Subscription s = requireActive(get(id));
        if (!versionOf(s).isAllowPause())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This plan does not allow pausing");
        if (s.isPaused())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subscription is already paused");
        s.setPaused(true);
        return subs.saveAndFlush(s);
    }

    /**
     * Resume: billing cycles that fell inside the pause are skipped (not billed), and expiry is pushed
     * out by the same number of cycles so the customer still receives the cycles they pay for.
     */
    public Subscription resume(Long id) {
        Subscription s = requireActive(get(id));
        if (!s.isPaused())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subscription is not paused");
        PlanVersion v = versionOf(s);
        LocalDate today = LocalDate.now();
        LocalDate next = s.getNextBillingDate();
        LocalDate expiry = s.getExpiryDate();
        while (next != null && expiry != null && next.isBefore(today) && next.isBefore(expiry)) {
            next = CycleMath.plus(v.getBillingCycle(), next);
            expiry = CycleMath.plus(v.getBillingCycle(), expiry);
        }
        s.setNextBillingDate(next);
        s.setExpiryDate(expiry);
        s.setPaused(false);
        return subs.saveAndFlush(s);
    }

    /** Extends expiry by one cycle from the current expiry, so there is no gap or overlap. */
    public Subscription renew(Long id) {
        Subscription s = requireActive(get(id));
        PlanVersion v = versionOf(s);
        if (!v.isAllowRenewal())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This plan does not allow renewal");
        s.setExpiryDate(CycleMath.plus(v.getBillingCycle(), s.getExpiryDate()));
        return subs.saveAndFlush(s);
    }

    /** ACTIVE -> CLOSED. Closing before expiry needs allow_early_close on the plan version. */
    public Subscription close(Long id) {
        Subscription s = get(id);
        move(s, Status.CLOSED);
        boolean early = s.getExpiryDate() != null && LocalDate.now().isBefore(s.getExpiryDate());
        if (early && !versionOf(s).isAllowEarlyClose())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This plan does not allow early close");
        s.setStatus(Status.CLOSED);
        return subs.saveAndFlush(s);
    }

    // ---------- helpers ----------

    private void move(Subscription s, Status to) {
        if (NEXT.get(s.getStatus()) != to)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Invalid transition " + s.getStatus() + " -> " + to);
        s.setStatus(to);
    }

    private Subscription requireActive(Subscription s) {
        if (s.getStatus() != Status.ACTIVE)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subscription is not ACTIVE");
        return s;
    }

    private PlanVersion versionOf(Subscription s) {
        return versions.findById(s.getPlanVersionId()).orElseThrow(() -> notFound("Plan version"));
    }

    private ResponseStatusException notFound(String what) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found");
    }
}
