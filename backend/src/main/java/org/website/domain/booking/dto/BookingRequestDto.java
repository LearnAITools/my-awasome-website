package org.website.domain.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for booking request from client.
 * Used for creating new bookings with selected seats.
 * 
 * Domain: Booking
 * Type: Request
 * 
 * Example:
 * {
 *   "showId": 1,
 *   "selectedSeats": [1, 2, 5],
 *   "quantity": 3
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    private Long showId;
    private List<Integer> selectedSeats;
    private Integer quantity;

    /**
     * Validate request data before processing
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (showId == null || showId <= 0) {
            throw new IllegalArgumentException("Show ID must be positive");
        }
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }
        if (quantity == null || quantity != selectedSeats.size()) {
            throw new IllegalArgumentException("Quantity must match selected seats count");
        }
    }
}
