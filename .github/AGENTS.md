# BookMyShow - AI Agent Guide

A full-stack movie ticket booking application with seat selection, JWT authentication, and Razorpay payment integration.

## Quick Start

### Prerequisites
- **Node.js**: v18 or higher
- **Java**: JDK 17 or higher
- **Git**: Optional

### Build & Run

**Backend (Spring Boot)**
```bash
cd backend
./gradlew bootRun
# Server runs on http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
```

**Frontend (React + Vite)**
```bash
cd frontend
npm install
npm run dev
# Development server: http://localhost:5173
# Automatically proxies /api requests to http://localhost:8080
```

**Full Setup (automated)**
```bash
chmod +x setup-local.sh
./setup-local.sh
```

### Test Credentials
- **Email**: admin@bookmyshow.com
- **Password**: password

---

## Architecture Overview

### Backend Structure

**Location**: `backend/src/main/java/org/website/`

```
config/          → Spring Security, JWT, CORS, Web configurations
controller/      → REST API endpoints for Auth, Movies, Shows, Bookings, Payments
service/         → Business logic layer with transaction management
repository/      → Spring Data JPA repositories for database access
model/           → JPA entities (User, Movie, Show, Booking, Payment, Seat, Theater)
security/        → JWT token generation and authentication filtering
dto/             → Data Transfer Objects for API contracts
exception/       → Custom exceptions and global exception handler
```

**Key Patterns**:
- **Layered Architecture**: Controller → Service → Repository → Model
- **DTOs**: Used for all request/response payloads (never expose entities directly)
- **Exception Handling**: [GlobalExceptionHandler.java](../backend/src/main/java/org/website/exception/GlobalExceptionHandler.java) catches all exceptions
- **Transactions**: Services use `@Transactional` for atomicity
- **Optimistic Locking**: `@Version` on Seat and Booking entities prevents race conditions
- **CORS**: Enabled for `http://localhost:5173` (frontend)

### Frontend Structure

**Location**: `frontend/src/`

```
api/             → Axios HTTP client with JWT interceptor
components/      → Reusable UI components (Header, MovieCard, SeatLayout, etc.)
context/         → AuthContext (user auth state) and BookingContext (booking state)
pages/           → Route pages (HomePage, LoginPage, MovieDetailsPage, MyBookingsPage)
```

**Key Patterns**:
- **Context API**: Two contexts manage global state
  - `AuthContext`: User authentication, token storage
  - `BookingContext`: Show and seat selection state
- **Local Storage**: Auth tokens and user data persisted in localStorage
- **Tailwind CSS**: Utility-first styling with custom color theme
- **Responsive Design**: Mobile-first approach with Tailwind breakpoints

---

## Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Backend Framework** | Spring Boot | 3.3.10 |
| **Backend Language** | Java | 17+ |
| **Build Tool** | Gradle | 8.x |
| **Database** | H2 (in-memory) | 2.2.224 |
| **Security** | Spring Security + JWT | JJWT 0.12.3 |
| **Payments** | Razorpay Java SDK | 1.4.5 |
| **ORM** | Spring Data JPA + Hibernate | 6.x |
| **Frontend Framework** | React | 18.2 |
| **Build Tool** | Vite | 5.0 |
| **Styling** | Tailwind CSS | 3.3 |
| **HTTP Client** | Axios | 1.6 |
| **Router** | React Router | 6.16 |
| **Testing** | Vitest | 0.34.6 |

---

## Key Development Tasks

### Backend Tasks

**Add a new API endpoint**:
1. Create DTO in `dto/` for request/response
2. Create Controller method in `controller/`
3. Add Service method in `service/`
4. Add Repository query if needed in `repository/`
5. Handle exceptions with custom exceptions in `exception/`
6. Test with integration tests

**Add database entity**:
1. Create JPA entity in `model/` with annotations
2. Create Repository in `repository/` extending JpaRepository
3. Create DTO in `dto/` for API serialization
4. Add Service methods if needed
5. Add Controller endpoints

**Database Queries**:
- Custom queries in repositories use `@Query` annotation
- Projections supported via DTOs
- `@EntityGraph` for eager loading relationships

### Frontend Tasks

**Add a new page**:
1. Create component in `pages/`
2. Add Route in [App.jsx](../frontend/src/App.jsx)
3. Use `useAuth()` for auth context
4. Use `useBooking()` for booking context
5. Call API via `apiClient.js` methods

