package util;

/**
 * Thrown when trying to add an employee whose ID already exists.
 * A "checked" exception (extends Exception, not RuntimeException) so
 * the compiler forces callers to handle it with try/catch — this is
 * intentional here since a duplicate ID is an expected, recoverable
 * situation, not a bug.
 */

public class DuplicateEmployeeException extends Exception {

    public DuplicateEmployeeException(String message) {
        super(message);
    }
}