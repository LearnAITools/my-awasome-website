package org.website.service;

import org.junit.jupiter.api.Test;
import org.website.dto.MovieDTO;
import org.website.exception.ResourceNotFoundException;
import org.website.model.Movie;
import org.website.repository.MovieRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MovieServiceTest {

    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final MovieService movieService = new MovieService(movieRepository);

    @Test
    void returnsMoviesMappedToDtos() {
        Movie movie = new Movie("Dune", "A sci-fi epic", "Sci-Fi", 166, "English", "/dune.jpg", null, 8.8);
        movie.setId(7L);
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<MovieDTO> movies = movieService.getAllMovies();

        assertEquals(1, movies.size());
        assertEquals(7L, movies.get(0).getId());
        assertEquals("Dune", movies.get(0).getTitle());
        assertEquals("Sci-Fi", movies.get(0).getGenre());
    }

    @Test
    void throwsWhenMovieDoesNotExist() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> movieService.getMovieById(99L)
        );

        assertEquals("Movie not found with ID: 99", exception.getMessage());
    }

    @Test
    void delegatesTitleSearchToRepository() {
        when(movieRepository.findByTitleContainingIgnoreCase("dune")).thenReturn(List.of());

        assertEquals(List.of(), movieService.searchMovies("dune"));
    }
}
