package com.brightloop.subscription.notifications.worker;

import com.brightloop.subscription.notifications.entity.EmailOutbox;
import com.brightloop.subscription.notifications.repository.EmailOutboxRepository;
import com.brightloop.subscription.notifications.service.InvoicePdfService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class EmailOutboxWorker {

    private static final Logger log = LoggerFactory.getLogger(EmailOutboxWorker.class);

    private final EmailOutboxRepository emailOutboxRepository;
    private final JavaMailSender mailSender;
    private final InvoicePdfService invoicePdfService;
    private final String mailFrom;

    public EmailOutboxWorker(
            EmailOutboxRepository emailOutboxRepository,
            JavaMailSender mailSender,
            InvoicePdfService invoicePdfService,
            @Value("${app.mail.from:1ms24cs052@msrit.edu}") String mailFrom
    ) {
        this.emailOutboxRepository = emailOutboxRepository;
        this.mailSender = mailSender;
        this.invoicePdfService = invoicePdfService;
        this.mailFrom = mailFrom;
    }

    /**
     * Periodically process up to 20 pending emails and send them via SMTP.
     */
    @Scheduled(fixedDelay = 30000)
    public void processPendingEmails() {
        OffsetDateTime now = OffsetDateTime.now();
        List<EmailOutbox> pendingEmails = emailOutboxRepository.findPendingEmails("PENDING", now, PageRequest.of(0, 20));

        if (pendingEmails.isEmpty()) {
            return;
        }

        log.info("Processing {} pending email(s) from outbox", pendingEmails.size());

        for (EmailOutbox email : pendingEmails) {
            sendEmail(email);
        }
    }

    public void sendEmail(EmailOutbox email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(email.getToAddress());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true); // html = true

            if (email.getAttachmentInvoiceId() != null) {
                byte[] pdfBytes = invoicePdfService.generateInvoicePdf(email.getAttachmentInvoiceId());
                String attachmentName = email.getAttachmentName() != null ? email.getAttachmentName() : "invoice.pdf";
                helper.addAttachment(attachmentName, new ByteArrayResource(pdfBytes), "application/pdf");
            }

            mailSender.send(message);

            email.setStatus("SENT");
            email.setSentAt(OffsetDateTime.now());
            emailOutboxRepository.save(email);
            log.info("Email id={} sent successfully to {}", email.getId(), email.getToAddress());

        } catch (Exception ex) {
            log.error("Failed to send email id={}: {}", email.getId(), ex.getMessage());
            int attempts = email.getAttempts() + 1;
            email.setAttempts(attempts);
            email.setLastError(ex.getMessage() != null ? ex.getMessage() : ex.toString());

            if (attempts >= 3) {
                email.setStatus("FAILED");
            } else {
                email.setNextAttemptAt(OffsetDateTime.now().plusSeconds(30L * attempts));
            }
            emailOutboxRepository.save(email);
        }
    }
}
