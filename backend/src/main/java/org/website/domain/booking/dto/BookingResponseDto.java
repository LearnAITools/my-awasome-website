package org.website.domain.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for booking response to client.
 * Contains all booking details including show info and seat details.
 * 
 * Domain: Booking
 * Type: Response
 * 
 * Example Response:
 * {
 *   "bookingId": 101,
 *   "bookingReference": "BK-ABC123DEF",
 *   "status": "RESERVED",
 *   "movieTitle": "Kalki 2898 AD",
 *   "theaterName": "PVR Cinemas",
 *   "showTime": "2026-06-01T18:00:00",
 *   "selectedSeats": [1, 2, 5],
 *   "quantity": 3,
 *   "totalPrice": 75000,
 *   "bookingDate": "2026-05-30T10:30:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long bookingId;
    private String bookingReference;
    private String status;
    private Long totalPrice;
    private LocalDateTime bookingDate;
    private String paymentOrderId;
    private String movieTitle;
    private LocalDateTime showTime;
    private String theaterName;
    private List<Integer> selectedSeats;
}
