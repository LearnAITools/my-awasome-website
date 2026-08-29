package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Deprecated(since = "2026-08-29", forRemoval = true)
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private Long totalAmountInCents;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    @Column
    private LocalDateTime paymentTime;

    @Column
    private String paymentOrderId;

    @Column
    private String qrCode;

    @ManyToMany
    @JoinTable(
        name = "booking_seats",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private List<Seat> seats;

    public Booking(User user, Show show, String bookingReference, BookingStatus status, Long totalAmountInCents) {
        this.user = user;
        this.show = show;
        this.bookingReference = bookingReference;
        this.status = status;
        this.totalAmountInCents = totalAmountInCents;
        this.bookingTime = LocalDateTime.now();
    }
}
