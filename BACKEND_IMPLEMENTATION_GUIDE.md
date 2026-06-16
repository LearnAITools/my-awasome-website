# TicketToMyShow - Critical Backend Implementation Guide

## Part 1: Missing Java Entities

### 1. Role Entity (NEW)
**File: `backend/src/main/java/org/website/model/Role.java`**

```java
package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;  // ROLE_USER, ROLE_ADMIN
    
    private String description;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
```

---

### 2. Updated User Entity
**File: `backend/src/main/java/org/website/model/User.java`** (MODIFY EXISTING)

```java
package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;  // BCrypt encoded
    
    @Column(nullable = false)
    private String fullName;
    
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    
    // NEW: M-to-M relationship with roles
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Helper method to add role
    public void addRole(Role role) {
        this.roles.add(role);
    }
    
    // Helper method to check if user has role
    public boolean hasRole(String roleName) {
        return this.roles.stream()
            .anyMatch(role -> role.getName().equals(roleName));
    }
}

// NEW: User Status Enum
public enum UserStatus {
    ACTIVE("Active", "User account is active"),
    INACTIVE("Inactive", "User account is inactive"),
    SUSPENDED("Suspended", "User account is suspended"),
    DELETED("Deleted", "User account is deleted");
    
    private String displayName;
    private String description;
    
    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
```

---

### 3. Screen Entity (NEW)
**File: `backend/src/main/java/org/website/model/Screen.java`**

```java
package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "screens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Screen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;
    
    @Column(nullable = false)
    private String screenNumber;  // "Screen 1", "Screen A", etc.
    
    @Column(nullable = false)
    private Integer capacity;  // Total seats
    
    // Features like IMAX, 4DX, 3D, DOLBY_ATMOS
    @ElementCollection
    @CollectionTable(name = "screen_features", joinColumns = @JoinColumn(name = "screen_id"))
    @Column(name = "feature")
    private Set<String> features = new HashSet<>();
    
    @OneToMany(mappedBy = "screen", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Show> shows = new HashSet<>();
    
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @UniqueConstraint(columnNames = {"theater_id", "screen_number"})
    public Screen(Theater theater, String screenNumber, Integer capacity) {
        this.theater = theater;
        this.screenNumber = screenNumber;
        this.capacity = capacity;
    }
    
    public void addFeature(String feature) {
        this.features.add(feature);
    }
    
    public String getFeaturesAsString() {
        return String.join(", ", features);
    }
}

// Feature Enum (Optional)
public enum ScreenFeature {
    IMAX("IMAX", "Ultra-large format screen"),
    FOUR_DX("4DX", "4D motion seating"),
    THREE_D("3D", "Three-dimensional display"),
    DOLBY_ATMOS("DOLBY_ATMOS", "Dolby Atmos sound");
    
    private String code;
    private String description;
    
    ScreenFeature(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
```

---

### 4. ShowSeat Entity (NEW - Replaces Seat)
**File: `backend/src/main/java/org/website/model/ShowSeat.java`**

```java
package org.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "show_seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;
    
    @Column(nullable = false)
    private String seatNumber;  // "A1", "A2", "B1", etc.
    
    @Column(name = "row_num", nullable = false)
    private Integer row;  // 1, 2, 3, etc.
    
    @Column(name = "column_num", nullable = false)
    private Integer column;  // 1, 2, 3, etc.
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType type = SeatType.STANDARD;
    
    @Column(name = "price_in_cents")
    private Integer priceInCents;  // Can override show price
    
    @Column(name = "held_until")
    private LocalDateTime heldUntil;  // Seat hold expiration
    
    @Version
    private Long version;  // CRITICAL: For optimistic locking
    
    // Constructor for new seat
    public ShowSeat(Show show, String seatNumber, Integer row, Integer column, Integer priceInCents) {
        this.show = show;
        this.seatNumber = seatNumber;
        this.row = row;
        this.column = column;
        this.priceInCents = priceInCents;
        this.status = SeatStatus.AVAILABLE;
        this.type = SeatType.STANDARD;
    }
    
    // Check if seat is available for booking
    public boolean isAvailable() {
        return this.status == SeatStatus.AVAILABLE;
    }
    
    // Check if seat hold has expired
    public boolean isHoldExpired() {
        if (this.heldUntil == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.heldUntil);
    }
}

// Status Enum for Seat
public enum SeatStatus {
    AVAILABLE("Available", "Seat can be booked"),
    HELD("Held", "Seat is temporarily held"),
    BOOKED("Booked", "Seat is booked"),
    BLOCKED("Blocked", "Seat is unavailable");
    
    private String displayName;
    private String description;
    
    SeatStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}

// Type Enum for Seat
public enum SeatType {
    STANDARD("Standard", "Regular seat"),
    VIP("VIP", "Premium comfort seat"),
    WHEELCHAIR("Wheelchair", "Wheelchair accessible seat"),
    PREMIUM("Premium", "Premium priced seat");
    
    private String displayName;
    private String description;
    
    SeatType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
```

