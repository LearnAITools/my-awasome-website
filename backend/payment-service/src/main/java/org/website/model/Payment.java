package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String razorpayOrderId;

    @Column(unique = true)
    private String razorpayPaymentId;

    @Column
    private String razorpaySignature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amountInCents;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;

    public Payment(String bookingReference, Long userId, String razorpayOrderId, Long amountInCents, PaymentStatus status) {
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.razorpayOrderId = razorpayOrderId;
        this.amountInCents = amountInCents;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}
