package com.brightloop.subscription.auth.dto;

import java.time.OffsetDateTime;

public class CustomerDto {
    private Long id;
    private Long userId;
    private String companyName;
    private String contactName;
    private String phone;
    private String billingAddress;
    private String gstin;
    private String status;
    private OffsetDateTime createdAt;

    public CustomerDto() {}

    public CustomerDto(Long id, Long userId, String companyName, String contactName, String phone, String billingAddress, String gstin, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.contactName = contactName;
        this.phone = phone;
        this.billingAddress = billingAddress;
        this.gstin = gstin;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getCompanyName() { return companyName; }
    public String getContactName() { return contactName; }
    public String getPhone() { return phone; }
    public String getBillingAddress() { return billingAddress; }
    public String getGstin() { return gstin; }
    public String getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}

