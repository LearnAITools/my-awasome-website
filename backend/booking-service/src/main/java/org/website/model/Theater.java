package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "theaters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer screens;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    private List<Show> shows;

    public Theater(String name, String city, String address, Integer screens) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.screens = screens;
    }
}
