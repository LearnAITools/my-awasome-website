/**
 * Movie Domain Package.
 * 
 * This domain encapsulates all movie management and catalog operations.
 * Includes movie details, search, filtering, and admin movie creation.
 * 
 * Layers:
 * - dto: Data Transfer Objects for movie operations
 *   * MovieDTO: Movie details for display in catalog
 *   * Includes title, genre, duration, language, rating, cast, crew
 * 
 * - service: Movie business logic
 *   * MovieDomainService: Movie search, filtering, management
 *   * getAllMovies() - Return all movies with optional filtering
 *   * searchMovies() - Search by title, genre, language
 *   * getMovieById() - Fetch single movie details with cast/crew
 *   * Admin: createMovie() - Add new movie (admin only)
 * 
 * - controller: HTTP endpoints
 *   * GET /api/movies - Get all movies (with filters)
 *   * GET /api/movies/{id} - Get movie details
 *   * GET /api/movies/search - Search movies
 *   * POST /api/movies - Create movie (admin only)
 * 
 * - repository: Database access layer
 *   * Interface for Movie entity CRUD operations
 *   * Custom queries: findByTitle, findByGenre, etc.
 * 
 * - model: Domain entity
 *   * Movie entity with JPA annotations
 *   * One-to-Many: Movie has multiple Shows
 *   * One-to-Many: Movie has multiple Cast members
 * 
 * Filters:
 * - By Genre: Action, Comedy, Drama, Horror, etc.
 * - By Language: English, Hindi, Telugu, Kannada, etc.
 * - By Release Date: Upcoming, Now Showing, Recently Released
 * - By Rating: Sort by IMDb rating
 * 
 * Error Codes:
 * - ERR100: Movie not found
 * - ERR101: Genre invalid
 * - ERR102: Show not found
 * 
 * @since 2026-05-30
 */
package org.website.domain.movie;
