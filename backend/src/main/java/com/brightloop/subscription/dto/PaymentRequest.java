package com.brightloop.subscription.dto;

public class PaymentRequest {
    private Long invoiceId;
    private Long amountPaise;
    private String method;
    private String reference;
    private String note;

    public PaymentRequest() {}

    public PaymentRequest(Long invoiceId, Long amountPaise, String method, String reference, String note) {
        this.invoiceId = invoiceId;
        this.amountPaise = amountPaise;
        this.method = method;
        this.reference = reference;
        this.note = note;
    }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public Long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(Long amountPaise) { this.amountPaise = amountPaise; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
