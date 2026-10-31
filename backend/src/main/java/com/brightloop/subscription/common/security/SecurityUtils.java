package com.brightloop.subscription.common.security;

import com.brightloop.subscription.auth.entity.Role;
import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.common.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<AuthenticatedUser> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getPrincipal() instanceof AuthenticatedUser user) {
                return Optional.of(user);
            }
            if (auth.getPrincipal() instanceof UserPrincipal up) {
                Role role = null;
                try {
                    role = Role.valueOf(up.getRole().toUpperCase());
                } catch (Exception ignored) {
                }
                return Optional.of(new AuthenticatedUser(
                        up.getUserId(),
                        up.getEmail(),
                        role,
                        up.getCustomerId()
                ));
            }
        }
        return Optional.empty();
    }

    public static AuthenticatedUser requireAuthenticatedUser() {
        return getAuthenticatedUser()
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated."));
    }

    public static Long requireCurrentCustomerId() {
        AuthenticatedUser user = requireAuthenticatedUser();
        if (user.getRole() != Role.CUSTOMER) {
            throw new AccessDeniedException("Operation restricted exclusively to authenticated CUSTOMER accounts.");
        }
        if (user.getCustomerId() == null) {
            throw new AccessDeniedException("Authenticated customer profile does not exist.");
        }
        return user.getCustomerId();
    }

    public static Long getCurrentUserId() {
        return getAuthenticatedUser().map(AuthenticatedUser::getUserId).orElse(null);
    }

    public static Role getCurrentRole() {
        return getAuthenticatedUser().map(AuthenticatedUser::getRole).orElse(null);
    }
}
