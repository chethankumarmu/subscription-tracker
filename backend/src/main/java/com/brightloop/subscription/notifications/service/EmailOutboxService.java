package com.brightloop.subscription.notifications.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.entity.InvoiceLine;
import com.brightloop.subscription.common.util.MoneyUtils;
import com.brightloop.subscription.notifications.entity.EmailOutbox;
import com.brightloop.subscription.notifications.repository.EmailOutboxRepository;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailOutboxService {

    private final EmailOutboxRepository emailOutboxRepository;
    private final TemplateEngine templateEngine;
    private final String baseUrl;

    public EmailOutboxService(
            EmailOutboxRepository emailOutboxRepository,
            TemplateEngine templateEngine,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.emailOutboxRepository = emailOutboxRepository;
        this.templateEngine = templateEngine;
        this.baseUrl = baseUrl;
    }

    /**
     * Queues an email for a generated invoice with the invoice PDF attached.
     */
    @Transactional
    public EmailOutbox queueInvoiceEmail(Invoice invoice) {
        Customer customer = invoice.getCustomer();
        String recipientEmail = getCustomerEmail(customer);

        Context context = new Context();
        context.setVariable("invoiceNumber", invoice.getNumber());
        context.setVariable("customerName", customer != null ? customer.getContactName() : "Valued Customer");
        context.setVariable("companyName", customer != null ? customer.getCompanyName() : "");
        context.setVariable("billingAddress", customer != null && customer.getBillingAddress() != null ? customer.getBillingAddress() : "N/A");
        context.setVariable("issueDate", invoice.getIssueDate());
        context.setVariable("dueDate", invoice.getDueDate());
        context.setVariable("subscriptionNumber", invoice.getSubscription().getNumber());
        context.setVariable("paymentStatus", invoice.getStatus());

        List<InvoiceItemViewModel> items = new ArrayList<>();
        if (invoice.getLines() != null && !invoice.getLines().isEmpty()) {
            for (InvoiceLine line : invoice.getLines()) {
                items.add(new InvoiceItemViewModel(
                        line.getDescription(),
                        line.getQuantity(),
                        MoneyUtils.formatPaiseToRupees(line.getUnitPricePaise()),
                        MoneyUtils.formatPaiseToRupees(line.getTaxPaise()),
                        MoneyUtils.formatPaiseToRupees(line.getLineTotalPaise())
                ));
            }
        } else {
            items.add(new InvoiceItemViewModel(
                    "Subscription Service - " + invoice.getSubscription().getNumber(),
                    1,
                    MoneyUtils.formatPaiseToRupees(invoice.getSubtotalPaise()),
                    MoneyUtils.formatPaiseToRupees(invoice.getTaxPaise()),
                    MoneyUtils.formatPaiseToRupees(invoice.getTotalPaise())
            ));
        }

        context.setVariable("items", items);
        context.setVariable("subtotalFormatted", MoneyUtils.formatPaiseToRupees(invoice.getSubtotalPaise()));
        context.setVariable("discountFormatted", MoneyUtils.formatPaiseToRupees(invoice.getDiscountPaise()));
        context.setVariable("taxFormatted", MoneyUtils.formatPaiseToRupees(invoice.getTaxPaise()));
        context.setVariable("totalFormatted", MoneyUtils.formatPaiseToRupees(invoice.getTotalPaise()));

        String htmlBody = templateEngine.process("email/invoice", context);

        EmailOutbox outbox = EmailOutbox.builder()
                .toAddress(recipientEmail)
                .subject("Brightloop Invoice: " + invoice.getNumber())
                .body(htmlBody)
                .templateKey("INVOICE")
                .status("PENDING")
                .attempts(0)
                .nextAttemptAt(OffsetDateTime.now())
                .attachmentInvoiceId(invoice.getId())
                .attachmentName(invoice.getNumber() + ".pdf")
                .build();

        return emailOutboxRepository.save(outbox);
    }

    /**
     * Queues a renewal reminder email.
     */
    @Transactional
    public EmailOutbox queueRenewalReminderEmail(Subscription subscription, int daysLeft) {
        Customer customer = subscription.getCustomer();
        String recipientEmail = getCustomerEmail(customer);
        String planName = subscription.getPlanVersion() != null && subscription.getPlanVersion().getPlan() != null
                ? subscription.getPlanVersion().getPlan().getName()
                : "Subscription Plan";

        Context context = new Context();
        context.setVariable("subscriptionNumber", subscription.getNumber());
        context.setVariable("planName", planName);
        context.setVariable("expiryDate", subscription.getExpiryDate());
        context.setVariable("daysLeft", daysLeft);
        context.setVariable("renewUrl", baseUrl + "/renew/" + subscription.getId());

        String htmlBody = templateEngine.process("email/renewal-reminder", context);

        EmailOutbox outbox = EmailOutbox.builder()
                .toAddress(recipientEmail)
                .subject("Renewal Reminder: " + subscription.getNumber() + " expires in " + daysLeft + " day(s)")
                .body(htmlBody)
                .templateKey("RENEWAL_REMINDER")
                .status("PENDING")
                .attempts(0)
                .nextAttemptAt(OffsetDateTime.now())
                .attachmentInvoiceId(null)
                .attachmentName(null)
                .build();

        return emailOutboxRepository.save(outbox);
    }

    private String getCustomerEmail(Customer customer) {
        if (customer != null && customer.getUser() != null && customer.getUser().getEmail() != null) {
            return customer.getUser().getEmail();
        }
        return "cust@brightloop.test";
    }

    public record InvoiceItemViewModel(
            String description,
            Integer quantity,
            String unitPriceFormatted,
            String taxFormatted,
            String amountFormatted
    ) {
    }
}
