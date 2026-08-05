package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private Integer duration; // in minutes

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String posterUrl;

    @Column(nullable = false)
    private String releaseDate;

    @Column(nullable = false)
    private Double rating;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Show> shows;

    public Movie(String title, String description, String genre, Integer duration, String language, String posterUrl, String releaseDate, Double rating) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.language = language;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }
}
