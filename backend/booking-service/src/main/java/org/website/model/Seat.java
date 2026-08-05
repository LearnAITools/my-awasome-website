package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats", uniqueConstraints = {@UniqueConstraint(columnNames = {"show_id", "seat_number"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(nullable = false)
    private Integer seatNumber;

    @Column(name = "seat_row", nullable = false)
    private Integer row;

    @Column(name = "seat_column", nullable = false)
    private Integer column;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Version
    private Long version;

    public Seat(Show show, Integer seatNumber, Integer row, Integer column, SeatStatus status) {
        this.show = show;
        this.seatNumber = seatNumber;
        this.row = row;
        this.column = column;
        this.status = status;
    }
}
