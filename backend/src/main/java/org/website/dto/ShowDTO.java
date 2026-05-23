package org.website.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long theaterId;
    private String theaterName;
    private LocalDateTime showTime;
    private Integer totalSeats;
    private Long priceInCents;
    private Integer screen;
    private Integer availableSeats;
    private List<SeatDTO> seats;
}