---

### 5. Updated Show Entity
**File: `backend/src/main/java/org/website/model/Show.java`** (MODIFY EXISTING)

```java
// In existing Show.java, replace:
// @OneToMany(mappedBy = "show")
// private List<Seat> seats;

// WITH:
@OneToMany(mappedBy = "show", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<ShowSeat> seats = new ArrayList<>();

// Also update Theater reference:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "screen_id", nullable = false)  // Changed from theater_id
private Screen screen;  // NEW: Reference Screen instead of Theater

// REMOVE old field:
// @ManyToOne
// private Theater theater;  // REMOVE THIS

// Add helper method:
public Integer getAvailableSeatsCount() {
    return (int) this.seats.stream()
        .filter(seat -> seat.isAvailable())
        .count();
}

public Integer getBookedSeatsCount() {
    return (int) this.seats.stream()
        .filter(seat -> seat.getStatus() == SeatStatus.BOOKED)
        .count();
}
```

---

## Part 2: Missing Repositories

### 1. RoleRepository (NEW)
**File: `backend/src/main/java/org/website/repository/RoleRepository.java`**

```java
package org.website.repository;

import org.website.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
```

---

### 2. ScreenRepository (NEW)
**File: `backend/src/main/java/org/website/repository/ScreenRepository.java`**

```java
package org.website.repository;

import org.website.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    List<Screen> findByTheaterId(Long theaterId);
    List<Screen> findByTheaterIdOrderByScreenNumber(Long theaterId);
}
```

---

### 3. ShowSeatRepository (NEW)
**File: `backend/src/main/java/org/website/repository/ShowSeatRepository.java`**

```java
package org.website.repository;

import org.website.model.ShowSeat;
import org.website.model.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    
    List<ShowSeat> findByShowId(Long showId);
    
    Optional<ShowSeat> findByShowIdAndSeatNumber(Long showId, String seatNumber);
    
    List<ShowSeat> findByShowIdAndStatus(Long showId, SeatStatus status);
    
    // Find expired holds
    List<ShowSeat> findByStatusAndHeldUntilBefore(SeatStatus status, LocalDateTime dateTime);
    
    // Count available seats for a show
    @Query("SELECT COUNT(s) FROM ShowSeat s WHERE s.show.id = ?1 AND s.status = 'AVAILABLE'")
    Integer countAvailableSeatsByShowId(Long showId);
}
```

---

## Part 3: Service Layer Updates

### 1. SeatManagementService (NEW)
**File: `backend/src/main/java/org/website/service/SeatManagementService.java`**

```java
package org.website.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.website.model.SeatStatus;
import org.website.model.ShowSeat;
import org.website.repository.ShowSeatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
public class SeatManagementService {
    
    @Autowired
    private ShowSeatRepository showSeatRepository;
    
    /**
     * CRITICAL: Release expired seat holds
     * Runs every 60 seconds (configurable)
     * Reverts HELD seats back to AVAILABLE if hold has expired
     */
    @Scheduled(fixedRate = 60000)  // Every 60 seconds
    public void releaseExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find all HELD seats with expired hold time
        List<ShowSeat> expiredSeats = showSeatRepository
            .findByStatusAndHeldUntilBefore(SeatStatus.HELD, now);
        
        // Revert them to AVAILABLE
        expiredSeats.forEach(seat -> {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHeldUntil(null);
        });
        
        showSeatRepository.saveAll(expiredSeats);
        
        if (!expiredSeats.isEmpty()) {
            System.out.println("Released " + expiredSeats.size() + " expired seat holds");
        }
    }
    
    /**
     * Hold a seat for a user
     * @param seatId - Seat to hold
     * @param holdDurationMinutes - How long to hold (default: 10)
     * @return true if hold successful, false if already booked
     */
    public boolean holdSeat(Long seatId, int holdDurationMinutes) {
        ShowSeat seat = showSeatRepository.findById(seatId)
            .orElseThrow(() -> new RuntimeException("Seat not found"));
        
        if (!seat.isAvailable()) {
            return false;
        }
        
        seat.setStatus(SeatStatus.HELD);
        seat.setHeldUntil(LocalDateTime.now().plusMinutes(holdDurationMinutes));
        showSeatRepository.save(seat);
        
        return true;
    }
    
    /**
     * Release a held seat (e.g., when payment fails)
     */
    public void releaseSeat(Long seatId) {
        ShowSeat seat = showSeatRepository.findById(seatId)
            .orElseThrow(() -> new RuntimeException("Seat not found"));
        
        if (seat.getStatus() == SeatStatus.HELD) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHeldUntil(null);
            showSeatRepository.save(seat);
        }
    }
    
    /**
     * Book a seat (after successful payment)
     */
    public void bookSeat(Long seatId) {
        ShowSeat seat = showSeatRepository.findById(seatId)
            .orElseThrow(() -> new RuntimeException("Seat not found"));
        
        seat.setStatus(SeatStatus.BOOKED);
        seat.setHeldUntil(null);
        showSeatRepository.save(seat);
    }
}
```

