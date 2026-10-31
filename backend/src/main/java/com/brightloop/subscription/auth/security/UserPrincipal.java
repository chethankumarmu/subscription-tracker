package com.brightloop.subscription.auth.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String email;
    private final String fullName;
    private final String role;
    private final Long customerId;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long userId, String email, String fullName, String role, Long customerId) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role != null ? role.toUpperCase() : "CUSTOMER";
        this.customerId = ("ADMIN".equalsIgnoreCase(role) || "STAFF".equalsIgnoreCase(role)) ? null : customerId;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public Long getCustomerId() {
        return customerId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

