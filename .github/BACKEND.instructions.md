---
applyTo: backend/**
description: Backend Java/Spring Boot development guidelines
---

# Backend Development Instructions

## Quick Commands

```bash
cd backend

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Build JAR
./gradlew build

# View dependency tree
./gradlew dependencies

# Clean build artifacts
./gradlew clean
```

## Code Organization

**Package Structure**: `org.website.<layer>`
- `config/` - Spring configuration classes (@Configuration)
- `controller/` - REST endpoints (@RestController)
- `service/` - Business logic (@Service)
- `repository/` - Data access (@Repository)
- `model/` - JPA entities (@Entity)
- `security/` - Auth components (@Component)
- `dto/` - Data Transfer Objects
- `exception/` - Custom exceptions

## Writing Code

### REST Controller
```java
@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService service;
    
    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
```

### Service Class
```java
@Service
@RequiredArgsConstructor
@Transactional  // Use for methods that modify data
@Slf4j
public class ResourceService {
    private final ResourceRepository repository;
    
    public ResourceDTO getById(Long id) {
        return repository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Not found"));
    }
}
```

### Repository Query
```java
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Query("SELECT r FROM Resource r WHERE r.status = :status")
    List<Resource> findByStatus(@Param("status") ResourceStatus status);
}
```

### Exception Handling
Always throw custom exceptions; GlobalExceptionHandler catches them:
```java
throw new ResourceNotFoundException("Resource with ID " + id + " not found");
throw new SeatAlreadyBookedException("Seat already booked");
throw new InvalidPaymentException("Payment verification failed");
```

## Key Patterns

### Optimistic Locking (for Seat, Booking)
```java
@Entity
public class Seat {
    @Version
    private Long version;  // Prevents concurrent modification
}
```

### DTOs for API Contracts
- Never expose JPA entities directly
- Always use DTOs for request/response
- Map entities to DTOs in service layer

### Transactions
- `@Transactional` for read-write operations
- `@Transactional(readOnly=true)` for queries
- Transaction automatically rolls back on exceptions

### Validation
Use `@Valid` and Bean Validation annotations:
```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody ResourceRequest request) {
    return ResponseEntity.ok(service.create(request));
}
```

## Testing

### Unit Tests
```bash
./gradlew test --tests ServiceTest
```

### Integration Tests
Place in `src/test/java/org/website/`

## Dependencies

Key dependencies in [build.gradle](../../backend/build.gradle):
- `spring-boot-starter-web` - REST support
- `spring-boot-starter-data-jpa` - ORM
- `spring-boot-starter-security` - Authentication
- `jjwt` - JWT tokens
- `razorpay-java` - Payment gateway
- `lombok` - Reduce boilerplate

## Configuration

[application.properties](../../backend/src/main/resources/application.properties):
- `server.port` - Default 8080
- `spring.datasource.url` - H2 database connection
- `spring.jpa.hibernate.ddl-auto` - create-drop for dev
- `logging.level.*` - Logging configuration

## Common Issues

- **JWT validation fails**: Check token format and secret key match
- **CORS errors**: Verify SecurityConfig allows frontend origin
- **Seat booking conflict**: Optimistic locking will throw exception on conflict
- **Password encoding**: Always use BCryptPasswordEncoder, never store plaintext
