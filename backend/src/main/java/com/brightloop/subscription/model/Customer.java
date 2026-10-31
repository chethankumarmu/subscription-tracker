package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(length = 50)
    private String phone;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "billing_address", columnDefinition = "jsonb")
    private String billingAddress;

    @Column(length = 50)
    private String gstin;

    @Column(nullable = false, length = 50)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Customer() {}

    public Customer(Long id, Long userId, String companyName, String contactName, String phone, String billingAddress, String gstin, String status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.contactName = contactName;
        this.phone = phone;
        this.billingAddress = billingAddress;
        this.gstin = gstin;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    public static class CustomerBuilder {
        private Long id;
        private Long userId;
        private String companyName;
        private String contactName;
        private String phone;
        private String billingAddress;
        private String gstin;
        private String status = "ACTIVE";
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public CustomerBuilder id(Long id) { this.id = id; return this; }
        public CustomerBuilder userId(Long userId) { this.userId = userId; return this; }
        public CustomerBuilder companyName(String companyName) { this.companyName = companyName; return this; }
        public CustomerBuilder contactName(String contactName) { this.contactName = contactName; return this; }
        public CustomerBuilder phone(String phone) { this.phone = phone; return this; }
        public CustomerBuilder billingAddress(String billingAddress) { this.billingAddress = billingAddress; return this; }
        public CustomerBuilder gstin(String gstin) { this.gstin = gstin; return this; }
        public CustomerBuilder status(String status) { this.status = status; return this; }
        public CustomerBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public CustomerBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Customer build() {
            return new Customer(id, userId, companyName, contactName, phone, billingAddress, gstin, status, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
