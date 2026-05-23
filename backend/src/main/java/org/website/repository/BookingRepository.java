package org.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.model.Booking;
import org.website.model.BookingStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByUserIdOrderByBookingTimeDesc(Long userId);
    List<Booking> findByStatus(BookingStatus status);
    Integer countByShowIdAndStatus(Long showId, BookingStatus status);
}
