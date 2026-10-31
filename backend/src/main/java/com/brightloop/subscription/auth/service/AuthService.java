package com.brightloop.subscription.auth.service;

import com.brightloop.subscription.auth.dto.*;
import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.PasswordReset;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.PasswordResetRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.auth.security.JwtService;
import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.auth.util.PasswordValidator;
import com.brightloop.subscription.notifications.entity.EmailOutbox;
import com.brightloop.subscription.notifications.repository.EmailOutboxRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final EmailOutboxRepository emailOutboxRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String baseUrl;

    public AuthService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PasswordResetRepository passwordResetRepository,
            EmailOutboxRepository emailOutboxRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.emailOutboxRepository = emailOutboxRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public UserProfileResponse register(RegisterRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        String cleanEmail = req.getEmail().trim().toLowerCase();

        if (!PasswordValidator.isValid(req.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be more than 8 characters with at least one uppercase, one lowercase, and one special character");
        }

        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        OffsetDateTime now = OffsetDateTime.now();
        String fullName = (req.getFullName() != null && !req.getFullName().isBlank())
                ? req.getFullName().trim()
                : "Customer";

        User user = new User(
                null,
                cleanEmail,
                passwordEncoder.encode(req.getPassword()),
                fullName,
                "CUSTOMER",
                true,
                null,
                now,
                now
        );
        User savedUser = userRepository.save(user);

        String contactName = (req.getContactName() != null && !req.getContactName().isBlank())
                ? req.getContactName().trim()
                : fullName;

        String companyName = (req.getCompanyName() != null && !req.getCompanyName().isBlank())
                ? req.getCompanyName().trim()
                : contactName;

        Customer customer = new Customer(
                null,
                savedUser,
                companyName,
                contactName,
                req.getPhone(),
                req.getBillingAddress(),
                req.getGstin(),
                "ACTIVE",
                now,
                now
        );
        Customer savedCustomer = customerRepository.save(customer);

        return new UserProfileResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole(),
                savedCustomer.getId()
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findByEmailIgnoreCase(req.getEmail().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        Long customerId = null;
        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            customerId = customerRepository.findByUserId(user.getId())
                    .map(Customer::getId)
                    .orElse(null);
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                customerId
        );
        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                customerId
        );

        return new LoginResponse(accessToken, refreshToken, user.getRole(), customerId);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(RefreshTokenRequest req) {
        if (req.getRefreshToken() == null || req.getRefreshToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is required");
        }

        Claims claims = jwtService.parseAndValidateToken(req.getRefreshToken());
        if (claims == null || !"REFRESH".equals(claims.get("type", String.class))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        Long userId = Long.valueOf(claims.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled");
        }

        Long customerId = null;
        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            customerId = customerRepository.findByUserId(user.getId())
                    .map(Customer::getId)
                    .orElse(null);
        }

        String newAccessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                customerId
        );

        return new TokenResponse(newAccessToken);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            return;
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(req.getEmail().trim());
        if (userOpt.isEmpty()) {
            // Always return silently with 200 for unknown emails
            return;
        }

        User user = userOpt.get();
        String rawToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        String tokenHash = sha256Hex(rawToken);

        PasswordReset reset = PasswordReset.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(OffsetDateTime.now().plusMinutes(30))
                .createdAt(OffsetDateTime.now())
                .build();
        passwordResetRepository.save(reset);

        String resetLink = baseUrl + "/reset-password?token=" + rawToken;
        String emailBody = "<p>Hello " + user.getFullName() + ",</p>"
                + "<p>You requested a password reset. Click the link below to set a new password:</p>"
                + "<p><a href=\"" + resetLink + "\">" + resetLink + "</a></p>"
                + "<p>This link expires in 30 minutes. If you did not request this, please ignore this email.</p>";

        EmailOutbox email = EmailOutbox.builder()
                .toAddress(user.getEmail())
                .subject("Reset your Brightloop password")
                .body(emailBody)
                .templateKey("PASSWORD_RESET")
                .status("PENDING")
                .attempts(0)
                .nextAttemptAt(OffsetDateTime.now())
                .build();
        emailOutboxRepository.save(email);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        if (req.getToken() == null || req.getToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
        }

        if (!PasswordValidator.isValid(req.getNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be more than 8 characters with at least one uppercase, one lowercase, and one special character");
        }

        String tokenHash = sha256Hex(req.getToken().trim());
        PasswordReset reset = passwordResetRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token"));

        if (reset.getUsedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has already been used");
        }

        if (reset.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        User user = reset.getUser();
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);

        reset.setUsedAt(OffsetDateTime.now());
        passwordResetRepository.save(reset);
    }

    @Transactional
    public UserProfileResponse createStaff(CreateStaffRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        String cleanEmail = req.getEmail().trim().toLowerCase();

        if (!PasswordValidator.isValid(req.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be more than 8 characters with at least one uppercase, one lowercase, and one special character");
        }

        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        OffsetDateTime now = OffsetDateTime.now();
        String fullName = (req.getFullName() != null && !req.getFullName().isBlank())
                ? req.getFullName().trim()
                : "Staff Member";

        User user = new User(
                null,
                cleanEmail,
                passwordEncoder.encode(req.getPassword()),
                fullName,
                "STAFF",
                true,
                null,
                now,
                now
        );
        User savedUser = userRepository.save(user);

        return new UserProfileResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole(),
                null
        );
    }

    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}

