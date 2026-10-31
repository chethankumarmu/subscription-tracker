package com.brightloop.subscription.notifications.worker;

import com.brightloop.subscription.notifications.entity.EmailOutbox;
import com.brightloop.subscription.notifications.repository.EmailOutboxRepository;
import com.brightloop.subscription.notifications.service.InvoicePdfService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailOutboxWorkerTest {

    @Mock
    private EmailOutboxRepository emailOutboxRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private InvoicePdfService invoicePdfService;

    private EmailOutboxWorker worker;

    @BeforeEach
    void setUp() {
        worker = new EmailOutboxWorker(
                emailOutboxRepository,
                mailSender,
                invoicePdfService,
                "noreply@brightloop.com"
        );
    }

    @Test
    void testWorkerMarksFailedAfterThreeAttempts_OnSendFailure() {
        // Email that has already failed 2 times
        EmailOutbox email = EmailOutbox.builder()
                .id(10L)
                .toAddress("cust@example.com")
                .subject("Test Invoice")
                .body("<p>Invoice Body</p>")
                .status("PENDING")
                .attempts(2)
                .build();

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("SMTP connection refused"))
                .when(mailSender).send(any(MimeMessage.class));

        // Execute send attempt
        worker.sendEmail(email);

        // Verify attempts incremented to 3 and status set to FAILED
        assertEquals(3, email.getAttempts());
        assertEquals("FAILED", email.getStatus());
        assertNotNull(email.getLastError());
        assertTrue(email.getLastError().contains("SMTP connection refused"));

        ArgumentCaptor<EmailOutbox> captor = ArgumentCaptor.forClass(EmailOutbox.class);
        verify(emailOutboxRepository).save(captor.capture());
        EmailOutbox saved = captor.getValue();
        assertEquals("FAILED", saved.getStatus());
        assertEquals(3, saved.getAttempts());
    }

    @Test
    void testWorkerIncrementsAttemptsAndSchedulesNextAttempt_WhenAttemptsUnderThree() {
        // Email on its first attempt (attempts = 0)
        EmailOutbox email = EmailOutbox.builder()
                .id(11L)
                .toAddress("cust2@example.com")
                .subject("Test 1st attempt")
                .body("<p>Hello</p>")
                .status("PENDING")
                .attempts(0)
                .build();

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Temporary network timeout"))
                .when(mailSender).send(any(MimeMessage.class));

        worker.sendEmail(email);

        assertEquals(1, email.getAttempts());
        assertNotEquals("FAILED", email.getStatus());
        assertNotNull(email.getNextAttemptAt());
        verify(emailOutboxRepository).save(email);
    }

    @Test
    void testWorkerMarksSentOnSuccess_WithDynamicPdfAttachment() {
        Long invoiceId = 55L;
        EmailOutbox email = EmailOutbox.builder()
                .id(12L)
                .toAddress("customer@brightloop.test")
                .subject("Invoice Attached")
                .body("<p>Please find attached invoice.</p>")
                .attachmentInvoiceId(invoiceId)
                .attachmentName("INV-2026-000055.pdf")
                .status("PENDING")
                .attempts(0)
                .build();

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(invoicePdfService.generateInvoicePdf(invoiceId)).thenReturn(new byte[]{1, 2, 3, 4});

        worker.sendEmail(email);

        // Verify email marked SENT
        assertEquals("SENT", email.getStatus());
        assertNotNull(email.getSentAt());

        // Verify PDF was generated dynamically at send time and not read from DB
        verify(invoicePdfService, times(1)).generateInvoicePdf(invoiceId);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(emailOutboxRepository).save(email);
    }
}

