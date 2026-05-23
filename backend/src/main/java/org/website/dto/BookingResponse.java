package org.website.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingReference;
    private String status;
    private Long totalAmountInCents;
    private LocalDateTime bookingTime;
    private String paymentOrderId;
    private String movieTitle;
    private LocalDateTime showTime;
    private String theaterName;
    private List<Integer> seatNumbers;
}
