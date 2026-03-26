package com.leandre.validation;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Centralized regex validation using Pattern, Matcher and Predicates.
 * Used by InputValidator for validating user input with consistent error messages.
 */
public class ValidationUtils {

    // ── Patterns (compiled once, reused) ─────────────────────────────────

    // Account number: ACC- followed by exactly 3 digits (e.g. ACC-001, ACC-999)
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^ACC-\\d{3}$");

    // Email: standard format (e.g. user@example.com, user.name+tag@domain.co)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // ── Predicates (functional-style validation) ─────────────────────────

    // Predicate that tests if a string is a valid account number
    public static final Predicate<String> isValidAccountNumber =
            input -> ACCOUNT_NUMBER_PATTERN.matcher(input).matches();

    // Predicate that tests if a string is a valid email
    public static final Predicate<String> isValidEmail =
            input -> EMAIL_PATTERN.matcher(input).matches();

    // ── Methods using Pattern + Matcher (explicit matching) ──────────────

    // Validate account number using Pattern and Matcher
    public static boolean validateAccountNumber(String accountNumber) {
        Matcher matcher = ACCOUNT_NUMBER_PATTERN.matcher(accountNumber);
        return matcher.matches();
    }

    // Validate email using Pattern and Matcher
    public static boolean validateEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    // ── Error message helpers ────────────────────────────────────────────

    public static String getAccountNumberError() {
        return "Account number must follow format ACC-XXX (e.g. ACC-001, ACC-123).";
    }

    public static String getEmailError() {
        return "Invalid email format. Expected format: user@example.com";
    }
}