package com.brightloop.subscription.common.security;

import com.brightloop.subscription.auth.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AuthenticatedUser implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final Role role;
    private final Long customerId;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(Long userId, String email, String password, Role role, Long customerId, boolean active) {
        this.userId = userId;
        this.email = email;
        this.password = password != null ? password : "";
        this.role = role;
        this.customerId = customerId;
        this.active = active;
        this.authorities = role != null 
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
                : Collections.emptyList();
    }

    public AuthenticatedUser(Long userId, String email, Role role, Long customerId) {
        this(userId, email, "", role, customerId, true);
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public Long getCustomerId() { return customerId; }
    public boolean isActive() { return active; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return active; }
}
