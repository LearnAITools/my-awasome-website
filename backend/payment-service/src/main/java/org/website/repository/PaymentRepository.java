package org.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.model.Payment;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRazorpayOrderId(String orderId);
    Optional<Payment> findByRazorpayOrderIdAndUserId(String orderId, Long userId);
    Optional<Payment> findByRazorpayPaymentId(String paymentId);
    Optional<Payment> findByBookingReference(String bookingReference);
    Optional<Payment> findByBookingReferenceAndUserId(String bookingReference, Long userId);
}
