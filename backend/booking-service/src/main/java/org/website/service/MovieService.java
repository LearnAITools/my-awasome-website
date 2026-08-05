package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.dto.MovieDTO;
import org.website.exception.ResourceNotFoundException;
import org.website.model.Movie;
import org.website.repository.MovieRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class MovieService {
    
    private MovieRepository movieRepository;

    MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public MovieDTO getMovieById(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));
        return convertToDTO(movie);
    }

    public List<MovieDTO> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<MovieDTO> getMoviesByGenre(String genre) {
        return movieRepository.findByGenreContainingIgnoreCase(genre).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<MovieDTO> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguageContainingIgnoreCase(language).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public MovieDTO createMovie(MovieDTO movieDTO) {
        Movie movie = new Movie(
            movieDTO.getTitle(),
            movieDTO.getDescription(),
            movieDTO.getGenre(),
            movieDTO.getDuration(),
            movieDTO.getLanguage(),
            movieDTO.getPosterUrl(),
            movieDTO.getReleaseDate(),
            movieDTO.getRating()
        );
        Movie savedMovie = movieRepository.save(movie);
        log.info("Movie created: {}", savedMovie.getTitle());
        return convertToDTO(savedMovie);
    }

    private MovieDTO convertToDTO(Movie movie) {
        return new MovieDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getDescription(),
            movie.getGenre(),
            movie.getDuration(),
            movie.getLanguage(),
            movie.getPosterUrl(),
            movie.getReleaseDate(),
            movie.getRating()
        );
    }
}
