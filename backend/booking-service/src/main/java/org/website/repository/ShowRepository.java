package org.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.model.Show;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovieId(Long movieId);
    List<Show> findByTheaterId(Long theaterId);
    List<Show> findByMovieIdAndTheaterId(Long movieId, Long theaterId);
    List<Show> findByShowTimeAfterAndShowTimeBeforeAndTheaterIdAndScreenOrderByShowTimeAsc(
        LocalDateTime start, LocalDateTime end, Long theaterId, Integer screen
    );
}