**Add a new component**:
1. Create in `components/`
2. Use Tailwind CSS classes from [tailwind.config.js](../frontend/tailwind.config.js)
3. Accept props for data and callbacks
4. Use Context hooks as needed

**API Integration**:
- All API calls through [apiClient.js](../frontend/src/api/apiClient.js)
- JWT token automatically added to request headers
- Base URL: `http://localhost:8080/api`

---

## Security & Authentication

### JWT Flow
1. User logs in → `POST /api/auth/login`
2. Backend returns JWT token in `AuthResponse`
3. Frontend stores token in localStorage
4. [JwtAuthenticationFilter](../backend/src/main/java/org/website/security/JwtAuthenticationFilter.java) validates on each request
5. `@Secured` or `@PreAuthorize` annotations protect endpoints

### Security Configuration
- [SecurityConfig.java](../backend/src/main/java/org/website/config/SecurityConfig.java): Defines auth rules
- Public endpoints: `/api/auth/**`, `/api/movies`, GET `/api/shows/**`
- Protected endpoints: `/api/bookings/**`, `/api/payments/**`
- Admin endpoints: `/api/admin/**` require ROLE_ADMIN
- CSRF disabled (stateless JWT-based)

### Password Encoding
- Uses BCryptPasswordEncoder
- Always hash passwords before storing

---

## Common Patterns & Dependencies

### Error Responses
All errors follow the [ErrorResponse](../backend/src/main/java/org/website/dto/ErrorResponse.java) DTO:
```json
{
  "message": "User not found",
  "errorCode": "RESOURCE_NOT_FOUND",
  "statusCode": 404,
  "timestamp": "2026-05-29 10:30:00"
}
```

### API Response Format
- **Success**: HTTP 200/201 with response DTO
- **Errors**: HTTP 4xx/5xx with ErrorResponse DTO
- **Pagination**: Not yet implemented; queries return full lists

### Booking Workflow
1. Select show and seats → Sent to `BookingService`
2. Create Booking (status: PENDING, reserved for 5 min)
3. Create Payment Order via Razorpay
4. User verifies payment
5. Update Booking status to CONFIRMED

### Seat Management
- [Seat.java](../backend/src/main/java/org/website/model/Seat.java) has `@Version` for optimistic locking
- Prevents double-booking in concurrent scenarios
- Status: AVAILABLE, BOOKED, RESERVED

---

## Configuration Files

### Backend
- [application.properties](../backend/src/main/resources/application.properties): Database, logging, JWT settings
- [build.gradle](../backend/build.gradle): Dependencies and build configuration
- `security.jwt.secret`: Required in properties (currently uses default)
- `security.jwt.expiration`: Token expiry in milliseconds (default: 24 hours)

### Frontend
- [vite.config.js](../frontend/vite.config.js): Port 5173, API proxy to `/api` → localhost:8080
- [tailwind.config.js](../frontend/tailwind.config.js): Custom colors (primary, secondary, danger, dark)
- [package.json](../frontend/package.json): Scripts: `dev`, `build`, `preview`, `lint`, `test`

---

## Testing

### Backend
```bash
cd backend
./gradlew test           # Run all tests
./gradlew test --tests ClassName  # Run specific test class
```

### Frontend
```bash
cd frontend
npm test                 # Run Vitest
npm run lint            # Run ESLint
```

---

## Known Limitations & Future Enhancements

- **Database**: H2 in-memory; resets on restart (use PostgreSQL for production)
- **Admin Dashboard**: Planned but not yet implemented
- **Pagination**: Currently returns all results
- **Notifications**: Email notifications stub exists ([NotificationService.java](../backend/src/main/java/org/website/service/NotificationService.java))
- **Analytics**: Not implemented

---

## Useful Commands

**Backend**
```bash
# Build JAR
./gradlew build

# Clean
./gradlew clean

# View dependencies
./gradlew dependencies
```

**Frontend**
```bash
# Build for production
npm run build

# Preview production build locally
npm run preview

# Format with Prettier (if installed)
npm run format
```

---

## Deployment

See [deployment/](../deployment/) directory for production configuration.

---

## Additional Resources

- [README.md](../README.md) - Project overview and features
- [Razorpay API Docs](https://razorpay.com/docs/api/)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev/)
- [Tailwind CSS Docs](https://tailwindcss.com/docs)
