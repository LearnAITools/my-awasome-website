package org.website.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.website.dto.ErrorResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "RESOURCE_NOT_FOUND",
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now().format(formatter)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyBookedException(
            SeatAlreadyBookedException ex, WebRequest request) {
        log.warn("Seat already booked: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "SEAT_ALREADY_BOOKED",
            HttpStatus.CONFLICT.value(),
            LocalDateTime.now().format(formatter)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentException(
            InvalidPaymentException ex, WebRequest request) {
        log.error("Invalid payment: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "INVALID_PAYMENT",
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now().format(formatter)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "UNAUTHORIZED",
            HttpStatus.UNAUTHORIZED.value(),
            LocalDateTime.now().format(formatter)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Invalid argument: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "INVALID_ARGUMENT",
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now().format(formatter)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        log.error("Invalid state: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "INVALID_STATE",
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now().format(formatter)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
            "An unexpected error occurred. Please try again later.",
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now().format(formatter)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
