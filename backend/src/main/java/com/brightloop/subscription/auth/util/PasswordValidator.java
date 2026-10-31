package com.brightloop.subscription.auth.util;

public final class PasswordValidator {

    private PasswordValidator() {}

    public static boolean isValid(String password) {
        if (password == null || password.length() <= 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        return hasUpper && hasLower && hasSpecial;
    }
}

