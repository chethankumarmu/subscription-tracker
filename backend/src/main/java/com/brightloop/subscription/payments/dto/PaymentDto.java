package com.brightloop.subscription.payments.dto;

import java.time.LocalDate;

public class PaymentDto {
    private Long id;
    private Long invoiceId;
    private String method;
    private Long amountPaise;
    private LocalDate paidOn;
    private String reference;
    private String note;

    public PaymentDto() {}

    public PaymentDto(Long id, Long invoiceId, String method, Long amountPaise, LocalDate paidOn, String reference, String note) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.method = method;
        this.amountPaise = amountPaise;
        this.paidOn = paidOn;
        this.reference = reference;
        this.note = note;
    }

    public Long getId() { return id; }
    public Long getInvoiceId() { return invoiceId; }
    public String getMethod() { return method; }
    public Long getAmountPaise() { return amountPaise; }
    public LocalDate getPaidOn() { return paidOn; }
    public String getReference() { return reference; }
    public String getNote() { return note; }
}

