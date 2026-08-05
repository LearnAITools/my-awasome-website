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

    // Explicit getters for Maven compilation (Lombok annotation processing disabled)
    public Long getId() { return id; }
    public Long getMovieId() { return movieId; }
    public String getMovieTitle() { return movieTitle; }
    public Long getTheaterId() { return theaterId; }
    public String getTheaterName() { return theaterName; }
    public LocalDateTime getShowTime() { return showTime; }
    public Integer getTotalSeats() { return totalSeats; }
    public Long getPriceInCents() { return priceInCents; }
    public Integer getScreen() { return screen; }
    public Integer getAvailableSeats() { return availableSeats; }
    public List<SeatDTO> getSeats() { return seats; }

    // Explicit setters for Maven compilation
    public void setId(Long id) { this.id = id; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setTheaterId(Long theaterId) { this.theaterId = theaterId; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public void setPriceInCents(Long priceInCents) { this.priceInCents = priceInCents; }
    public void setScreen(Integer screen) { this.screen = screen; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    public void setSeats(List<SeatDTO> seats) { this.seats = seats; }
}
