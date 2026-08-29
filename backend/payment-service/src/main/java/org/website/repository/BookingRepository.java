package org.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.model.Payment;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingReference(String bookingReference);
    Optional<Payment> findByBookingReferenceAndUserId(String bookingReference, Long userId);
    Optional<Payment> findByIdAndUserId(Long bookingId, Long userId);
}
