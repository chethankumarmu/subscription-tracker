package com.brightloop.subscription.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.*;

class JwtSecretValidatorTest {

    @Test
    void shouldFailWhenJwtSecretIsMissing() {
        MockEnvironment env = new MockEnvironment();
        JwtSecretValidator validator = new JwtSecretValidator(env);

        IllegalStateException ex = assertThrows(IllegalStateException.class, validator::validateJwtSecret);
        assertTrue(ex.getMessage().contains("JWT_SECRET environment variable is missing"));
    }

    @Test
    void shouldFailWhenJwtSecretIsShorterThan32Bytes() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("app.jwt.secret", "short-secret-under-32-bytes");
        JwtSecretValidator validator = new JwtSecretValidator(env);

        IllegalStateException ex = assertThrows(IllegalStateException.class, validator::validateJwtSecret);
        assertTrue(ex.getMessage().contains("JWT_SECRET is too short"));
    }

    @Test
    void shouldSucceedWhenJwtSecretIsAtLeast32Bytes() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("app.jwt.secret", "a-very-long-and-secure-jwt-secret-key-32-bytes!");
        JwtSecretValidator validator = new JwtSecretValidator(env);

        assertDoesNotThrow(validator::validateJwtSecret);
    }
}

