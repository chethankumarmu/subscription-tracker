package com.brightloop.subscription.chatbot.service;

import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.chatbot.dto.FaqFact;
import com.brightloop.subscription.chatbot.dto.InvoiceFact;
import com.brightloop.subscription.chatbot.dto.SubscriptionFact;
import com.brightloop.subscription.plans.entity.Plan;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.plans.repository.PlanRepository;
import com.brightloop.subscription.plans.repository.PlanVersionRepository;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.entity.SubscriptionStatus;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import com.brightloop.subscription.support.entity.FaqArticle;
import com.brightloop.subscription.support.repository.FaqArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomerFactService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final PlanVersionRepository planVersionRepository;
    private final PlanRepository planRepository;
    private final FaqArticleRepository faqArticleRepository;

    public CustomerFactService(
            SubscriptionRepository subscriptionRepository,
            InvoiceRepository invoiceRepository,
            PlanVersionRepository planVersionRepository,
            PlanRepository planRepository,
            FaqArticleRepository faqArticleRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceRepository = invoiceRepository;
        this.planVersionRepository = planVersionRepository;
        this.planRepository = planRepository;
        this.faqArticleRepository = faqArticleRepository;
    }

    public Optional<SubscriptionFact> getLatestSubscription(Long customerId) {
        return subscriptionRepository.findFirstByCustomerIdAndStatusOrderByCreatedAtDesc(customerId, SubscriptionStatus.ACTIVE)
                .or(() -> subscriptionRepository.findFirstByCustomerIdOrderByCreatedAtDesc(customerId))
                .map(this::mapToSubscriptionFact);
    }

    public Optional<InvoiceFact> getLatestInvoice(Long customerId) {
        return invoiceRepository.findFirstByCustomerIdOrderByIssueDateDesc(customerId)
                .map(this::mapToInvoiceFact);
    }

    public Optional<FaqFact> searchFaq(String query) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }
        String term = query.trim();
        List<FaqArticle> results = faqArticleRepository.searchPublished(term);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        FaqArticle top = results.get(0);
        return Optional.of(FaqFact.builder()
                .question(top.getQuestion())
                .answer(top.getAnswer())
                .category(top.getCategory())
                .keywords(top.getKeywords())
                .build());
    }

    private SubscriptionFact mapToSubscriptionFact(Subscription sub) {
        String planName = "Custom Plan";
        String planCode = "UNKNOWN";
        Long pricePaise = 0L;
        String billingCycle = "MONTHLY";
        boolean allowPause = false;
        boolean allowRenewal = true;

        if (sub.getPlanVersionId() != null) {
            Optional<PlanVersion> pvOpt = planVersionRepository.findById(sub.getPlanVersionId());
            if (pvOpt.isPresent()) {
                PlanVersion pv = pvOpt.get();
                pricePaise = pv.getPricePaise();
                billingCycle = pv.getBillingCycle();
                allowPause = pv.isAllowPause();
                allowRenewal = pv.isAllowRenewal();

                Optional<Plan> planOpt = planRepository.findById(pv.getPlanId());
                if (planOpt.isPresent()) {
                    planName = planOpt.get().getName();
                    planCode = planOpt.get().getCode();
                }
            }
        }

        String formattedPrice = formatPaiseToRupees(pricePaise);

        return SubscriptionFact.builder()
                .number(sub.getNumber())
                .status(sub.getStatus().name())
                .paused(sub.isPaused())
                .startDate(sub.getStartDate())
                .expiryDate(sub.getExpiryDate())
                .nextBillingDate(sub.getNextBillingDate())
                .planName(planName)
                .planCode(planCode)
                .pricePaise(pricePaise)
                .priceFormatted(formattedPrice)
                .billingCycle(billingCycle)
                .allowPause(allowPause)
                .allowRenewal(allowRenewal)
                .build();
    }

    private InvoiceFact mapToInvoiceFact(Invoice inv) {
        long total = inv.getTotalPaise() != null ? inv.getTotalPaise() : 0L;
        long paid = inv.getAmountPaidPaise() != null ? inv.getAmountPaidPaise() : 0L;
        long remaining = Math.max(0L, total - paid);

        return InvoiceFact.builder()
                .number(inv.getNumber())
                .status(inv.getStatus().name())
                .totalPaise(total)
                .totalFormatted(formatPaiseToRupees(total))
                .amountPaidPaise(paid)
                .remainingPaise(remaining)
                .dueDate(inv.getDueDate())
                .issueDate(inv.getIssueDate())
                .periodStart(inv.getPeriodStart())
                .periodEnd(inv.getPeriodEnd())
                .currency(inv.getCurrency())
                .build();
    }

    private String formatPaiseToRupees(Long paise) {
        if (paise == null) {
            return "₹0.00";
        }
        BigDecimal rupees = BigDecimal.valueOf(paise).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return "₹" + rupees;
    }
}

