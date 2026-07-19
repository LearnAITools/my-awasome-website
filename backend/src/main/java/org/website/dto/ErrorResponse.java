package org.website.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Standard error response DTO used across all API error responses.
 * Provides detailed error information including error code, message, and timestamp.
 * 
 * Error codes follow the pattern: ERRxxx where xxx represents error category and number.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    /**
     * Unique error code (e.g., ERR001, ERR100) for programmatic error handling
     */
    @JsonProperty("error_code")
    private String errorCode;

    /**
     * Human-readable error message for end users
     */
    private String message;

    /**
     * HTTP status code (e.g., 404, 409, 500)
     */
    private Integer status;

    /**
     * Error type/category for classification
     */
    @JsonProperty("error_type")
    private String errorType;

    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;

    /**
     * Request path that caused the error
     */
    private String path;

    /**
     * Additional details or context about the error (optional)
     */
    private String details;

    /**
     * Constructor for common error responses
     */
    public ErrorResponse(String errorCode, String message, Integer status, String errorType, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.errorType = errorType;
        this.timestamp = timestamp;
    }
}
