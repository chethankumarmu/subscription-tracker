package com.brightloop.subscription.service;

import com.brightloop.subscription.model.*;
import com.brightloop.subscription.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PlanRepository planRepository;
    private final PlanVersionRepository planVersionRepository;
    private final PlanLimitRepository planLimitRepository;
    private final TaxRuleRepository taxRuleRepository;
    private final DiscountRepository discountRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionLineRepository subscriptionLineRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final PaymentRepository paymentRepository;
    private final FaqArticleRepository faqArticleRepository;
    private final SupportTicketRepository supportTicketRepository;

    public DataSeeder(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            PlanRepository planRepository,
            PlanVersionRepository planVersionRepository,
            PlanLimitRepository planLimitRepository,
            TaxRuleRepository taxRuleRepository,
            DiscountRepository discountRepository,
            SubscriptionRepository subscriptionRepository,
            SubscriptionLineRepository subscriptionLineRepository,
            InvoiceRepository invoiceRepository,
            InvoiceLineRepository invoiceLineRepository,
            PaymentRepository paymentRepository,
            FaqArticleRepository faqArticleRepository,
            SupportTicketRepository supportTicketRepository
    ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.planRepository = planRepository;
        this.planVersionRepository = planVersionRepository;
        this.planLimitRepository = planLimitRepository;
        this.taxRuleRepository = taxRuleRepository;
        this.discountRepository = discountRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionLineRepository = subscriptionLineRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.paymentRepository = paymentRepository;
        this.faqArticleRepository = faqArticleRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping initial seeding.");
            return;
        }

        log.info("Seeding database with realistic production data matching UI design contracts...");

        // 1. Users
        User admin = userRepository.save(User.builder()
                .email("admin@brightloop.io")
                .fullName("Alex Vance")
                .role("ADMIN")
                .passwordHash("$2a$12$eX4mP1eH4shForAdm1nSecur1tyKeyBrightloop")
                .isActive(true)
                .build());

        User staff = userRepository.save(User.builder()
                .email("sarah.chen@brightloop.io")
                .fullName("Sarah Chen")
                .role("STAFF")
                .passwordHash("$2a$12$eX4mP1eH4shForSt4ffSecur1tyKeyBrightloop")
                .isActive(true)
                .build());

        User custUser = userRepository.save(User.builder()
                .email("john.doe@acme.com")
                .fullName("John Doe")
                .role("CUSTOMER")
                .passwordHash("$2a$12$eX4mP1eH4shForCustSecur1tyKeyBrightloop")
                .isActive(true)
                .build());

        // 2. Customers
        Customer acme = customerRepository.save(Customer.builder()
                .userId(custUser.getId())
                .companyName("Acme Corp")
                .contactName("John Doe")
                .phone("+91 98765 43210")
                .billingAddress("{\"street\":\"102 Brigade Road\",\"city\":\"Bangalore\",\"state\":\"Karnataka\",\"pincode\":\"560001\"}")
                .gstin("29ABCDE1234F1Z5")
                .status("ACTIVE")
                .build());

        Customer starlight = customerRepository.save(Customer.builder()
                .companyName("Starlight Media")
                .contactName("Sophia Miller")
                .phone("+91 98765 43211")
                .billingAddress("{\"street\":\"44 Bandra Kurla Complex\",\"city\":\"Mumbai\",\"state\":\"Maharashtra\",\"pincode\":\"400051\"}")
                .gstin("27AAACB9876P1ZV")
                .status("ACTIVE")
                .build());

        Customer nexus = customerRepository.save(Customer.builder()
                .companyName("Nexus Innovations")
                .contactName("Rajesh Kumar")
                .phone("+91 98765 43212")
                .billingAddress("{\"street\":\"Sector 62\",\"city\":\"Noida\",\"state\":\"Uttar Pradesh\",\"pincode\":\"201309\"}")
                .gstin("09XYZPQ5678M1Z8")
                .status("ACTIVE")
                .build());

        Customer bluewave = customerRepository.save(Customer.builder()
                .companyName("BlueWave Tech")
                .contactName("Emily Watson")
                .phone("+91 98765 43213")
                .billingAddress("{\"street\":\"Cyber City Hub\",\"city\":\"Gurugram\",\"state\":\"Haryana\",\"pincode\":\"122002\"}")
                .gstin("06DEFGH4321K1Z2")
                .status("ACTIVE")
                .build());

        Customer vertex = customerRepository.save(Customer.builder()
                .companyName("Vertex Labs")
                .contactName("David Kim")
                .phone("+91 98765 43214")
                .billingAddress("{\"street\":\"HITEC City\",\"city\":\"Hyderabad\",\"state\":\"Telangana\",\"pincode\":\"500081\"}")
                .gstin("36KLMNO7890R1ZT")
                .status("ACTIVE")
                .build());

        Customer quantum = customerRepository.save(Customer.builder()
                .companyName("Quantum Dynamics")
                .contactName("Elena Rostova")
                .phone("+91 98765 43215")
                .billingAddress("{\"street\":\"Tidel Park\",\"city\":\"Chennai\",\"state\":\"Tamil Nadu\",\"pincode\":\"600113\"}")
                .gstin("33UVWXY1357Q1Z9")
                .status("ACTIVE")
                .build());

        // 3. Tax Rule
        TaxRule gst18 = taxRuleRepository.save(TaxRule.builder()
                .name("GST 18%")
                .rateBps(1800)
                .applies_to("ALL")
                .isActive(true)
                .build());

        // 4. Products & Variants
        Product coreProd = productRepository.save(Product.builder()
                .sku("SKU-SUB-CORE")
                .name("Brightloop Core SaaS Platform")
                .productType("SERVICE")
                .salesPricePaise(199900L) // ₹1,999.00
                .costPricePaise(30000L)
                .notes("Standard cloud subscription tier")
                .isActive(true)
                .build());

        Product storageAddon = productRepository.save(Product.builder()
                .sku("SKU-ADDON-STORAGE")
                .name("Extra Cloud Storage (100 GB)")
                .productType("ADDON")
                .salesPricePaise(49900L) // ₹499.00
                .costPricePaise(8000L)
                .notes("High-speed NVMe S3-compatible storage")
                .isActive(true)
                .build());

        Product seatAddon = productRepository.save(Product.builder()
                .sku("SKU-ADDON-SEATS")
                .name("Team Member Seat (Per User)")
                .productType("ADDON")
                .salesPricePaise(49900L) // ₹499.00
                .costPricePaise(5000L)
                .notes("Full workspace collaborator seat")
                .isActive(true)
                .build());

        // 5. Plans & Plan Versions (Starter, Pro [RECOMMENDED], Enterprise)
        // Starter Plan
        Plan planStarter = planRepository.save(Plan.builder()
                .code("PLAN_STARTER")
                .name("Starter")
                .productId(coreProd.getId())
                .status("PUBLISHED")
                .build());

        PlanVersion pvStarter = planVersionRepository.save(PlanVersion.builder()
                .planId(planStarter.getId())
                .versionNo(1)
                .pricePaise(0L) // Free
                .billingCycle("MONTHLY")
                .minQuantity(1)
                .availableFrom(LocalDate.now().minusMonths(6))
                .allowPause(false)
                .allowRenewal(true)
                .createdBy(admin.getId())
                .build());

        planStarter.setCurrentVersionId(pvStarter.getId());
        planRepository.save(planStarter);

        planLimitRepository.saveAll(List.of(
                PlanLimit.builder().planVersionId(pvStarter.getId()).limitKey("projects").limitValue(3L).build(),
                PlanLimit.builder().planVersionId(pvStarter.getId()).limitKey("storage_gb").limitValue(5L).build(),
                PlanLimit.builder().planVersionId(pvStarter.getId()).limitKey("custom_domains").limitValue(0L).build(),
                PlanLimit.builder().planVersionId(pvStarter.getId()).limitKey("templates").limitValue(10L).build()
        ));

        // Pro Plan (Recommended)
        Plan planPro = planRepository.save(Plan.builder()
                .code("PLAN_PRO")
                .name("Pro")
                .productId(coreProd.getId())
                .status("PUBLISHED")
                .build());

        PlanVersion pvPro = planVersionRepository.save(PlanVersion.builder()
                .planId(planPro.getId())
                .versionNo(1)
                .pricePaise(199900L) // ₹1,999 / mo
                .billingCycle("MONTHLY")
                .minQuantity(1)
                .availableFrom(LocalDate.now().minusMonths(6))
                .allowPause(true)
                .allowRenewal(true)
                .createdBy(admin.getId())
                .build());

        planPro.setCurrentVersionId(pvPro.getId());
        planRepository.save(planPro);

        planLimitRepository.saveAll(List.of(
                PlanLimit.builder().planVersionId(pvPro.getId()).limitKey("projects").limitValue(-1L).build(), // Unlimited
                PlanLimit.builder().planVersionId(pvPro.getId()).limitKey("storage_gb").limitValue(50L).build(),
                PlanLimit.builder().planVersionId(pvPro.getId()).limitKey("custom_domains").limitValue(3L).build(),
                PlanLimit.builder().planVersionId(pvPro.getId()).limitKey("templates").limitValue(50L).build()
        ));

        // Enterprise Plan
        Plan planEnterprise = planRepository.save(Plan.builder()
                .code("PLAN_ENTERPRISE")
                .name("Enterprise")
                .productId(coreProd.getId())
                .status("PUBLISHED")
                .build());

        PlanVersion pvEnterprise = planVersionRepository.save(PlanVersion.builder()
                .planId(planEnterprise.getId())
                .versionNo(1)
                .pricePaise(799900L) // ₹7,999 / mo
                .billingCycle("MONTHLY")
                .minQuantity(1)
                .availableFrom(LocalDate.now().minusMonths(6))
                .allowPause(true)
                .allowRenewal(true)
                .createdBy(admin.getId())
                .build());

        planEnterprise.setCurrentVersionId(pvEnterprise.getId());
        planRepository.save(planEnterprise);

        planLimitRepository.saveAll(List.of(
                PlanLimit.builder().planVersionId(pvEnterprise.getId()).limitKey("projects").limitValue(-1L).build(),
                PlanLimit.builder().planVersionId(pvEnterprise.getId()).limitKey("storage_gb").limitValue(500L).build(),
                PlanLimit.builder().planVersionId(pvEnterprise.getId()).limitKey("custom_domains").limitValue(-1L).build(),
                PlanLimit.builder().planVersionId(pvEnterprise.getId()).limitKey("templates").limitValue(-1L).build()
        ));

        // 6. Discounts
        discountRepository.save(Discount.builder()
                .code("EARLYBIRD")
                .name("Early Adopter 20% Off")
                .discountType("PERCENT")
                .value(2000L) // 20%
                .scope("SUBSCRIPTION")
                .startsAt(OffsetDateTime.now().minusMonths(1))
                .endsAt(OffsetDateTime.now().plusMonths(6))
                .usageLimit(100)
                .isActive(true)
                .build());

        discountRepository.save(Discount.builder()
                .code("GROWTH500")
                .name("Growth Flat ₹500 Off")
                .discountType("FIXED")
                .value(50000L) // ₹500
                .scope("SUBSCRIPTION")
                .startsAt(OffsetDateTime.now().minusMonths(1))
                .endsAt(OffsetDateTime.now().plusMonths(6))
                .usageLimit(50)
                .isActive(true)
                .build());

        // 7. Subscriptions & Subscription Lines
        Subscription subAcme = subscriptionRepository.save(Subscription.builder()
                .number("SUB-2026-0001")
                .customerId(acme.getId())
                .planVersionId(pvPro.getId())
                .status("ACTIVE")
                .isPaused(false)
                .startDate(LocalDate.now().minusDays(20))
                .nextBillingDate(LocalDate.now().plusDays(10))
                .paymentTermsDays(30)
                .createdBy(custUser.getId())
                .build());

        subscriptionLineRepository.save(SubscriptionLine.builder()
                .subscriptionId(subAcme.getId())
                .productId(coreProd.getId())
                .quantity(1)
                .unitPricePaise(199900L)
                .taxRuleId(gst18.getId())
                .lineTotalPaise(199900L)
                .build());

        Subscription subStarlight = subscriptionRepository.save(Subscription.builder()
                .number("SUB-2026-0002")
                .customerId(starlight.getId())
                .planVersionId(pvPro.getId())
                .status("CONFIRMED")
                .isPaused(false)
                .startDate(LocalDate.now().minusDays(5))
                .nextBillingDate(LocalDate.now().plusDays(25))
                .paymentTermsDays(30)
                .createdBy(admin.getId())
                .build());

        Subscription subNexus = subscriptionRepository.save(Subscription.builder()
                .number("SUB-2026-0003")
                .customerId(nexus.getId())
                .planVersionId(pvEnterprise.getId())
                .status("ACTIVE")
                .isPaused(false)
                .startDate(LocalDate.now().minusDays(15))
                .nextBillingDate(LocalDate.now().plusDays(15))
                .paymentTermsDays(30)
                .createdBy(admin.getId())
                .build());

        Subscription subBlue = subscriptionRepository.save(Subscription.builder()
                .number("SUB-2026-0004")
                .customerId(bluewave.getId())
                .planVersionId(pvPro.getId())
                .status("CONFIRMED")
                .isPaused(false)
                .startDate(LocalDate.now().minusDays(2))
                .nextBillingDate(LocalDate.now().plusDays(28))
                .paymentTermsDays(30)
                .createdBy(admin.getId())
                .build());

        Subscription subVertex = subscriptionRepository.save(Subscription.builder()
                .number("SUB-2026-0005")
                .customerId(vertex.getId())
                .planVersionId(pvEnterprise.getId())
                .status("ACTIVE")
                .isPaused(false)
                .startDate(LocalDate.now().minusDays(12))
                .nextBillingDate(LocalDate.now().plusDays(18))
                .paymentTermsDays(30)
                .createdBy(admin.getId())
                .build());

        Subscription subQuantum = subscriptionRepository.save(Subscription.builder()
                .number("SUB-2026-0006")
                .customerId(quantum.getId())
                .planVersionId(pvStarter.getId())
                .status("DRAFT")
                .isPaused(false)
                .startDate(LocalDate.now())
                .nextBillingDate(LocalDate.now().plusDays(30))
                .paymentTermsDays(30)
                .createdBy(admin.getId())
                .build());

        // 8. Invoices matching Image 2 "Recent Activities"
        // INV_000071 - Acme Corp - Pro Subscription
        Invoice inv71 = invoiceRepository.save(Invoice.builder()
                .number("INV_000071")
                .subscriptionId(subAcme.getId())
                .customerId(acme.getId())
                .status("PAID")
                .periodStart(LocalDate.now().minusDays(20))
                .periodEnd(LocalDate.now().plusDays(10))
                .issueDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().plusDays(10))
                .currency("INR")
                .subtotalPaise(199900L)
                .discountPaise(0L)
                .taxPaise(35982L)
                .totalPaise(235882L)
                .amountPaidPaise(235882L)
                .build());

        invoiceLineRepository.save(InvoiceLine.builder()
                .invoiceId(inv71.getId())
                .productId(coreProd.getId())
                .description("Pro Tier Subscription - Monthly (Acme Corp)")
                .quantity(1)
                .unitPricePaise(199900L)
                .taxRuleId(gst18.getId())
                .taxRateBps(1800)
                .taxPaise(35982L)
                .lineTotalPaise(235882L)
                .build());

        paymentRepository.save(Payment.builder()
                .invoiceId(inv71.getId())
                .method("CARD")
                .amountPaise(235882L)
                .paidOn(LocalDate.now().minusDays(19))
                .reference("TXN_VISA_98124")
                .note("Paid via Corporate Visa Card")
                .recordedBy(admin.getId())
                .build());

        // INV_000072 - Starlight Media - Storage Addon
        Invoice inv72 = invoiceRepository.save(Invoice.builder()
                .number("INV_000072")
                .subscriptionId(subStarlight.getId())
                .customerId(starlight.getId())
                .status("CONFIRMED")
                .periodStart(LocalDate.now().minusDays(5))
                .periodEnd(LocalDate.now().plusDays(25))
                .issueDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(25))
                .currency("INR")
                .subtotalPaise(49900L)
                .discountPaise(0L)
                .taxPaise(8982L)
                .totalPaise(58882L)
                .amountPaidPaise(0L)
                .build());

        invoiceLineRepository.save(InvoiceLine.builder()
                .invoiceId(inv72.getId())
                .productId(storageAddon.getId())
                .description("Cloud Storage Booster 100GB (Starlight Media)")
                .quantity(1)
                .unitPricePaise(49900L)
                .taxRuleId(gst18.getId())
                .taxRateBps(1800)
                .taxPaise(8982L)
                .lineTotalPaise(58882L)
                .build());

        // INV_000073 - Nexus Innovations - Enterprise Tier
        Invoice inv73 = invoiceRepository.save(Invoice.builder()
                .number("INV_000073")
                .subscriptionId(subNexus.getId())
                .customerId(nexus.getId())
                .status("PAID")
                .periodStart(LocalDate.now().minusDays(15))
                .periodEnd(LocalDate.now().plusDays(15))
                .issueDate(LocalDate.now().minusDays(15))
                .dueDate(LocalDate.now().plusDays(15))
                .currency("INR")
                .subtotalPaise(799900L)
                .discountPaise(0L)
                .taxPaise(143982L)
                .totalPaise(943882L)
                .amountPaidPaise(943882L)
                .build());

        invoiceLineRepository.save(InvoiceLine.builder()
                .invoiceId(inv73.getId())
                .productId(coreProd.getId())
                .description("Enterprise Dedicated Cluster Tier (Nexus Innovations)")
                .quantity(1)
                .unitPricePaise(799900L)
                .taxRuleId(gst18.getId())
                .taxRateBps(1800)
                .taxPaise(143982L)
                .lineTotalPaise(943882L)
                .build());

        paymentRepository.save(Payment.builder()
                .invoiceId(inv73.getId())
                .method("UPI")
                .amountPaise(943882L)
                .paidOn(LocalDate.now().minusDays(14))
                .reference("UPI/NEXUS/202610")
                .note("HDFC UPI Direct Transfer")
                .recordedBy(admin.getId())
                .build());

        // INV_000074 - BlueWave Tech - Annual Pro License
        Invoice inv74 = invoiceRepository.save(Invoice.builder()
                .number("INV_000074")
                .subscriptionId(subBlue.getId())
                .customerId(bluewave.getId())
                .status("CONFIRMED")
                .periodStart(LocalDate.now().minusDays(2))
                .periodEnd(LocalDate.now().plusYears(1))
                .issueDate(LocalDate.now().minusDays(2))
                .dueDate(LocalDate.now().plusDays(28))
                .currency("INR")
                .subtotalPaise(1999000L)
                .discountPaise(0L)
                .taxPaise(359820L)
                .totalPaise(2358820L)
                .amountPaidPaise(0L)
                .build());

        invoiceLineRepository.save(InvoiceLine.builder()
                .invoiceId(inv74.getId())
                .productId(coreProd.getId())
                .description("Annual Pro License (BlueWave Tech)")
                .quantity(1)
                .unitPricePaise(1999000L)
                .taxRuleId(gst18.getId())
                .taxRateBps(1800)
                .taxPaise(359820L)
                .lineTotalPaise(2358820L)
                .build());

        // INV_000075 - Vertex Labs - Custom Consulting & Plan
        Invoice inv75 = invoiceRepository.save(Invoice.builder()
                .number("INV_000075")
                .subscriptionId(subVertex.getId())
                .customerId(vertex.getId())
                .status("PAID")
                .periodStart(LocalDate.now().minusDays(12))
                .periodEnd(LocalDate.now().plusDays(18))
                .issueDate(LocalDate.now().minusDays(12))
                .dueDate(LocalDate.now().plusDays(18))
                .currency("INR")
                .subtotalPaise(1450000L)
                .discountPaise(0L)
                .taxPaise(261000L)
                .totalPaise(1711000L)
                .amountPaidPaise(1711000L)
                .build());

        invoiceLineRepository.save(InvoiceLine.builder()
                .invoiceId(inv75.getId())
                .productId(coreProd.getId())
                .description("Custom SLA & Cloud Infrastructure (Vertex Labs)")
                .quantity(1)
                .unitPricePaise(1450000L)
                .taxRuleId(gst18.getId())
                .taxRateBps(1800)
                .taxPaise(261000L)
                .lineTotalPaise(1711000L)
                .build());

        paymentRepository.save(Payment.builder()
                .invoiceId(inv75.getId())
                .method("BANK_TRANSFER")
                .amountPaise(1711000L)
                .paidOn(LocalDate.now().minusDays(11))
                .reference("NEFT_VERTEX_8829")
                .note("Corporate RTGS Transfer")
                .recordedBy(admin.getId())
                .build());

        // INV_000076 - Quantum Dynamics - Team Seats (5x)
        Invoice inv76 = invoiceRepository.save(Invoice.builder()
                .number("INV_000076")
                .subscriptionId(subQuantum.getId())
                .customerId(quantum.getId())
                .status("DRAFT")
                .periodStart(LocalDate.now())
                .periodEnd(LocalDate.now().plusDays(30))
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .currency("INR")
                .subtotalPaise(249500L)
                .discountPaise(0L)
                .taxPaise(44910L)
                .totalPaise(294410L)
                .amountPaidPaise(0L)
                .build());

        invoiceLineRepository.save(InvoiceLine.builder()
                .invoiceId(inv76.getId())
                .productId(seatAddon.getId())
                .description("5x Team Collaborator Addon (Quantum Dynamics)")
                .quantity(5)
                .unitPricePaise(49900L)
                .taxRuleId(gst18.getId())
                .taxRateBps(1800)
                .taxPaise(44910L)
                .lineTotalPaise(294410L)
                .build());

        // 9. FAQ Articles for Chatbot
        faqArticleRepository.save(FaqArticle.builder()
                .category("Billing")
                .question("How do I upgrade or change my plan?")
                .answer("You can upgrade your subscription anytime from the Pricing Matrix tab. Upgrades take effect immediately with pro-rated billing for the current cycle.")
                .isPublished(true)
                .build());

        faqArticleRepository.save(FaqArticle.builder()
                .category("Billing")
                .question("What payment methods are accepted?")
                .answer("We support Credit/Debit Cards (Visa, Mastercard, RuPay), UPI, and Direct Corporate Bank Transfer (NEFT/RTGS) with automatic GST invoice generation.")
                .isPublished(true)
                .build());

        faqArticleRepository.save(FaqArticle.builder()
                .category("Subscriptions")
                .question("Can I pause my active subscription?")
                .answer("Yes! Pro and Enterprise plans permit pausing your subscription up to 90 days. You can pause or resume anytime in the Manage Subscriptions tab.")
                .isPublished(true)
                .build());

        faqArticleRepository.save(FaqArticle.builder()
                .category("Invoicing")
                .question("Where can I download my tax invoices?")
                .answer("All invoices with GST breakdown are available in the Activity and Manage tabs. Click any invoice row to view details and print or download your receipt.")
                .isPublished(true)
                .build());

        // 10. Sample Support Ticket
        supportTicketRepository.save(SupportTicket.builder()
                .number("TKT-1001")
                .customerId(acme.getId())
                .subject("Request for custom webhook signature verification")
                .status("OPEN")
                .priority("NORMAL")
                .assignedStaffId(staff.getId())
                .build());

        supportTicketRepository.save(SupportTicket.builder()
                .number("TKT-1002")
                .customerId(starlight.getId())
                .subject("GSTIN update on previous month tax credit note")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .assignedStaffId(staff.getId())
                .build());

        log.info("Database seeding successfully completed with 27-table aligned data!");
    }
}