---

### 2. PaymentWebhookService (NEW)
**File: `backend/src/main/java/org/website/service/PaymentWebhookService.java`**

```java
package org.website.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.website.model.*;
import org.website.repository.BookingRepository;
import org.website.repository.PaymentRepository;
import org.website.repository.ShowSeatRepository;

import java.util.List;

@Service
public class PaymentWebhookService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private ShowSeatRepository showSeatRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SeatManagementService seatManagementService;
    
    /**
     * Handle payment success from Razorpay webhook
     */
    public void handlePaymentSuccess(String razorpayPaymentId, String bookingId) {
        // 1. Update Payment record
        Payment payment = paymentRepository.findByRazorpayPaymentId(razorpayPaymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        
        // 2. Update Booking record
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.BOOKED);
        bookingRepository.save(booking);
        
        // 3. Update Seats to BOOKED
        List<ShowSeat> seats = booking.getSeats().stream()
            .map(Object::getId)
            .map(id -> showSeatRepository.findById(id).orElse(null))
            .filter(seat -> seat != null)
            .toList();
        
        seats.forEach(seat -> seatManagementService.bookSeat(seat.getId()));
        
        // 4. Send confirmation email
        notificationService.sendBookingConfirmation(booking);
        
        System.out.println("✓ Payment successful for booking: " + booking.getBookingReference());
    }
    
    /**
     * Handle payment failure from Razorpay webhook
     */
    public void handlePaymentFailure(String razorpayPaymentId) {
        // 1. Update Payment record
        Payment payment = paymentRepository.findByRazorpayPaymentId(razorpayPaymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        
        // 2. Update Booking status
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // 3. Release held seats back to AVAILABLE
        booking.getSeats().forEach(seat -> 
            seatManagementService.releaseSeat(seat.getId())
        );
        
        // 4. Send failure notification
        notificationService.sendPaymentFailedNotification(booking);
        
        System.out.println("✗ Payment failed for booking: " + booking.getBookingReference());
    }
}
```

---

## Part 4: Controller Updates

### 1. Razorpay WebhookController (NEW - CRITICAL)
**File: `backend/src/main/java/org/website/controller/WebhookController.java`**

