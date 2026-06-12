package org.website.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.MovieDTO;
import org.website.service.MovieService;
import java.util.List;

/**
 * Movie Controller - Handles movie-related API operations.
 * 
 * This controller provides endpoints for retrieving movie information including:
 * - Fetching all movies or specific movies
 * - Searching movies by title, genre, or language
 * - Creating new movies (admin only)
 * 
 * Endpoints:
 * - GET /api/movies - Fetch all movies
 * - GET /api/movies/{id} - Fetch specific movie
 * - GET /api/movies/search?title=xxx - Search movies by title
 * - GET /api/movies/genre/{genre} - Filter movies by genre
 * - GET /api/movies/language/{language} - Filter movies by language
 * - POST /api/movies - Create new movie (ADMIN only)
 * 
 * Security: GET endpoints are PUBLIC. POST endpoint requires ADMIN role.
 * 
 * @author BookMyShow Dev Team
 * @version 1.0
 * @since 2026-05-30
 */
@RestController
@RequestMapping("/api/movies")
@Slf4j
public class MovieController {

    private MovieService movieService;

    MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Retrieve all movies currently available in the system.
     * 
     * Returns a complete list of all movies regardless of status or genre.
     * This endpoint is typically used by the frontend to populate the movie grid.
     * 
     * @return ResponseEntity containing List<MovieDTO> with all movies
     * @return HTTP 200 OK with movies list
     * 
     * Example Response:
     * [
     *   {
     *     "id": 1,
     *     "title": "Kalki 2898 AD",
     *     "genre": "Science Fiction",
     *     "language": "Telugu",
     *     "duration": 180,
     *     "posterUrl": "https://...",
     *     "rating": 8.5,
     *     "releaseDate": "2024-05-09"
     *   },
     *   ...
     * ]
     */
    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        log.info("Fetching all movies");
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    /**
     * Retrieve a specific movie by its ID.
     * 
     * Fetches detailed information about a single movie including cast, crew,
     * and other metadata. Used when user clicks on a movie to view details.
     * 
     * @param id The unique identifier of the movie (must be positive)
     * @return ResponseEntity containing MovieDTO with complete movie details
     * @return HTTP 200 OK with movie details
     * @return HTTP 404 NOT_FOUND if movie does not exist (error_code: ERR101)
     * @throws ResourceNotFoundException if movie with given ID not found
     * 
     * Example Response (200):
     * {
     *   "id": 1,
     *   "title": "Kalki 2898 AD",
     *   "genre": "Science Fiction",
     *   "language": "Telugu",
     *   "duration": 180,
     *   "posterUrl": "https://...",
     *   "rating": 8.5,
     *   "releaseDate": "2024-05-09",
     *   "description": "A futuristic action-packed sci-fi thriller..."
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        log.info("Fetching movie with id: {}", id);
        MovieDTO movie = movieService.getMovieById(id);
        log.debug("Movie found: {}", movie.getTitle());
        return ResponseEntity.ok(movie);
    }

    /**
     * Search movies by title (partial or full match).
     * 
     * Performs a case-insensitive search across movie titles.
     * Useful for the search feature in the frontend where users type a movie name.
     * 
     * @param title The movie title or partial title to search for
     * @return ResponseEntity containing List<MovieDTO> matching the search
     * @return HTTP 200 OK with matching movies (empty list if no matches)
     * 
     * Example: GET /api/movies/search?title=Kalki
     * Returns: All movies with "kalki" in the title (case-insensitive)
     */
    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> searchMovies(@RequestParam String title) {
        log.info("Searching movies with title: {}", title);
        List<MovieDTO> results = movieService.searchMovies(title);
        log.debug("Search returned {} results", results.size());
        return ResponseEntity.ok(results);
    }

    /**
     * Filter movies by genre.
     * 
     * Retrieves all movies belonging to a specific genre.
     * Used in the frontend to display genre-based filters and collections.
     * 
     * @param genre The movie genre to filter by (e.g., "Action", "Comedy", "Drama")
     * @return ResponseEntity containing List<MovieDTO> in the specified genre
     * @return HTTP 200 OK with movies (empty list if no movies in genre)
     * 
     * Example: GET /api/movies/genre/Action
     * Returns: All Action genre movies
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(@PathVariable String genre) {
        log.info("Fetching movies by genre: {}", genre);
        List<MovieDTO> results = movieService.getMoviesByGenre(genre);
        log.debug("Found {} movies in genre: {}", results.size(), genre);
        return ResponseEntity.ok(results);
    }

    /**
     * Filter movies by language.
     * 
     * Retrieves all movies in a specific language.
     * Used for multi-language support where users select their preferred language.
     * 
     * @param language The movie language to filter by (e.g., "Hindi", "English", "Telugu")
     * @return ResponseEntity containing List<MovieDTO> in specified language
     * @return HTTP 200 OK with movies (empty list if no movies in language)
     * 
     * Example: GET /api/movies/language/Hindi
     * Returns: All Hindi language movies
     */
    @GetMapping("/language/{language}")
    public ResponseEntity<List<MovieDTO>> getMoviesByLanguage(@PathVariable String language) {
        log.info("Fetching movies by language: {}", language);
        List<MovieDTO> results = movieService.getMoviesByLanguage(language);
        log.debug("Found {} movies in language: {}", results.size(), language);
        return ResponseEntity.ok(results);
    }

    /**
     * Create a new movie in the system.
     * 
     * RESTRICTED to ADMIN users only. Allows administrators to add new movies
     * to the platform without direct database manipulation.
     * 
     * @param movieDTO MovieDTO containing movie details:
     *                  - title (required, non-empty)
     *                  - genre (required)
     *                  - language (required)
     *                  - duration (required, positive integer in minutes)
     *                  - posterUrl (required)
     *                  - rating (optional, 0-10)
     *                  - releaseDate (required)
     *                  - description (optional)
     * 
     * @return ResponseEntity containing created MovieDTO with auto-generated ID
     * @return HTTP 201 CREATED with new movie details
     * @return HTTP 400 BAD_REQUEST if validation fails
     * @return HTTP 403 FORBIDDEN if user is not ADMIN (error_code: ERR005)
     * 
     * Example Request:
     * {
     *   "title": "New Movie",
     *   "genre": "Action",
     *   "language": "Hindi",
     *   "duration": 150,
     *   "posterUrl": "https://...",
     *   "rating": 8.0,
     *   "releaseDate": "2026-06-15",
     *   "description": "An exciting new action movie"
     * }
     * 
     * Example Response (201):
     * {
     *   "id": 10,
     *   "title": "New Movie",
     *   "genre": "Action",
     *   "language": "Hindi",
     *   "duration": 150,
     *   "posterUrl": "https://...",
     *   "rating": 8.0,
     *   "releaseDate": "2026-06-15",
     *   "description": "An exciting new action movie"
     * }
     */
    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@RequestBody MovieDTO movieDTO) {
        log.info("Creating new movie: {}", movieDTO.getTitle());
        MovieDTO created = movieService.createMovie(movieDTO);
        log.info("Movie created successfully with id: {}", created.getId());
        return ResponseEntity.ok(created);
    }
}
