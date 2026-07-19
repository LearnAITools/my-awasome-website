package org.website.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.website.constant.ErrorCode;
import org.website.exception.InvalidPaymentException;
import org.website.exception.ResourceNotFoundException;
import org.website.exception.SeatAlreadyBookedException;
import org.website.exception.UnauthorizedException;

/**
 * Utility class for error handling and validation across the application.
 * Provides helper methods to throw exceptions with appropriate error codes.
 * 
 * This class ensures consistency in error handling and reduces code duplication
 * in service and controller layers.
 * 
 * Usage Example:
 * <pre>
 * Movie movie = movieRepository.findById(id)
 *   .orElseThrow(() -> ErrorUtil.throwResourceNotFound("Movie", id));
 * </pre>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorUtil {

    /**
     * Throw ResourceNotFoundException with appropriate error code
     * @param resource Name of the resource (e.g., "Movie", "Show", "User")
     * @param identifier The ID or identifier of the missing resource
     */
    public static void throwResourceNotFound(String resource, Object identifier) {
        String message = String.format("%s with id '%s' not found", resource, identifier);
        log.error("[{}] {}", ErrorCode.RESOURCE_NOT_FOUND.getCode(), message);
        throw new ResourceNotFoundException(message);
    }

    /**
     * Throw SeatAlreadyBookedException when seat booking conflict occurs
     * @param seatNumber The seat number/identifier
     * @param showId The show ID
     */
    public static void throwSeatAlreadyBooked(String seatNumber, Long showId) {
        String message = String.format("Seat %s for show %d is already booked", seatNumber, showId);
        log.warn("[{}] {}", ErrorCode.SEAT_ALREADY_BOOKED.getCode(), message);
        throw new SeatAlreadyBookedException(message);
    }

    /**
     * Throw SeatAlreadyBookedException with custom message
     * @param message Custom error message
     */
    public static void throwSeatAlreadyBooked(String message) {
        log.warn("[{}] {}", ErrorCode.SEAT_ALREADY_BOOKED.getCode(), message);
        throw new SeatAlreadyBookedException(message);
    }

    /**
     * Throw InvalidPaymentException when payment processing fails
     * @param reason The reason for payment failure
     */
    public static void throwPaymentFailed(String reason) {
        String message = String.format("Payment processing failed: %s", reason);
        log.error("[{}] {}", ErrorCode.PAYMENT_FAILED.getCode(), message);
        throw new InvalidPaymentException(message);
    }

    /**
     * Throw InvalidPaymentException for signature verification failures
     * @param detailMessage Details about the verification failure
     */
    public static void throwPaymentSignatureInvalid(String detailMessage) {
        String message = String.format("Payment signature verification failed: %s", detailMessage);
        log.error("[{}] {}", ErrorCode.PAYMENT_INVALID_SIGNATURE.getCode(), message);
        throw new InvalidPaymentException(message);
    }

    /**
     * Throw UnauthorizedException when user lacks required permissions
     * @param message Descriptive message about authorization failure
     */
    public static void throwUnauthorized(String message) {
        log.warn("[{}] {}", ErrorCode.AUTH_UNAUTHORIZED_ACCESS.getCode(), message);
        throw new UnauthorizedException(message);
    }

    /**
     * Throw UnauthorizedException when user is not authenticated
     * @param userId The user ID that failed authentication
     */
    public static void throwNotAuthenticated(Long userId) {
        String message = String.format("User %d is not authenticated", userId);
        log.warn("[{}] {}", ErrorCode.AUTH_USER_NOT_FOUND.getCode(), message);
        throw new UnauthorizedException(message);
    }

    /**
     * Validate that a required field is not null
     * @param value The value to validate
     * @param fieldName Name of the field being validated
     * @throws IllegalArgumentException if value is null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            String message = String.format("Required field '%s' is missing", fieldName);
            log.error("[{}] {}", ErrorCode.MISSING_REQUIRED_FIELD.getCode(), message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate that a string field is not empty
     * @param value The value to validate
     * @param fieldName Name of the field being validated
     * @throws IllegalArgumentException if value is null or empty
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            String message = String.format("Required field '%s' cannot be empty", fieldName);
            log.error("[{}] {}", ErrorCode.MISSING_REQUIRED_FIELD.getCode(), message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate that a numeric value is positive
     * @param value The value to validate
     * @param fieldName Name of the field being validated
     * @throws IllegalArgumentException if value is not positive
     */
    public static void validatePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            String message = String.format("Field '%s' must be positive", fieldName);
            log.error("[{}] {}", ErrorCode.VALIDATION_FAILED.getCode(), message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Log error with error code prefix for consistency
     * @param errorCode The ErrorCode enum
     * @param message The error message
     */
    public static void logError(ErrorCode errorCode, String message) {
        log.error("[{}] {}", errorCode.getCode(), message);
    }

    /**
     * Log warning with error code prefix for consistency
     * @param errorCode The ErrorCode enum
     * @param message The warning message
     */
    public static void logWarn(ErrorCode errorCode, String message) {
        log.warn("[{}] {}", errorCode.getCode(), message);
    }
}
