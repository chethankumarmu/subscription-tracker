package com.brightloop.subscription.chatbot.dto;

import java.time.LocalDate;

public class InvoiceFact {
    private String number;
    private String status;
    private Long totalPaise;
    private String totalFormatted;
    private Long amountPaidPaise;
    private Long remainingPaise;
    private LocalDate dueDate;
    private LocalDate issueDate;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String currency;

    public InvoiceFact() {}

    public InvoiceFact(String number, String status, Long totalPaise, String totalFormatted, Long amountPaidPaise, Long remainingPaise, LocalDate dueDate, LocalDate issueDate, LocalDate periodStart, LocalDate periodEnd, String currency) {
        this.number = number;
        this.status = status;
        this.totalPaise = totalPaise;
        this.totalFormatted = totalFormatted;
        this.amountPaidPaise = amountPaidPaise;
        this.remainingPaise = remainingPaise;
        this.dueDate = dueDate;
        this.issueDate = issueDate;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.currency = currency;
    }

    public static InvoiceFactBuilder builder() {
        return new InvoiceFactBuilder();
    }

    public static class InvoiceFactBuilder {
        private String number;
        private String status;
        private Long totalPaise;
        private String totalFormatted;
        private Long amountPaidPaise;
        private Long remainingPaise;
        private LocalDate dueDate;
        private LocalDate issueDate;
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private String currency;

        public InvoiceFactBuilder number(String number) { this.number = number; return this; }
        public InvoiceFactBuilder status(String status) { this.status = status; return this; }
        public InvoiceFactBuilder totalPaise(Long totalPaise) { this.totalPaise = totalPaise; return this; }
        public InvoiceFactBuilder totalFormatted(String totalFormatted) { this.totalFormatted = totalFormatted; return this; }
        public InvoiceFactBuilder amountPaidPaise(Long amountPaidPaise) { this.amountPaidPaise = amountPaidPaise; return this; }
        public InvoiceFactBuilder remainingPaise(Long remainingPaise) { this.remainingPaise = remainingPaise; return this; }
        public InvoiceFactBuilder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public InvoiceFactBuilder issueDate(LocalDate issueDate) { this.issueDate = issueDate; return this; }
        public InvoiceFactBuilder periodStart(LocalDate periodStart) { this.periodStart = periodStart; return this; }
        public InvoiceFactBuilder periodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; return this; }
        public InvoiceFactBuilder currency(String currency) { this.currency = currency; return this; }

        public InvoiceFact build() {
            return new InvoiceFact(number, status, totalPaise, totalFormatted, amountPaidPaise, remainingPaise, dueDate, issueDate, periodStart, periodEnd, currency);
        }
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getTotalPaise() { return totalPaise; }
    public void setTotalPaise(Long totalPaise) { this.totalPaise = totalPaise; }
    public String getTotalFormatted() { return totalFormatted; }
    public void setTotalFormatted(String totalFormatted) { this.totalFormatted = totalFormatted; }
    public Long getAmountPaidPaise() { return amountPaidPaise; }
    public void setAmountPaidPaise(Long amountPaidPaise) { this.amountPaidPaise = amountPaidPaise; }
    public Long getRemainingPaise() { return remainingPaise; }
    public void setRemainingPaise(Long remainingPaise) { this.remainingPaise = remainingPaise; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
