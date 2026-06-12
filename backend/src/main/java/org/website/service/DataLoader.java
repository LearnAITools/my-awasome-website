package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * Data Loader Service - Initializes test data on application startup.
 * 
 * PURPOSE:
 * This service loads sample data (movies, theaters, shows, users) into the database
 * when the application starts in development/testing environment. This enables:
 * - Immediate testing without manual data population
 * - Demo/POC purposes without needing full admin workflows
 * - Consistency across test environments
 * - Faster development iteration
 * 
 * CONFIGURATION:
 * Add this property to application.properties to control data loading:
 * app.data-loader.enabled=true|false (default: true)
 * 
 * Set to FALSE in production environments:
 * - Prevents accidental data duplication on production
 * - Avoids database pollution with test data
 * - Recommended for production deployments
 * 
 * SAFETY:
 * - Only loads data if database is empty (check: userRepository.count() == 0)
 * - Prevents duplicate data if application restarts
 * - Safe to keep enabled even in production (won't corrupt existing data)
 * 
 * DATA LOADED:
 * 1. Admin User: admin@bookmyshow.com / password
 * 2. Test User: user@example.com / password
 * 3. 3 Theaters: PVR, IMAX, Cineplex
 * 4. 5 Movies: Kalki 2898 AD, Inception, Pushpa, Dark Knight, Avatar
 * 5. 9 Shows: Scheduled across theaters with varied times and prices
 * 6. 900 Seats: 100 seats per show in 10x10 grid format
 * 
 * USAGE IN DEVELOPMENT:
 * 1. Run application with app.data-loader.enabled=true
 * 2. Access admin dashboard with admin@bookmyshow.com / password
 * 3. Create additional test data via UI or modify this file
 * 4. Clear database when needed (H2 console or restart)
 * 
 * USAGE IN PRODUCTION:
 * 1. Set app.data-loader.enabled=false in production application.properties
 * 2. Populate data exclusively through admin UI
 * 3. Maintain clean separation between test and production data
 * 
 * @author BookMyShow Dev Team
 * @version 1.0
 * @since 2026-05-30
 */
@Service
@Slf4j
public class DataLoader implements CommandLineRunner {

    private UserRepository userRepository;
    private MovieRepository movieRepository;
    private TheaterRepository theaterRepository;
    private ShowService showService;
    private PasswordEncoder passwordEncoder;

    DataLoader(UserRepository userRepository, MovieRepository movieRepository, TheaterRepository theaterRepository, ShowService showService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.showService = showService;
        this.passwordEncoder = passwordEncoder;
    }

    String lan = "English";
    String line = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    /**
     * Configuration flag to enable/disable data loading.
     * 
     * Add to application.properties:
     * app.data-loader.enabled=true|false
     * 
     * Default: true (enables data loading for development)
     */
    @Value("${app.data-loader.enabled:true}")
    private boolean dataLoaderEnabled;

    /**
     * CommandLineRunner callback - runs on application startup.
     * 
     * This method is automatically invoked by Spring after application context
     * is initialized. It safely loads test data if:
     * 1. Data loader is enabled via configuration
     * 2. Database is empty (userRepository.count() == 0)
     * 
     * @param args Command line arguments (unused)
     * @throws Exception on any database error
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!dataLoaderEnabled) {
            log.info("🔇 Data loader disabled via configuration (app.data-loader.enabled=false)");
            log.info("💾 Database will be populated exclusively through admin UI or manual SQL");
            return;
        }

        if (userRepository.count() == 0) {
            log.info("📦 Starting data loading - database is empty");
            loadData();
            log.info("✅ Data loading completed successfully!");
        } else {
            log.info("⏭️  Data loading skipped - database already contains {} users", userRepository.count());
        }
    }

    /**
     * Load all sample data into the database.
     * 
     * This method creates:
     * - Admin and test users
     * - Multiple theaters across cities
     * - 5 popular movies
     * - Multiple shows with different times and prices
     * - 100 seats per show (10x10 grid)
     * 
     * All operations are wrapped in @Transactional, so any failure
     * will roll back all changes.
     */
    private void loadData() {
        log.debug("Creating sample users...");
        
        // Create admin user - Full privileges
        User admin = new User(
            "admin@bookmyshow.com",
            passwordEncoder.encode("password"),
            "Admin User",
            UserRole.ROLE_ADMIN
        );
        userRepository.save(admin);
        log.info("✓ Admin user created: admin@bookmyshow.com");

        // Create regular user - Standard user privileges
        User user1 = new User(
            "user@example.com",
            passwordEncoder.encode("password"),
            "John Doe",
            UserRole.ROLE_USER
        );
        userRepository.save(user1);
        log.info("✓ Test user created: user@example.com");

        log.debug("Creating theaters...");
        
        // Create multiple theaters across India
        Theater theater1 = new Theater("PVR Cinemas", "Bengaluru", "123 Main St, Bengaluru", 5);
        Theater theater2 = new Theater("IMAX Theater", "Mumbai", "456 Park Ave, Mumbai", 3);
        Theater theater3 = new Theater("Cineplex", "Delhi", "789 Ring Road, Delhi", 4);
        
        theaterRepository.save(theater1);
        theaterRepository.save(theater2);
        theaterRepository.save(theater3);
        log.info("✓ 3 Theaters created: PVR (Bengaluru), IMAX (Mumbai), Cineplex (Delhi)");

        log.debug("Creating movies...");
        
        // Create diverse movie catalog
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
            lan,
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
            lan,
            "https://via.placeholder.com/300x450?text=DarkKnight",
            "2008-07-18",
            9.0
        );

        Movie movie5 = new Movie(
            "Avatar",
            "An epic science fiction film set on the alien world Pandora",
            "Sci-Fi",
            162,
            lan,
            "https://via.placeholder.com/300x450?text=Avatar",
            "2009-12-18",
            7.8
        );

        movieRepository.save(movie1);
        movieRepository.save(movie2);
        movieRepository.save(movie3);
        movieRepository.save(movie4);
        movieRepository.save(movie5);
        log.info("✓ 5 Movies created: Kalki, Inception, Pushpa, Dark Knight, Avatar");

        log.debug("Creating shows and seats...");
        
        // Create diverse show timings and prices
        LocalDateTime now = LocalDateTime.now();
        
        // Kalki shows
        showService.createShow(movie1.getId(), theater1.getId(), now.plusDays(1).withHour(10).withMinute(0), 1, 50000L);
        showService.createShow(movie1.getId(), theater1.getId(), now.plusDays(1).withHour(13).withMinute(30), 1, 50000L);
        showService.createShow(movie1.getId(), theater1.getId(), now.plusDays(1).withHour(19).withMinute(0), 2, 60000L);
        
        // Inception shows
        showService.createShow(movie2.getId(), theater2.getId(), now.plusDays(2).withHour(14).withMinute(0), 1, 55000L);
        showService.createShow(movie2.getId(), theater2.getId(), now.plusDays(2).withHour(18).withMinute(0), 2, 55000L);
        
        // Pushpa shows
        showService.createShow(movie3.getId(), theater3.getId(), now.plusDays(3).withHour(11).withMinute(0), 1, 45000L);
        showService.createShow(movie3.getId(), theater3.getId(), now.plusDays(3).withHour(20).withMinute(0), 2, 50000L);
        
        // Dark Knight and Avatar shows
        showService.createShow(movie4.getId(), theater1.getId(), now.plusDays(4).withHour(16).withMinute(0), 3, 60000L);
        showService.createShow(movie5.getId(), theater2.getId(), now.plusDays(5).withHour(12).withMinute(0), 1, 55000L);

        log.info("✓ 9 Shows created across theaters with 100 seats each (900 total seats)");
        log.info(line);
        log.info("📊 Sample Data Loaded Successfully!");
        log.info(line);
        log.info("👤 Test Credentials:");
        log.info("   Admin:  admin@bookmyshow.com / password (full access)");
        log.info("   User:   user@example.com / password (standard access)");
        log.info(line);
    }
}
