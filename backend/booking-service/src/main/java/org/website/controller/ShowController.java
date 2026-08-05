package org.website.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.ShowDTO;
import org.website.service.ShowService;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Show Controller - Handles movie show scheduling and retrieval operations.
 * 
 * This controller manages all show-related operations including:
 * - Fetching available shows for movies and theaters
 * - Retrieving show details with seat information
 * - Creating new shows (admin only)
 * 
 * Endpoints:
 * - GET /api/shows - Fetch all shows
 * - GET /api/shows/{id} - Get show details with seat matrix
 * - GET /api/shows/movie/{movieId} - Get shows for a specific movie
 * - GET /api/shows/theater/{theaterId} - Get shows for a specific theater
 * - POST /api/shows - Create new show (ADMIN only)
 * 
 * Security: GET endpoints are PUBLIC. POST endpoint requires ADMIN role.
 * 
 * Show Details: Each show includes a 10x10 seat matrix with status information.
 * Seats statuses: AVAILABLE (can be booked), RESERVED (temporarily held),
 * BOOKED (sold), BLOCKED (maintenance/disabled).
 * 
 * @author BookMyShow Dev Team
 * @version 1.0
 * @since 2026-05-30
 */
@RestController
@RequestMapping("/api/shows")
@Slf4j
public class ShowController {

    private ShowService showService;

    ShowController(ShowService showService) {
        this.showService = showService;
    }

    /**
     * Retrieve all shows in the system.
     * 
     * Returns a complete list of all available shows across all theaters.
     * Used to populate show listings without movie/theater filters.
     * 
     * @return ResponseEntity containing List<ShowDTO> with all shows
     * @return HTTP 200 OK with shows list
     * 
     * Example Response:
     * [
     *   {
     *     "id": 1,
     *     "movieId": 1,
     *     "movieTitle": "Kalki 2898 AD",
     *     "theaterId": 1,
     *     "theaterName": "AMB Cinemas",
     *     "screen": "Screen 1",
     *     "showTime": "2026-06-01T18:00:00",
     *     "pricePerSeat": 25000,
     *     "totalSeats": 100,
     *     "availableSeats": 45
     *   },
     *   ...
     * ]
     */
    @GetMapping
    public ResponseEntity<List<ShowDTO>> getAllShows() {
        log.info("Fetching all shows");
        return ResponseEntity.ok(showService.getAllShows());
    }

