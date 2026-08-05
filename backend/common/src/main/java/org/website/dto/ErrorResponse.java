package org.website.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Standard error response DTO used across all API error responses.
 * Provides detailed error information including error code, message, and timestamp.
 * 
 * Error codes follow the pattern: ERRxxx where xxx represents error category and number.
 */
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
     * Default constructor for serialization.
     */
    public ErrorResponse() {
    }

    /**
     * Constructor for common error responses.
     */
    public ErrorResponse(String errorCode, String message, Integer status, String errorType, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.errorType = errorType;
        this.timestamp = timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
