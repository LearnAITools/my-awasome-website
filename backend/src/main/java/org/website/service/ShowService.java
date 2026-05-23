package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.dto.SeatDTO;
import org.website.dto.ShowDTO;
import org.website.exception.ResourceNotFoundException;
import org.website.model.Seat;
import org.website.model.Show;
import org.website.model.SeatStatus;
import org.website.repository.MovieRepository;
import org.website.repository.SeatRepository;
import org.website.repository.ShowRepository;
import org.website.repository.TheaterRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ShowService {
    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private SeatRepository seatRepository;

    public List<ShowDTO> getAllShows() {
        return showRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public ShowDTO getShowById(Long showId) {
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + showId));
        return convertToDTO(show);
    }

    public List<ShowDTO> getShowsByMovie(Long movieId) {
        movieRepository.findById(movieId)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));
        return showRepository.findByMovieId(movieId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<ShowDTO> getShowsByTheater(Long theaterId) {
        theaterRepository.findById(theaterId)
            .orElseThrow(() -> new ResourceNotFoundException("Theater not found with ID: " + theaterId));
        return showRepository.findByTheaterId(theaterId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public ShowDTO createShow(Long movieId, Long theaterId, LocalDateTime showTime, Integer screen, Long priceInCents) {
        var movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));
        var theater = theaterRepository.findById(theaterId)
            .orElseThrow(() -> new ResourceNotFoundException("Theater not found with ID: " + theaterId));

        Show show = new Show(movie, theater, showTime, 100, priceInCents, screen);
        Show savedShow = showRepository.save(show);

        // Create seats for the show
        createSeatsForShow(savedShow);

        log.info("Show created: {} at {} on {}", movie.getTitle(), theater.getName(), showTime);
        return convertToDTO(savedShow);
    }

    private void createSeatsForShow(Show show) {
        // Create 10x10 seat matrix
        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                Seat seat = new Seat();
                seat.setShow(show);
                seat.setRow(row);
                seat.setColumn(col);
                seat.setSeatNumber(row * 10 + col);
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }
        }
        log.debug("Created 100 seats for show ID: {}", show.getId());
    }

    private ShowDTO convertToDTO(Show show) {
        Integer availableSeats = seatRepository.countByShowIdAndStatus(show.getId(), SeatStatus.AVAILABLE);
        List<SeatDTO> seats = seatRepository.findByShowId(show.getId()).stream()
            .map(seat -> new SeatDTO(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getRow(),
                seat.getColumn(),
                seat.getStatus().toString()
            ))
            .collect(Collectors.toList());

        return new ShowDTO(
            show.getId(),
            show.getMovie().getId(),
            show.getMovie().getTitle(),
            show.getTheater().getId(),
            show.getTheater().getName(),
            show.getShowTime(),
            show.getTotalSeats(),
            show.getPriceInCents(),
            show.getScreen(),
            availableSeats,
            seats
        );
    }
}