    /**
     * Retrieve show details with complete seat matrix.
     * 
     * Fetches detailed information about a specific show including the 10x10
     * seat layout with real-time seat status (AVAILABLE, RESERVED, BOOKED, BLOCKED).
     * Used when user selects a show to see available seats.
     * 
     * @param id The unique identifier of the show (must be positive)
     * @return ResponseEntity containing ShowDTO with complete seat matrix
     * @return HTTP 200 OK with show details and seat layout
     * @return HTTP 404 NOT_FOUND if show doesn't exist (error_code: ERR102)
     * 
     * @throws ResourceNotFoundException if show not found
     * 
     * Example Response (200):
     * {
     *   "id": 1,
     *   "movieId": 1,
     *   "movieTitle": "Kalki 2898 AD",
     *   "movieDuration": 180,
     *   "theaterId": 1,
     *   "theaterName": "AMB Cinemas",
     *   "theaterLocation": "Bangalore",
     *   "screen": "Screen 1",
     *   "showTime": "2026-06-01T18:00:00",
     *   "pricePerSeat": 25000,
     *   "totalSeats": 100,
     *   "availableSeats": 45,
     *   "seatLayout": [
     *     [
     *       {
     *         "seatNumber": "A1",
     *         "status": "AVAILABLE",
     *         "price": 25000
     *       },
     *       {
     *         "seatNumber": "A2",
     *         "status": "BOOKED",
     *         "price": 25000
     *       },
     *       ...
     *     ],
     *     ...
     *   ]
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShowDTO> getShowById(@PathVariable Long id) {
        log.info("Fetching show details with seats for show id: {}", id);
        ShowDTO show = showService.getShowById(id);
        log.debug("Show found: {} - {}", show.getMovieTitle(), show.getShowTime());
        return ResponseEntity.ok(show);
    }

    /**
     * Retrieve all shows for a specific movie.
     * 
     * Fetches all available show timings across all theaters for the given movie.
     * Used on movie detail page to show "Choose Timing" section.
     * Shows are typically sorted by date and time.
     * 
     * @param movieId The unique identifier of the movie (must be positive)
     * @return ResponseEntity containing List<ShowDTO> for the movie
     * @return HTTP 200 OK with shows list (empty list if no shows)
     * @return HTTP 404 NOT_FOUND if movie doesn't exist (error_code: ERR101)
     * 
     * @throws ResourceNotFoundException if movie not found
     * 
     * Example: GET /api/shows/movie/1
     * Returns: All show timings for movie ID 1 across all theaters
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowDTO>> getShowsByMovie(@PathVariable Long movieId) {
        log.info("Fetching all shows for movie id: {}", movieId);
        List<ShowDTO> shows = showService.getShowsByMovie(movieId);
        log.debug("Found {} shows for movie", shows.size());
        return ResponseEntity.ok(shows);
    }

    /**
     * Retrieve all shows for a specific theater.
     * 
     * Fetches all current shows in a particular theater.
     * Used to show what's currently playing in a theater.
     * 
     * @param theaterId The unique identifier of the theater (must be positive)
     * @return ResponseEntity containing List<ShowDTO> for the theater
     * @return HTTP 200 OK with shows list (empty list if no shows)
     * @return HTTP 404 NOT_FOUND if theater doesn't exist (error_code: ERR103)
     * 
     * @throws ResourceNotFoundException if theater not found
     * 
     * Example: GET /api/shows/theater/1
     * Returns: All shows currently scheduled in theater ID 1
     */
    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<ShowDTO>> getShowsByTheater(@PathVariable Long theaterId) {
        log.info("Fetching all shows for theater id: {}", theaterId);
        List<ShowDTO> shows = showService.getShowsByTheater(theaterId);
        log.debug("Found {} shows for theater", shows.size());
        return ResponseEntity.ok(shows);
    }

    /**
     * Create a new show (schedule a movie at a theater).
     * 
     * RESTRICTED to ADMIN users only. Allows administrators to schedule movies
     * at specific theaters and times. Automatically creates a 10x10 seat matrix
     * with all seats initially marked as AVAILABLE.
     * 
     * @param showDTO ShowDTO containing:
     *                - movieId (required, must exist)
     *                - theaterId (required, must exist)
     *                - screen (required, e.g., "Screen 1")
     *                - showTime (required, cannot be in past)
     *                - pricePerSeat (required, must be positive)
     * 
     * @return ResponseEntity containing created ShowDTO with auto-generated ID
     * @return HTTP 201 CREATED with new show details
     * @return HTTP 400 BAD_REQUEST if validation fails (error_code: ERR400)
     * @return HTTP 403 FORBIDDEN if user is not ADMIN (error_code: ERR005)
     * @return HTTP 404 NOT_FOUND if movie or theater not found
     * 
     * Example Request:
     * {
     *   "movieId": 1,
     *   "theaterId": 1,
     *   "screen": "Screen 1",
     *   "showTime": "2026-06-01T18:00:00",
     *   "pricePerSeat": 25000
     * }
     * 
     * Example Response (201):
     * {
     *   "id": 10,
     *   "movieId": 1,
     *   "movieTitle": "Kalki 2898 AD",
     *   "theaterId": 1,
     *   "theaterName": "AMB Cinemas",
     *   "screen": "Screen 1",
     *   "showTime": "2026-06-01T18:00:00",
     *   "pricePerSeat": 25000,
     *   "totalSeats": 100,
     *   "availableSeats": 100
     * }
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowDTO> createShow(@RequestBody ShowDTO showDTO) {
        log.info("Admin creating new show for movie: {}, theater: {}", showDTO.getMovieId(), showDTO.getTheaterId());
        ShowDTO created = showService.createShowFromDTO(showDTO);
        log.info("Show created successfully with id: {}", created.getId());
        return ResponseEntity.ok(created);
    }
}
