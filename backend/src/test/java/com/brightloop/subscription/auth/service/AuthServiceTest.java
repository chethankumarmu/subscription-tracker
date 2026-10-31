package com.brightloop.subscription.auth.service;

import com.brightloop.subscription.auth.dto.*;
import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.PasswordReset;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.PasswordResetRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.auth.security.JwtService;
import com.brightloop.subscription.notifications.entity.EmailOutbox;
import com.brightloop.subscription.notifications.repository.EmailOutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private EmailOutboxRepository emailOutboxRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                customerRepository,
                passwordResetRepository,
                emailOutboxRepository,
                passwordEncoder,
                jwtService,
                "http://localhost:8080"
        );
    }

    @Test
    void register_shouldThrowConflict_whenEmailAlreadyExistsCaseInsensitive() {
        String existingEmail = "john.doe@example.com";
        when(userRepository.existsByEmailIgnoreCase(eq(existingEmail.toLowerCase()))).thenReturn(true);

        RegisterRequest request = new RegisterRequest(
                "JOHN.DOE@EXAMPLE.COM",
                "StrongP@ssword123",
                "John Doe",
                "Acme Corp",
                "John Doe",
                "1234567890",
                "{}",
                "GST123"
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().contains("already registered"));

        verify(userRepository, never()).save(any(User.class));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void register_shouldThrowBadRequest_whenPasswordIsInvalid() {
        RegisterRequest request = new RegisterRequest(
                "newuser@example.com",
                "weakpass", // invalid password
                "New User",
                "Acme Corp",
                "New User",
                "1234567890",
                "{}",
                "GST123"
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldSucceed_whenValidDataProvided() {
        RegisterRequest request = new RegisterRequest(
                "newcustomer@example.com",
                "ValidP@ssword123",
                "Jane Doe",
                "Tech Solutions",
                "Jane Doe",
                "9876543210",
                "{\"city\":\"Bengaluru\"}",
                "29ABCDE1234F1Z5"
        );

        when(userRepository.existsByEmailIgnoreCase("newcustomer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("ValidP@ssword123")).thenReturn("encodedPasswordHash");

        User savedUser = new User(
                10L,
                "newcustomer@example.com",
                "encodedPasswordHash",
                "Jane Doe",
                "CUSTOMER",
                true,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Customer savedCustomer = new Customer(
                100L,
                savedUser,
                "Tech Solutions",
                "Jane Doe",
                "9876543210",
                "{\"city\":\"Bengaluru\"}",
                "29ABCDE1234F1Z5",
                "ACTIVE",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        UserProfileResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(10L, response.getUserId());
        assertEquals("newcustomer@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals(100L, response.getCustomerId());
    }

    @Test
    void forgotPassword_shouldReturnSilentlyWithoutException_whenEmailIsUnknown() {
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        ForgotPasswordRequest request = new ForgotPasswordRequest("unknown@example.com");

        assertDoesNotThrow(() -> authService.forgotPassword(request));

        verify(passwordResetRepository, never()).save(any(PasswordReset.class));
        verify(emailOutboxRepository, never()).save(any(EmailOutbox.class));
    }

    @Test
    void forgotPassword_shouldQueueEmailAndSaveResetToken_whenEmailExists() {
        User user = new User(
                5L,
                "existing@example.com",
                "hash",
                "Existing User",
                "CUSTOMER",
                true,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        when(userRepository.findByEmailIgnoreCase("existing@example.com")).thenReturn(Optional.of(user));

        ForgotPasswordRequest request = new ForgotPasswordRequest("existing@example.com");

        authService.forgotPassword(request);

        ArgumentCaptor<PasswordReset> resetCaptor = ArgumentCaptor.forClass(PasswordReset.class);
        verify(passwordResetRepository).save(resetCaptor.capture());
        PasswordReset savedReset = resetCaptor.getValue();
        assertEquals(5L, savedReset.getUser().getId());
        assertNotNull(savedReset.getTokenHash());
        assertEquals(64, savedReset.getTokenHash().length());
        assertTrue(savedReset.getExpiresAt().isAfter(OffsetDateTime.now()));

        ArgumentCaptor<EmailOutbox> emailCaptor = ArgumentCaptor.forClass(EmailOutbox.class);
        verify(emailOutboxRepository).save(emailCaptor.capture());
        EmailOutbox savedEmail = emailCaptor.getValue();
        assertEquals("existing@example.com", savedEmail.getToAddress());
        assertEquals("PASSWORD_RESET", savedEmail.getTemplateKey());
        assertTrue(savedEmail.getBody().contains("/reset-password?token="));
    }
}

