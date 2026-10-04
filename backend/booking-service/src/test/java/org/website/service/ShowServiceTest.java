package org.website.service;

import org.junit.jupiter.api.Test;
import org.website.dto.ShowDTO;
import org.website.exception.ResourceNotFoundException;
import org.website.model.Movie;
import org.website.model.SeatStatus;
import org.website.model.Show;
import org.website.model.Theater;
import org.website.repository.MovieRepository;
import org.website.repository.SeatRepository;
import org.website.repository.ShowRepository;
import org.website.repository.TheaterRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowServiceTest {

    private final ShowRepository showRepository = mock(ShowRepository.class);
    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final TheaterRepository theaterRepository = mock(TheaterRepository.class);
    private final SeatRepository seatRepository = mock(SeatRepository.class);
    private final ShowService showService = new ShowService(
        showRepository, movieRepository, theaterRepository, seatRepository
    );

    @Test
    void createsShowAndOneHundredAvailableSeats() {
        Movie movie = new Movie("Dune", "Sci-fi", "Sci-Fi", 166, "English", "/dune.jpg", "2026-01-01", 8.8);
        movie.setId(1L);
        Theater theater = new Theater("Central", "Pune", "Main Road", 2);
        theater.setId(2L);
        Show savedShow = new Show(movie, theater, LocalDateTime.now().plusDays(1), 100, 75000L, 1);
        savedShow.setId(3L);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(2L)).thenReturn(Optional.of(theater));
        when(showRepository.save(any(Show.class))).thenReturn(savedShow);
        when(seatRepository.countByShowIdAndStatus(3L, SeatStatus.AVAILABLE)).thenReturn(100);
        when(seatRepository.findByShowId(3L)).thenReturn(List.of());

        ShowDTO response = showService.createShow(1L, 2L, savedShow.getShowTime(), 1, 75000L);

        assertEquals(3L, response.getId());
        assertEquals("Dune", response.getMovieTitle());
        assertEquals(100, response.getAvailableSeats());
        verify(seatRepository, org.mockito.Mockito.times(100)).save(any());
    }

    @Test
    void rejectsShowForMissingMovie() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> showService.createShow(99L, 2L, LocalDateTime.now(), 1, 75000L)
        );
    }
}