```java
package org.website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.website.service.PaymentWebhookService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
    
    @Autowired
    private PaymentWebhookService paymentWebhookService;
    
    @Value("${razorpay.secret_key}")
    private String razorpaySecretKey;
    
    /**
     * CRITICAL: Razorpay webhook endpoint for payment events
     * Receives events: payment.authorized, payment.captured, payment.failed
     */
    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(
        @RequestBody String payload,
        @RequestHeader("X-Razorpay-Signature") String signature
    ) {
        try {
            // 1. Verify webhook signature (prevent spoofing)
            if (!verifySignature(payload, signature)) {
                System.err.println("❌ Invalid webhook signature!");
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            // 2. Parse webhook data
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode event = objectMapper.readTree(payload);
            String eventType = event.get("event").asText();
            
            System.out.println("🔔 Received webhook: " + eventType);
            
            // 3. Handle different events
            switch(eventType) {
                case "payment.authorized":
                case "payment.captured":
                    handlePaymentSuccess(event);
                    break;
                case "payment.failed":
                    handlePaymentFailure(event);
                    break;
                case "refund.created":
                    handleRefund(event);
                    break;
                default:
                    System.out.println("⚠ Unhandled event: " + eventType);
            }
            
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            System.err.println("❌ Webhook processing error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }
    
    private void handlePaymentSuccess(JsonNode event) throws Exception {
        JsonNode paymentData = event.get("payload").get("payment").get("entity");
        String razorpayPaymentId = paymentData.get("id").asText();
        String bookingId = paymentData.get("notes").get("booking_id").asText();
        
        System.out.println("✓ Processing payment success: " + razorpayPaymentId);
        paymentWebhookService.handlePaymentSuccess(razorpayPaymentId, bookingId);
    }
    
    private void handlePaymentFailure(JsonNode event) throws Exception {
        JsonNode paymentData = event.get("payload").get("payment").get("entity");
        String razorpayPaymentId = paymentData.get("id").asText();
        
        System.out.println("✗ Processing payment failure: " + razorpayPaymentId);
        paymentWebhookService.handlePaymentFailure(razorpayPaymentId);
    }
    
    private void handleRefund(JsonNode event) {
        // TODO: Implement refund handling
        System.out.println("💰 Refund event received (not yet implemented)");
    }
    
    /**
     * Verify Razorpay webhook signature using HMAC-SHA256
     */
    private boolean verifySignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                razorpaySecretKey.getBytes(), 
                0, 
                razorpaySecretKey.getBytes().length, 
                "HmacSHA256"
            );
            mac.init(secretKey);
            
            byte[] messageBytes = payload.getBytes();
            byte[] hashBytes = mac.doFinal(messageBytes);
            String calculatedSignature = HexFormat.of().formatHex(hashBytes);
            
            boolean isValid = calculatedSignature.equals(signature);
            
            if (!isValid) {
                System.err.println("Signature mismatch!");
                System.err.println("Expected: " + calculatedSignature);
                System.err.println("Got: " + signature);
            }
            
            return isValid;
            
        } catch (Exception e) {
            System.err.println("Error verifying signature: " + e.getMessage());
            return false;
        }
    }
}
```

---

## Part 5: pom.xml Dependencies Update

Add to `backend/pom.xml`:

```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
    <scope>runtime</scope>
</dependency>

<!-- Flyway for Database Migrations -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.3</version>
</dependency>

<!-- QR Code Generation -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.3</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.3</version>
</dependency>

<!-- Email Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Thymeleaf for Email Templates -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

---

## Part 6: application.properties Update

Update `backend/src/main/resources/application.properties`:

```properties
# APPLICATION INFO
app.name=TicketToMyShow
app.version=1.0.0
app.description=Movie Ticket Booking Platform

# DATABASE: PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/tickettomyshow
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Flyway Configuration (Database Migrations)
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baselineOnMigrate=true

# JWT Configuration
jwt.expiration=86400000
jwt.secret=your_super_secret_key_change_this_in_production

# Razorpay Configuration (Replace with your keys)
razorpay.key_id=your_razorpay_key_id
razorpay.secret_key=your_razorpay_secret_key
razorpay.webhook_secret=your_webhook_secret

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Seat Hold Configuration (in minutes)
seat.hold.duration=10

# Server Configuration
server.port=8080
server.servlet.context-path=/api
logging.level.root=INFO
logging.level.org.website=DEBUG
```

---

## Part 7: Flyway Migration File

Create: `backend/src/main/resources/db/migration/V1__Initial_schema.sql`

[See DATABASE_SCHEMA_POSTGRESQL.sql]

---

## Implementation Checklist

- [ ] Create all 4 new entities (Role, Screen, ShowSeat, updated User)
- [ ] Create all 3 new repositories
- [ ] Create SeatManagementService with scheduled task
- [ ] Create PaymentWebhookService
- [ ] Create WebhookController
- [ ] Update pom.xml with new dependencies
- [ ] Update application.properties
- [ ] Create Flyway migration scripts
- [ ] Create Flyway migration directory
- [ ] Run database migration
- [ ] Update existing Show entity
- [ ] Test seat hold timeout
- [ ] Test payment webhook
- [ ] Test booking flow end-to-end
- [ ] Verify all tests pass

---

## Critical Business Rules Implementation

```java
// 1. Seat Hold Timeout (10 minutes)
LocalDateTime heldUntil = LocalDateTime.now().plusMinutes(10);
seat.setHeldUntil(heldUntil);

// 2. Optimistic Locking (Prevent double-booking)
@Version private Long version;  // On ShowSeat entity

// 3. Payment Webhook Validation
verifySignature(payload, signature);  // HMAC-SHA256

// 4. Seat Hold Release (Scheduled)
@Scheduled(fixedRate = 60000)
public void releaseExpiredHolds() { ... }
```

---

## Timeline Estimate

- Role & Screen entities: 1 hour
- ShowSeat entity & refactoring: 1.5 hours
- Repositories: 30 minutes
- Services (Seat Management, Webhook): 2 hours
- WebhookController: 1 hour
- Database schema & Flyway: 1 hour
- Testing: 2 hours

**Total: ~9 hours**
