package org.website.constant;

import org.springframework.http.HttpStatus;

/**
 * Enumeration of all error codes used in the application.
 * Each error code is mapped to a specific HTTP status and a default message.
 * 
 * Error Code Naming Convention:
 * - ERR001-ERR099: Authentication & Authorization errors
 * - ERR100-ERR199: Resource/Entity errors
 * - ERR200-ERR299: Booking & Seat errors
 * - ERR300-ERR399: Payment errors
 * - ERR400-ERR499: Validation errors
 * - ERR500-ERR599: System/Internal errors
 */
public enum ErrorCode {
    // ===== AUTHENTICATION & AUTHORIZATION (ERR001-ERR099) =====
    AUTH_INVALID_CREDENTIALS("ERR001", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    AUTH_UNAUTHORIZED_ACCESS("ERR002", "Unauthorized access to this resource", HttpStatus.FORBIDDEN),
    AUTH_TOKEN_EXPIRED("ERR003", "Authentication token has expired", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID("ERR004", "Invalid authentication token", HttpStatus.UNAUTHORIZED),
    AUTH_ROLE_REQUIRED("ERR005", "Required role not found for this operation", HttpStatus.FORBIDDEN),
    AUTH_USER_NOT_FOUND("ERR006", "User not found in the system", HttpStatus.UNAUTHORIZED),
    AUTH_EMAIL_ALREADY_EXISTS("ERR007", "Email already registered in the system", HttpStatus.CONFLICT),

    // ===== RESOURCE & ENTITY ERRORS (ERR100-ERR199) =====
    RESOURCE_NOT_FOUND("ERR100", "The requested resource was not found", HttpStatus.NOT_FOUND),
    MOVIE_NOT_FOUND("ERR101", "Movie not found in the system", HttpStatus.NOT_FOUND),
    SHOW_NOT_FOUND("ERR102", "Show not found in the system", HttpStatus.NOT_FOUND),
    THEATER_NOT_FOUND("ERR103", "Theater not found in the system", HttpStatus.NOT_FOUND),
    SEAT_NOT_FOUND("ERR104", "Seat not found in the system", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_FOUND("ERR105", "Payment not found in the system", HttpStatus.NOT_FOUND),
    BOOKING_NOT_FOUND("ERR106", "Booking not found in the system", HttpStatus.NOT_FOUND),

    // ===== BOOKING & SEAT ERRORS (ERR200-ERR299) =====
    SEAT_ALREADY_BOOKED("ERR200", "Selected seat is already booked", HttpStatus.CONFLICT),
    SEAT_RESERVED("ERR201", "Selected seat is reserved by another user", HttpStatus.CONFLICT),
    SEAT_INVALID_STATUS("ERR202", "Seat has invalid status for booking", HttpStatus.BAD_REQUEST),
    BOOKING_INVALID_STATE("ERR203", "Booking cannot be modified in current state", HttpStatus.BAD_REQUEST),
    BOOKING_CONCURRENCY_CONFLICT("ERR204", "Seat was booked by another user, please try again", HttpStatus.CONFLICT),
    BOOKING_EXPIRED("ERR205", "Booking reservation has expired", HttpStatus.REQUEST_TIMEOUT),
    SHOW_FULL("ERR206", "All seats for this show are already booked", HttpStatus.CONFLICT),

    // ===== PAYMENT ERRORS (ERR300-ERR399) =====
    PAYMENT_FAILED("ERR300", "Payment processing failed", HttpStatus.BAD_REQUEST),
    PAYMENT_INVALID_SIGNATURE("ERR301", "Payment signature verification failed", HttpStatus.BAD_REQUEST),
    PAYMENT_INVALID_AMOUNT("ERR302", "Payment amount is invalid", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED("ERR303", "Payment has already been processed", HttpStatus.CONFLICT),
    RAZORPAY_ERROR("ERR304", "Error communicating with payment gateway", HttpStatus.BAD_GATEWAY),

    // ===== VALIDATION ERRORS (ERR400-ERR499) =====
    VALIDATION_FAILED("ERR400", "Request validation failed", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("ERR401", "Email format is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("ERR402", "Password does not meet security requirements", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT("ERR403", "Date format is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PRICE_FORMAT("ERR404", "Price format is invalid", HttpStatus.BAD_REQUEST),
    INVALID_SEAT_SELECTION("ERR405", "Invalid seat selection", HttpStatus.BAD_REQUEST),
    INVALID_SHOW_TIME("ERR406", "Show time cannot be in the past", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("ERR407", "Required field is missing", HttpStatus.BAD_REQUEST),

    // ===== SYSTEM & INTERNAL ERRORS (ERR500-ERR599) =====
    INTERNAL_SERVER_ERROR("ERR500", "An internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("ERR501", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_OPERATION_FAILED("ERR502", "File operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SENDING_FAILED("ERR503", "Failed to send notification email", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR("ERR504", "External service is temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    INVALID_OPERATION("ERR505", "The requested operation is not valid", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getStatusCode() {
        return status.value();
    }
}
