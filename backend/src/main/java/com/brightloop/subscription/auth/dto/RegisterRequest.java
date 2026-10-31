package com.brightloop.subscription.auth.dto;

public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String companyName;
    private String contactName;
    private String phone;
    private String billingAddress;
    private String gstin;

    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String fullName, String companyName, String contactName, String phone, String billingAddress, String gstin) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.companyName = companyName;
        this.contactName = contactName;
        this.phone = phone;
        this.billingAddress = billingAddress;
        this.gstin = gstin;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
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
}

