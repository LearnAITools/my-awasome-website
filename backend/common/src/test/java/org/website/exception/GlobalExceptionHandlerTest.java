package org.website.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.WebRequest;
import org.website.constant.ErrorCode;
import org.website.dto.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest request = mock(WebRequest.class);

    @Test
    void mapsResourceNotFoundToNotFoundResponse() {
        ErrorResponse response = handler
            .handleResourceNotFoundException(new ResourceNotFoundException("Movie missing"), request)
            .getBody();

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.getCode(), response.getErrorCode());
        assertEquals("Movie missing", response.getMessage());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.name(), response.getErrorType());
    }

    @Test
    void mapsSeatConflictToConflictResponse() {
        ErrorResponse response = handler
            .handleSeatAlreadyBookedException(new SeatAlreadyBookedException("Seat A1 is booked"), request)
            .getBody();

        assertEquals(ErrorCode.SEAT_ALREADY_BOOKED.getStatusCode(), response.getStatus());
        assertEquals(ErrorCode.SEAT_ALREADY_BOOKED.getCode(), response.getErrorCode());
        assertEquals("Seat A1 is booked", response.getMessage());
    }

    @Test
    void hidesUnexpectedExceptionDetails() {
        ErrorResponse response = handler
            .handleGlobalException(new RuntimeException("database password"), request)
            .getBody();

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), response.getErrorCode());
        assertEquals("An unexpected error occurred. Please try again later or contact support.", response.getMessage());
    }
}
