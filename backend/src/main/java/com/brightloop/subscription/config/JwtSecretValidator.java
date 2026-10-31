package com.brightloop.subscription.config;

import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JwtSecretValidator {

    private final Environment environment;

    public JwtSecretValidator(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validateJwtSecret() {
        String secret = null;
        try {
            secret = environment.getProperty("app.jwt.secret");
        } catch (Exception e) {
            // Placeholder cannot be resolved if JWT_SECRET is unset
        }

        if (secret == null || secret.isBlank()) {
            secret = environment.getProperty("JWT_SECRET");
        }

        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("Startup failed: JWT_SECRET environment variable is missing. It must be provided and be at least 32 bytes (256 bits) long.");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("Startup failed: JWT_SECRET is too short (" + secretBytes.length + " bytes). It must be at least 32 bytes (256 bits) long for secure HMAC-SHA signing.");
        }
    }
}

