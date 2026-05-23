package org.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.model.Movie;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByGenreContainingIgnoreCase(String genre);
    List<Movie> findByLanguageContainingIgnoreCase(String language);
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
