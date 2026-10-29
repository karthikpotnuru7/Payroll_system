package util;

/**
 * InputValidator holds small static helper methods for checking user
 * input. Static because these checks don't need any state of their own —
 * they just take a value in and return true/false, or throw an exception
 * for the caller to catch.
 */
public class InputValidator {

    // A name must not be empty or just whitespace.
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    // Used for salary, hours, rates, etc. — none of these should be negative.
    public static boolean isNonNegative(double value) {
        return value >= 0;
    }

    // Employee IDs must be positive whole numbers.
    public static boolean isValidId(int id) {
        return id > 0;
    }
}