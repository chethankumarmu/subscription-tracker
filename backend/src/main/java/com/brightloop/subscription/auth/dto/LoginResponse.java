package com.brightloop.subscription.auth.dto;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String role;
    private Long customerId;

    public LoginResponse() {}

    public LoginResponse(String accessToken, String refreshToken, String role, Long customerId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.customerId = customerId;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}

