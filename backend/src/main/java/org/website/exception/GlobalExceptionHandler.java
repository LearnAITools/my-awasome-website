package org.website.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.website.constant.ErrorCode;
import org.website.dto.ErrorResponse;
import java.time.LocalDateTime;

/**
 * Global exception handler for the entire application.
 * Catches all exceptions and returns standardized error responses with error codes.
 * 
 * Provides centralized error handling following a common error code pattern:
 * - ERR001-ERR099: Authentication & Authorization
 * - ERR100-ERR199: Resource Not Found
 * - ERR200-ERR299: Booking & Seat Conflicts
 * - ERR300-ERR399: Payment Errors
 * - ERR400-ERR499: Validation Errors
 * - ERR500-ERR599: System Errors
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle custom ResourceNotFoundException
     * Used when a requested entity (Movie, Show, etc.) is not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("[ERR100] Resource not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = buildErrorResponse(
            ErrorCode.RESOURCE_NOT_FOUND,
            request,
            ex.getMessage()
        );
        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.getStatus()).body(errorResponse);
    }

    /**
     * Handle seat booking conflicts when seat is already booked
     * Triggers when user tries to book an unavailable seat
     */
    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyBookedException(
            SeatAlreadyBookedException ex, WebRequest request) {
        log.warn("[ERR200] Seat booking conflict: {}", ex.getMessage());
        
        ErrorResponse errorResponse = buildErrorResponse(
            ErrorCode.SEAT_ALREADY_BOOKED,
            request,
            ex.getMessage()
        );
        return ResponseEntity.status(ErrorCode.SEAT_ALREADY_BOOKED.getStatus()).body(errorResponse);
    }

    /**
     * Handle payment-related errors
     * Triggers on Razorpay signature verification failures or payment processing errors
     */
    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentException(
            InvalidPaymentException ex, WebRequest request) {
        log.error("[ERR300] Payment error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = buildErrorResponse(
            ErrorCode.PAYMENT_FAILED,
            request,
            ex.getMessage()
        );
        return ResponseEntity.status(ErrorCode.PAYMENT_FAILED.getStatus()).body(errorResponse);
    }

    /**
     * Handle unauthorized access attempts
     * Triggers when user lacks required permissions or credentials
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        log.warn("[ERR002] Unauthorized access attempt: {}", ex.getMessage());
        
        ErrorResponse errorResponse = buildErrorResponse(
            ErrorCode.AUTH_UNAUTHORIZED_ACCESS,
            request,
            ex.getMessage()
        );
        return ResponseEntity.status(ErrorCode.AUTH_UNAUTHORIZED_ACCESS.getStatus()).body(errorResponse);
    }

    /**
     * Handle validation errors and invalid argument exceptions
     * Triggers on bad input data, invalid formats, etc.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.error("[ERR400] Validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = buildErrorResponse(
            ErrorCode.VALIDATION_FAILED,
            request,
            ex.getMessage()
        );
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus()).body(errorResponse);
    }

    /**
     * Handle invalid state transitions
     * Triggers when an operation is performed on an entity in an invalid state
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        log.error("[ERR203] Invalid state: {}", ex.getMessage());
        
        ErrorResponse errorResponse = buildErrorResponse(
            ErrorCode.BOOKING_INVALID_STATE,
            request,
            ex.getMessage()
        );
        return ResponseEntity.status(ErrorCode.BOOKING_INVALID_STATE.getStatus()).body(errorResponse);
    }

    /**
     * Handle optimistic locking failures
     * Triggers when concurrent updates conflict on versioned entities
     */
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex, WebRequest request) {
        log.error("[ERR204] Concurrency conflict: Seat was booked by another user");
        
        ErrorResponse errorResponse = buildErrorResponse(
            ErrorCode.BOOKING_CONCURRENCY_CONFLICT,
            request,
            "The selected seat was just booked by another user. Please select different seats and try again."
        );
        return ResponseEntity.status(ErrorCode.BOOKING_CONCURRENCY_CONFLICT.getStatus()).body(errorResponse);
    }

    /**
     * Global catch-all exception handler
     * Handles any unexpected exceptions not covered by specific handlers
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("[ERR500] Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = buildErrorResponse(
            ErrorCode.INTERNAL_SERVER_ERROR,
            request,
            "An unexpected error occurred. Please try again later or contact support."
        );
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(errorResponse);
    }

    /**
     * Build a standardized error response with all required fields
     * 
     * @param errorCode The ErrorCode enum containing code, message, and status
     * @param request The current web request
     * @param customMessage Optional custom message; if null, uses default from ErrorCode
     * @return Fully populated ErrorResponse object
     */
    private ErrorResponse buildErrorResponse(
            ErrorCode errorCode, 
            WebRequest request, 
            String customMessage) {
        
        ErrorResponse response = new ErrorResponse();
        response.setErrorCode(errorCode.getCode());
        response.setMessage(customMessage != null ? customMessage : errorCode.getMessage());
        response.setStatus(errorCode.getStatusCode());
        response.setErrorType(errorCode.name());
        response.setTimestamp(LocalDateTime.now());
        
        // Extract path from request if available
        String path = null;
        if (request instanceof org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor) {
            try {
                path = request.getDescription(false).substring(4); // Remove "uri=" prefix
                response.setPath(path);
            } catch (Exception e) {
                log.debug("Could not extract request path", e);
            }
        }
        
        return response;
    }
}

