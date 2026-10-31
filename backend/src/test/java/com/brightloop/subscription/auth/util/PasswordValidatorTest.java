package com.brightloop.subscription.auth.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void shouldAcceptValidPasswords() {
        // More than 8 characters, >= 1 uppercase, >= 1 lowercase, >= 1 special character
        assertTrue(PasswordValidator.isValid("StrongP@ss1"));
        assertTrue(PasswordValidator.isValid("MySecure#Password9"));
        assertTrue(PasswordValidator.isValid("Abcdefgh!"));
        assertTrue(PasswordValidator.isValid("12345aB#xyz"));
    }

    @Test
    void shouldRejectInvalidPasswords() {
        // Less than or equal to 8 characters
        assertFalse(PasswordValidator.isValid("Pass!123")); // exactly 8 chars
        assertFalse(PasswordValidator.isValid("P@ss1"));    // 5 chars
        assertFalse(PasswordValidator.isValid(null));
        assertFalse(PasswordValidator.isValid(""));

        // Missing uppercase
        assertFalse(PasswordValidator.isValid("strongp@ss123"));

        // Missing lowercase
        assertFalse(PasswordValidator.isValid("STRONGP@SS123"));

        // Missing special character
        assertFalse(PasswordValidator.isValid("StrongPass1234"));
    }
}

