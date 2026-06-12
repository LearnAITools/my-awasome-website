package org.website.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.MovieDTO;
import org.website.dto.ShowDTO;
import org.website.dto.BookingResponse;
import org.website.service.MovieService;
import org.website.service.ShowService;
import org.website.service.BookingService;
import java.util.List;

/**
 * AdminController handles administrative operations for the BookMyShow platform.
 * All endpoints require ADMIN role authentication.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {
    
    private MovieService movieService;
    private ShowService showService;
    private BookingService bookingService;

    AdminController(MovieService movieService, ShowService showService, BookingService bookingService) {
        this.movieService = movieService;
        this.showService = showService;
        this.bookingService = bookingService;
    }

    /**
     * Create a new movie (Admin only)
     * @param movieDTO the movie details
     * @return the created movie
     */
    @PostMapping("/movies")
    public ResponseEntity<MovieDTO> createMovie(@RequestBody MovieDTO movieDTO) {
        log.info("Admin creating new movie: {}", movieDTO.getTitle());
        MovieDTO createdMovie = movieService.createMovie(movieDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    /**
     * Create a new show (Admin only)
     * @param showDTO the show details
     * @return the created show
     */
    @PostMapping("/shows")
    public ResponseEntity<ShowDTO> createShow(@RequestBody ShowDTO showDTO) {
        log.info("Admin creating new show for movie: {}, theater: {}", showDTO.getMovieId(), showDTO.getTheaterId());
        ShowDTO createdShow = showService.createShowFromDTO(showDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShow);
    }

    /**
     * Get all bookings (Analytics)
     * @return list of all bookings
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        log.info("Admin retrieving all bookings");
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
}
