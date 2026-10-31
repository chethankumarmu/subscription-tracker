package com.brightloop.subscription.subscriptions.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.common.util.MoneyUtils;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.plans.repository.PlanVersionRepository;
import com.brightloop.subscription.subscriptions.dto.CreateSubscriptionRequest;
import com.brightloop.subscription.subscriptions.dto.RenewalResponseDto;
import com.brightloop.subscription.subscriptions.dto.SubscriptionResponseDto;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.entity.SubscriptionLine;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceService invoiceService;
    private final EmailOutboxService emailOutboxService;
    private final PlanVersionRepository planVersionRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            InvoiceService invoiceService,
            EmailOutboxService emailOutboxService,
            PlanVersionRepository planVersionRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceService = invoiceService;
        this.emailOutboxService = emailOutboxService;
        this.planVersionRepository = planVersionRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new subscription. Takes plan_version_id from the request.
     * Strictly rejects DRAFT or RETIRED versions.
     * Price is snapshotted into subscription_lines.
     */
    @Transactional
    public SubscriptionResponseDto createSubscription(CreateSubscriptionRequest request, String currentUsername, boolean isAdminOrStaff) {
        PlanVersion planVersion = planVersionRepository.findById(request.getPlanVersionId())
                .orElseThrow(() -> new IllegalArgumentException("Plan version not found with id: " + request.getPlanVersionId()));

        // Reject DRAFT or RETIRED versions
        if (!"PUBLISHED".equals(planVersion.getStatus()) || planVersion.getEffectiveTo() != null) {
            throw new IllegalStateException("Cannot subscribe to an unpublished or retired plan version. Current status: " + planVersion.getStatus());
        }

        // Availability window checks
        LocalDate today = LocalDate.now();
        if (planVersion.getAvailableFrom() != null && today.isBefore(planVersion.getAvailableFrom())) {
            throw new IllegalStateException("Plan version is not yet available for subscription");
        }
        if (planVersion.getAvailableTo() != null && today.isAfter(planVersion.getAvailableTo())) {
            throw new IllegalStateException("Plan version is no longer available for subscription");
        }

        // Resolve customer
        Customer customer = resolveCustomer(request.getCustomerId(), currentUsername, isAdminOrStaff);

        // Validate quantity
        int quantity = request.getQuantity() != null ? request.getQuantity() : 1;
        if (planVersion.getMinQuantity() != null && quantity < planVersion.getMinQuantity()) {
            throw new IllegalArgumentException("Quantity cannot be less than minimum quantity: " + planVersion.getMinQuantity());
        }

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        int termsDays = request.getPaymentTermsDays() != null ? request.getPaymentTermsDays() : 30;
        String cycle = planVersion.getBillingCycle() != null ? planVersion.getBillingCycle() : "MONTHLY";
        LocalDate expiryDate = invoiceService.extendExpiryDate(startDate, cycle);

        String subNumber = generateSubscriptionNumber();

        Subscription subscription = Subscription.builder()
                .number(subNumber)
                .customer(customer)
                .planVersion(planVersion)
                .status("CONFIRMED")
                .isPaused(false)
                .startDate(startDate)
                .expiryDate(expiryDate)
                .paymentTermsDays(termsDays)
                .nextBillingDate(startDate)
                .lines(new ArrayList<>())
                .build();

        // Price snapshot into subscription_lines
        long unitPricePaise = planVersion.getPricePaise();
        long lineTotalPaise = unitPricePaise * quantity;

        SubscriptionLine line = SubscriptionLine.builder()
                .subscription(subscription)
                .product(planVersion.getPlan() != null ? planVersion.getPlan().getProduct() : null)
                .quantity(quantity)
                .unitPricePaise(unitPricePaise)
                .lineTotalPaise(lineTotalPaise)
                .build();

        subscription.getLines().add(line);

        Subscription saved = subscriptionRepository.save(subscription);

        return new SubscriptionResponseDto(
                saved.getId(),
                saved.getNumber(),
                customer.getId(),
                customer.getContactName(),
                planVersion.getId(),
                planVersion.getPlan() != null ? planVersion.getPlan().getCode() : null,
                planVersion.getVersionNo(),
                saved.getStatus(),
                saved.getStartDate(),
                saved.getExpiryDate(),
                saved.getNextBillingDate(),
                lineTotalPaise,
                MoneyUtils.formatPaiseToRupees(lineTotalPaise)
        );
    }

    /**
     * Activates a subscription, generates its first invoice if none exists,
     * and queues the invoice PDF email.
     */
    @Transactional
    public Invoice activateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        subscription.setStatus("ACTIVE");

        LocalDate startDate = subscription.getStartDate() != null ? subscription.getStartDate() : LocalDate.now();
        subscription.setStartDate(startDate);

        String cycle = subscription.getPlanVersion() != null ? subscription.getPlanVersion().getBillingCycle() : "MONTHLY";
        LocalDate periodEnd = invoiceService.calculatePeriodEnd(startDate, cycle);

        if (subscription.getExpiryDate() == null) {
            subscription.setExpiryDate(invoiceService.extendExpiryDate(startDate, cycle));
        }
        if (subscription.getNextBillingDate() == null) {
            subscription.setNextBillingDate(startDate);
        }

        subscriptionRepository.save(subscription);

        // Generate first invoice
        Invoice invoice = invoiceService.generateInvoiceForSubscription(subscription, startDate, periodEnd, "CONFIRMED");

        // Queue invoice email with PDF attachment
        emailOutboxService.queueInvoiceEmail(invoice);

        return invoice;
    }

    /**
     * Renews a subscription, creating a renewal invoice for the next period,
     * extending the expiry date, and queueing an invoice email with PDF.
     * Idempotent: repeat calls for the same period return the existing invoice.
     */
    @Transactional
    public RenewalResponseDto renewSubscription(Long subscriptionId, String currentUsername, boolean isAdminOrStaff) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        // Ownership verification for CUSTOMER role
        if (!isAdminOrStaff) {
            boolean isOwner = false;
            if (subscription.getCustomer() != null && subscription.getCustomer().getUser() != null) {
                isOwner = currentUsername.equalsIgnoreCase(subscription.getCustomer().getUser().getEmail());
            }
            if (!isOwner) {
                throw new AccessDeniedException("You do not have permission to renew subscriptions belonging to other customers");
            }
        }

        // Renewal permission check on plan version
        PlanVersion planVersion = subscription.getPlanVersion();
        if (planVersion != null && Boolean.FALSE.equals(planVersion.getAllowRenewal())) {
            throw new IllegalStateException("Plan version does not allow renewal");
        }

        String cycle = planVersion != null ? planVersion.getBillingCycle() : "MONTHLY";
        LocalDate currentExpiry = subscription.getExpiryDate() != null ? subscription.getExpiryDate() : LocalDate.now();

        // Check if a renewal invoice has already been generated for this period
        Optional<Invoice> latestInvoiceOpt = invoiceService.findLatestInvoiceForSubscription(subscriptionId);
        if (latestInvoiceOpt.isPresent()) {
            Invoice latestInvoice = latestInvoiceOpt.get();
            if (latestInvoice.getPeriodEnd() != null && latestInvoice.getPeriodEnd().isEqual(currentExpiry)
                    && !"CANCELLED".equals(latestInvoice.getStatus())) {
                // Return existing renewal invoice (idempotent repeat call)
                emailOutboxService.queueInvoiceEmail(latestInvoice);
                return new RenewalResponseDto(
                        latestInvoice.getId(),
                        latestInvoice.getNumber(),
                        subscription.getId(),
                        subscription.getNumber(),
                        latestInvoice.getStatus(),
                        latestInvoice.getPeriodStart(),
                        latestInvoice.getPeriodEnd(),
                        latestInvoice.getDueDate(),
                        latestInvoice.getTotalPaise(),
                        MoneyUtils.formatPaiseToRupees(latestInvoice.getTotalPaise()),
                        subscription.getExpiryDate()
                );
            }
        }

        LocalDate nextPeriodStart = currentExpiry.plusDays(1);
        LocalDate nextPeriodEnd = invoiceService.calculatePeriodEnd(nextPeriodStart, cycle);

        // Idempotent invoice creation
        Invoice invoice = invoiceService.generateInvoiceForSubscription(
                subscription, nextPeriodStart, nextPeriodEnd, "CONFIRMED"
        );

        // Extend expiry date
        LocalDate newExpiry = invoiceService.extendExpiryDate(currentExpiry, cycle);
        subscription.setExpiryDate(newExpiry);
        subscription.setNextBillingDate(nextPeriodStart);
        subscriptionRepository.save(subscription);

        // Queue email with PDF attachment
        emailOutboxService.queueInvoiceEmail(invoice);

        return new RenewalResponseDto(
                invoice.getId(),
                invoice.getNumber(),
                subscription.getId(),
                subscription.getNumber(),
                invoice.getStatus(),
                invoice.getPeriodStart(),
                invoice.getPeriodEnd(),
                invoice.getDueDate(),
                invoice.getTotalPaise(),
                MoneyUtils.formatPaiseToRupees(invoice.getTotalPaise()),
                subscription.getExpiryDate()
        );
    }

    private Customer resolveCustomer(Long requestedCustomerId, String currentUsername, boolean isAdminOrStaff) {
        if (requestedCustomerId != null && isAdminOrStaff) {
            return customerRepository.findById(requestedCustomerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + requestedCustomerId));
        }

        if (currentUsername != null && !currentUsername.isBlank()) {
            Optional<User> userOpt = userRepository.findByEmail(currentUsername);
            if (userOpt.isPresent()) {
                Optional<Customer> custOpt = customerRepository.findByUserId(userOpt.get().getId());
                if (custOpt.isPresent()) {
                    return custOpt.get();
                }
            }
        }

        if (requestedCustomerId != null) {
            return customerRepository.findById(requestedCustomerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + requestedCustomerId));
        }

        return customerRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No customers available in system"));
    }

    private String generateSubscriptionNumber() {
        long count = subscriptionRepository.count() + 1;
        String number = "SUB-" + LocalDate.now().getYear() + "-" + String.format("%06d", count);
        if (subscriptionRepository.findByNumber(number).isPresent()) {
            number = "SUB-" + LocalDate.now().getYear() + "-" + String.format("%06d", count + (System.currentTimeMillis() % 1000));
        }
        return number;
    }
}
