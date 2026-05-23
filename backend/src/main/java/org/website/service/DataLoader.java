package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.model.Movie;
import org.website.model.Show;
import org.website.model.Theater;
import org.website.model.User;
import org.website.model.UserRole;
import org.website.repository.MovieRepository;
import org.website.repository.ShowRepository;
import org.website.repository.TheaterRepository;
import org.website.repository.UserRepository;
import java.time.LocalDateTime;

@Service
@Slf4j
public class DataLoader implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowService showService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            loadData();
        }
    }

    private void loadData() {
        // Create admin user
        User admin = new User(
            "admin@bookmyshow.com",
            passwordEncoder.encode("password"),
            "Admin User",
            UserRole.ROLE_ADMIN
        );
        userRepository.save(admin);
        log.info("Admin user created");

        // Create regular users
        User user1 = new User(
            "user@example.com",
            passwordEncoder.encode("password"),
            "John Doe",
            UserRole.ROLE_USER
        );
        userRepository.save(user1);

        // Create theaters
        Theater theater1 = new Theater("PVR Cinemas", "Bengaluru", "123 Main St, Bengaluru", 5);
        Theater theater2 = new Theater("IMAX Theater", "Mumbai", "456 Park Ave, Mumbai", 3);
        Theater theater3 = new Theater("Cineplex", "Delhi", "789 Ring Road, Delhi", 4);
        
        theaterRepository.save(theater1);
        theaterRepository.save(theater2);
        theaterRepository.save(theater3);
        log.info("Theaters created");

        // Create movies
        Movie movie1 = new Movie(
            "Kalki 2898 AD",
            "A sci-fi action film set in a futuristic world",
            "Sci-Fi",
            165,
            "Telugu",
            "https://via.placeholder.com/300x450?text=Kalki2898",
            "2024-06-27",
            8.5
        );

        Movie movie2 = new Movie(
            "Inception",
            "A mind-bending thriller about dreams within dreams",
            "Thriller",
            148,
            "English",
            "https://via.placeholder.com/300x450?text=Inception",
            "2010-07-16",
            8.8
        );

        Movie movie3 = new Movie(
            "Pushpa",
            "An action drama about a smuggler's rise to power",
            "Action",
            179,
            "Telugu",
            "https://via.placeholder.com/300x450?text=Pushpa",
            "2021-12-17",
            8.2
        );

        Movie movie4 = new Movie(
            "The Dark Knight",
            "A superhero film about Batman fighting the Joker",
            "Action",
            152,
            "English",
            "https://via.placeholder.com/300x450?text=DarkKnight",
            "2008-07-18",
            9.0
        );

        Movie movie5 = new Movie(
            "Avatar",
            "An epic science fiction film set on the alien world Pandora",
            "Sci-Fi",
            162,
            "English",
            "https://via.placeholder.com/300x450?text=Avatar",
            "2009-12-18",
            7.8
        );

        movieRepository.save(movie1);
        movieRepository.save(movie2);
        movieRepository.save(movie3);
        movieRepository.save(movie4);
        movieRepository.save(movie5);
        log.info("Movies created");

        // Create shows
        LocalDateTime now = LocalDateTime.now();
        
        showService.createShow(movie1.getId(), theater1.getId(), now.plusDays(1).withHour(10).withMinute(0), 1, 50000L);
        showService.createShow(movie1.getId(), theater1.getId(), now.plusDays(1).withHour(13).withMinute(30), 1, 50000L);
        showService.createShow(movie1.getId(), theater1.getId(), now.plusDays(1).withHour(19).withMinute(0), 2, 60000L);
        
        showService.createShow(movie2.getId(), theater2.getId(), now.plusDays(2).withHour(14).withMinute(0), 1, 55000L);
        showService.createShow(movie2.getId(), theater2.getId(), now.plusDays(2).withHour(18).withMinute(0), 2, 55000L);
        
        showService.createShow(movie3.getId(), theater3.getId(), now.plusDays(3).withHour(11).withMinute(0), 1, 45000L);
        showService.createShow(movie3.getId(), theater3.getId(), now.plusDays(3).withHour(20).withMinute(0), 2, 50000L);
        
        showService.createShow(movie4.getId(), theater1.getId(), now.plusDays(4).withHour(16).withMinute(0), 3, 60000L);
        showService.createShow(movie5.getId(), theater2.getId(), now.plusDays(5).withHour(12).withMinute(0), 1, 55000L);

        log.info("Shows and seats created - Data seeding completed successfully!");
    }
}
