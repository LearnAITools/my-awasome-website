package org.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.model.Seat;
import org.website.model.SeatStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowId(Long showId);
    List<Seat> findByShowIdAndStatus(Long showId, SeatStatus status);
    Optional<Seat> findByShowIdAndSeatNumber(Long showId, Integer seatNumber);
    Integer countByShowIdAndStatus(Long showId, SeatStatus status);
}
