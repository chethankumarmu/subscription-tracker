package com.brightloop.subscription.auth.dto;

public class UserProfileResponse {
    private Long userId;
    private String email;
    private String name;
    private String role;
    private Long customerId;

    public UserProfileResponse() {}

    public UserProfileResponse(Long userId, String email, String name, String role, Long customerId) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.customerId = customerId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}

