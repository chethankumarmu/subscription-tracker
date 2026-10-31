package com.brightloop.subscription.subscriptions.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.common.util.MoneyUtils;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.plans.repository.PlanVersionRepository;
import com.brightloop.subscription.subscriptions.dto.CreateSubscriptionRequest;
import com.brightloop.subscription.subscriptions.dto.RenewalRequestResponseDto;
import com.brightloop.subscription.subscriptions.dto.RenewalResponseDto;
import com.brightloop.subscription.subscriptions.dto.SubscriptionResponseDto;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.entity.SubscriptionLine;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import com.brightloop.subscription.support.entity.SupportTicket;
import com.brightloop.subscription.support.entity.TicketComment;
import com.brightloop.subscription.support.repository.SupportTicketRepository;
import com.brightloop.subscription.support.repository.TicketCommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceService invoiceService;
    private final EmailOutboxService emailOutboxService;
    private final PlanVersionRepository planVersionRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final TicketCommentRepository ticketCommentRepository;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            InvoiceService invoiceService,
            EmailOutboxService emailOutboxService,
            PlanVersionRepository planVersionRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository
    ) {
        this(subscriptionRepository, invoiceService, emailOutboxService, planVersionRepository, customerRepository, userRepository, null, null);
    }

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            InvoiceService invoiceService,
            EmailOutboxService emailOutboxService,
            PlanVersionRepository planVersionRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            SupportTicketRepository supportTicketRepository,
            TicketCommentRepository ticketCommentRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceService = invoiceService;
        this.emailOutboxService = emailOutboxService;
        this.planVersionRepository = planVersionRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.supportTicketRepository = supportTicketRepository;
        this.ticketCommentRepository = ticketCommentRepository;
    }

    @Transactional
    public SubscriptionResponseDto createSubscription(CreateSubscriptionRequest request, String currentUsername, boolean isAdminOrStaff) {
        PlanVersion planVersion = planVersionRepository.findById(request.getPlanVersionId())
                .orElseThrow(() -> new IllegalArgumentException("Plan version not found: " + request.getPlanVersionId()));

        if (!"PUBLISHED".equals(planVersion.getStatus()) || planVersion.getEffectiveTo() != null) {
            throw new IllegalStateException("Cannot subscribe to an unpublished or retired plan version. Current status: " + planVersion.getStatus());
        }

        Customer customer = resolveCustomer(request.getCustomerId(), currentUsername, isAdminOrStaff);

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

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponseDto> getAllSubscriptions(UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            Long custId = principal.getCustomerId();
            if (custId == null) {
                return List.of();
            }
            return subscriptionRepository.findByCustomerId(custId).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
        return subscriptionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubscriptionResponseDto getSubscriptionById(Long id, UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            if (principal.getCustomerId() == null || !principal.getCustomerId().equals(sub.getCustomerId())) {
                // Return 404 to hide other customers' subscriptions
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found");
            }
        }
        return toDto(sub);
    }

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
        subscription.setNextBillingDate(periodEnd.plusDays(1));
        subscriptionRepository.save(subscription);

        // Step 2: Create its first invoice if none exists, then queue the invoice email with PDF attached
        Optional<Invoice> existingInvoice = invoiceService.findLatestInvoiceForSubscription(subscriptionId);
        Invoice invoice;
        if (existingInvoice.isEmpty()) {
            invoice = invoiceService.generateInvoiceForSubscription(subscription, startDate, periodEnd, "CONFIRMED");
            if (emailOutboxService != null) {
                emailOutboxService.queueInvoiceEmail(invoice);
            }
        } else {
            invoice = existingInvoice.get();
        }

        return invoice;
    }

    @Transactional
    public SubscriptionResponseDto pauseSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        if (!Boolean.TRUE.equals(subscription.getAllowPause())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription does not allow pause");
        }

        subscription.setStatus("PAUSED");
        subscription.setUpdatedAt(OffsetDateTime.now());
        Subscription saved = subscriptionRepository.save(subscription);
        return toDto(saved);
    }

    @Transactional
    public SubscriptionResponseDto closeSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        subscription.setStatus("CLOSED");
        subscription.setUpdatedAt(OffsetDateTime.now());
        Subscription saved = subscriptionRepository.save(subscription);
        return toDto(saved);
    }

    @Transactional
    public RenewalRequestResponseDto requestRenewal(Long subscriptionId, UserPrincipal principal) {
        if (principal == null || !"CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can submit renewal requests");
        }

        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        if (principal.getCustomerId() == null || !principal.getCustomerId().equals(sub.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found");
        }

        PlanVersion planVersion = sub.getPlanVersion();
        if (planVersion == null || !Boolean.TRUE.equals(planVersion.getAllowRenewal())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan version does not allow renewal requests");
        }

        String ticketNumber = "TKT-REN-" + sub.getNumber();
        SupportTicket ticket = supportTicketRepository.save(SupportTicket.builder()
                .number(ticketNumber)
                .customerId(sub.getCustomerId())
                .subject("Renewal Request: Subscription " + sub.getNumber())
                .status("OPEN")
                .priority("HIGH")
                .build());

        ticketCommentRepository.save(TicketComment.builder()
                .ticketId(ticket.getId())
                .authorId(principal.getUserId())
                .body("Customer requested renewal for subscription " + sub.getNumber() + " (Plan: "
                        + (planVersion.getPlan() != null ? planVersion.getPlan().getCode() : "N/A") + " v" + planVersion.getVersionNo() + ").")
                .isInternal(false)
                .build());

        return new RenewalRequestResponseDto(
                ticket.getId(),
                ticket.getNumber(),
                sub.getId(),
                sub.getNumber(),
                "Renewal request submitted successfully for staff review"
        );
    }

    @Transactional
    public RenewalResponseDto renewSubscription(Long subscriptionId, UserPrincipal principal) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        if (principal != null && "CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            if (principal.getCustomerId() == null || !principal.getCustomerId().equals(subscription.getCustomerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found");
            }
        }

        return doRenewSubscription(subscription);
    }

    @Transactional
    public RenewalResponseDto renewSubscription(Long subscriptionId, String currentUsername, boolean isAdminOrStaff) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        if (!isAdminOrStaff) {
            boolean isOwner = false;
            if (subscription.getCustomer() != null && subscription.getCustomer().getUser() != null) {
                isOwner = currentUsername != null && currentUsername.equalsIgnoreCase(subscription.getCustomer().getUser().getEmail());
            }
            if (!isOwner) {
                throw new AccessDeniedException("You do not have permission to renew subscriptions belonging to other customers");
            }
        }

        return doRenewSubscription(subscription);
    }

    private RenewalResponseDto doRenewSubscription(Subscription subscription) {
        PlanVersion planVersion = subscription.getPlanVersion();
        if (planVersion == null || Boolean.FALSE.equals(planVersion.getAllowRenewal())) {
            throw new IllegalStateException("Plan version does not allow renewal");
        }

        String cycle = planVersion.getBillingCycle() != null ? planVersion.getBillingCycle() : "MONTHLY";
        LocalDate currentExpiry = subscription.getExpiryDate() != null ? subscription.getExpiryDate() : LocalDate.now();

        Optional<Invoice> latestInvoiceOpt = invoiceService.findLatestInvoiceForSubscription(subscription.getId());
        if (latestInvoiceOpt.isPresent()) {
            Invoice latestInvoice = latestInvoiceOpt.get();
            if (latestInvoice.getPeriodEnd() != null && latestInvoice.getPeriodEnd().isEqual(currentExpiry)
                    && !"CANCELLED".equals(latestInvoice.getStatus())) {
                if (emailOutboxService != null) {
                    emailOutboxService.queueInvoiceEmail(latestInvoice);
                }
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

        Invoice invoice = invoiceService.generateInvoiceForSubscription(
                subscription, nextPeriodStart, nextPeriodEnd, "CONFIRMED"
        );

        LocalDate newExpiry = invoiceService.extendExpiryDate(currentExpiry, cycle);
        subscription.setExpiryDate(newExpiry);
        subscription.setNextBillingDate(nextPeriodStart);
        subscriptionRepository.save(subscription);

        if (emailOutboxService != null) {
            emailOutboxService.queueInvoiceEmail(invoice);
        }

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

    public SubscriptionResponseDto toDto(Subscription saved) {
        Long lineTotalPaise = 0L;
        if (saved.getLines() != null && !saved.getLines().isEmpty()) {
            lineTotalPaise = saved.getLines().stream()
                    .mapToLong(l -> l.getUnitPricePaise() * (l.getQuantity() != null ? l.getQuantity() : 1))
                    .sum();
        } else if (saved.getPlanVersion() != null) {
            lineTotalPaise = saved.getPlanVersion().getPricePaise();
        }

        return new SubscriptionResponseDto(
                saved.getId(),
                saved.getNumber(),
                saved.getCustomerId(),
                saved.getCustomer() != null ? saved.getCustomer().getContactName() : null,
                saved.getPlanVersionId(),
                saved.getPlanVersion() != null && saved.getPlanVersion().getPlan() != null ? saved.getPlanVersion().getPlan().getCode() : null,
                saved.getPlanVersion() != null ? saved.getPlanVersion().getVersionNo() : null,
                saved.getStatus(),
                saved.getStartDate(),
                saved.getExpiryDate(),
                saved.getNextBillingDate(),
                lineTotalPaise,
                MoneyUtils.formatPaiseToRupees(lineTotalPaise)
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
