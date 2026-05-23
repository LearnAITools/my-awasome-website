# Ultimate Multi-Agent Blueprint: BookMyShow Clone
**Tech Stack:** React (Vite) + Java (Spring Boot) + H2 DB + Razorpay + WebSockets

---

## 🤖 AGENT 1: The Project Architect
### Role & Objective
You are a Lead Software Architect. Initialize a clean, modular local project structure separating frontend and backend domains.

### Instructions
1. **Structure:** Create a root folder `ticketing-app` containing `/backend` (Java/Gradle) and `/frontend` (React/Vite).
2. **Backend Config:** Configure `build.gradle` for Java 17, Spring Boot 3+, Spring Data JPA, H2 Database, WebSocket, and Razorpay.
3. **Frontend Config:** Configure `package.json` with React, Tailwind CSS, Axios, and `stompjs` (for real-time sockets).

### Outputs Required
* **Tree Diagram:** The visual file structure.
* **Manifests:** `build.gradle` and `package.json`.

---

## 🤖 AGENT 2: The UI/UX Developer (React Frontend)
### Role & Objective
Build the visual screens and routing structure.

### Instructions
1. **Home Page:** Movie grid with "City" dropdown filter (Bengaluru, Mumbai) and Genre filters.
2. **Movie Details:** A page showing Cast, Crew, and a "Book Tickets" CTA that opens the Showtime modal.
3. **Seat Layout:** A 10x10 grid.
   * **Styles:** Available (White/Green Border), Selected (Solid Green), Booked (Solid Grey).
   * **Tooltip:** Hovering over a seat shows the Price (e.g., "₹250").
4. **Checkout Modal:** A summary sheet showing selected seats and total tax calculation.

### Outputs Required
* `src/pages/Home.jsx`, `src/pages/MovieDetail.jsx`
* `src/components/SeatGrid.jsx`

---

## 🤖 AGENT 3: The Core Engine Engineer (Java Backend)
### Role & Objective
Build the REST API and Real-Time WebSocket Engine.

### Instructions
1. **Entities:** `Movie`, `Showtime`, `Seat` (with `@Version` for locking), `User`, `Booking`.
2. **REST API:**
   * `GET /api/movies?city=Bengaluru`
   * `GET /api/showtimes/{id}/seats` (Returns seat matrix with status).
3. **Real-Time WebSockets (Critical):**
   * Configure `WebSocketConfig` with a `/topic/seats` broker.
   * When a booking is confirmed, broadcast a message: `{ "showtimeId": 1, "bookedSeats": ["A1", "A2"] }`.
4. **Concurrency:** Handle `OptimisticLockingFailureException` to prevent double-booking.

### Outputs Required
* `WebSocketConfig.java`
* `SeatController.java`
* `MovieController.java`

---

## 🤖 AGENT 4: The Payment & Integration Specialist
### Role & Objective
Implement Razorpay Sandbox and link the payment flow.

### Instructions
1. **Backend:**
   * `POST /api/payments/create-order`: Call Razorpay API to get an `order_id`.
   * `POST /api/payments/verify`: Verify the `razorpay_signature` hash.
   * **Transaction:** On success, change Seat Status from `LOCKED` to `BOOKED`.
2. **Frontend:**
   * Integrate `useRazorpay` hook.
   * On "Payment Success", call the backend verify endpoint and redirect to the "Ticket Confirmed" page.

### Outputs Required
* `PaymentService.java`
* `CheckoutLogic.js`

---

## 🤖 AGENT 5: The Specialized UI Architect (State & Sockets)
### Role & Objective
Manage global state, location context, and real-time socket listeners.

### Instructions
1. **Context Layer:**
   * `CityContext`: Stores the user's current city selection (persisted in localStorage).
   * `BookingContext`: Tracks selected seats and a 5-minute countdown timer.
2. **Socket Listener:**
   * Use `stompjs` to listen to `/topic/seats`.
   * **Reactive Update:** If a WebSocket message arrives saying "Seat A1 is Booked", instantly disable Seat A1 in the UI for all viewing users.

### Outputs Required
* `src/context/CityContext.jsx`
* `src/hooks/useSocket.js` (Manages the real-time connection).

---

## 🤖 AGENT 6: The Quality Assurance & Sonar Specialist
### Role & Objective
Ensure code cleanliness, accessibility, and testing.

### Instructions
1. **Backend Quality:** Enforce SLF4J logging, remove `System.out`, and ensure DTO pattern usage (never return Entities directly).
2. **Frontend Quality:** Ensure all images have `alt` tags and buttons have `aria-label`.
3. **Testing:** Generate `SeatServiceTest.java` to simulate 5 users trying to book the same seat simultaneously (Concurrency Test).

### Outputs Required
* `SonarProperties.xml`
* `ConcurrencyTest.java`

---

## 🤖 AGENT 7: The Local Deployment & Automation Engineer
### Role & Objective
Create a single-click "Run" script for the entire stack.

### Instructions
1. **Script:** `start-app.sh` (Mac/Linux) and `start-app.bat` (Windows).
   * Step 1: Start H2 Database.
   * Step 2: `gradle bootRun` (Backend) on Port 8080.
   * Step 3: `npm run dev` (Frontend) on Port 5173.
2. **Health Check:** The script should wait for the Backend to be "UP" before launching the Browser.

### Outputs Required
* `start-app.sh`
* `README.md` (Setup instructions).

---

## 🤖 AGENT 8: The Security & Identity Engineer (Auth Layer)
### Role & Objective
Secure the app with JWT (JSON Web Tokens).

### Instructions
1. **Spring Security:** Implement a `JwtFilter` chain.
   * Public: Home, Movie Details, Login.
   * Protected: Checkout, Payment, Admin Dashboard.
2. **Auth API:** `POST /auth/login` returns a JWT Access Token.
3. **Frontend:** Intercept Axios requests to attach `Authorization: Bearer <token>`.

### Outputs Required
* `JwtUtil.java`
* `SecurityConfig.java`

---

## 🤖 AGENT 9: The Admin Dashboard Developer (Back-Office)
### Role & Objective
Build the interface for Theater Managers to add movies.

### Instructions
1. **Route:** `/admin` (Protected Route).
2. **Features:**
   * **Add Movie:** Form with Title, Duration, and Image Upload.
   * **Schedule Manager:** Dropdown to link a Movie to a Theater and Time.
   * **Sales Dashboard:** A simple chart showing daily revenue.

### Outputs Required
* `src/pages/admin/Dashboard.jsx`
* `src/components/admin/MovieForm.jsx`

---

## 🤖 AGENT 10: The Notification Service Engineer
### Role & Objective
Generate tickets and simulate email delivery.

### Instructions
1. **Ticket Generator:** Create a method to generate a specialized String/QR Code: `BOOK-123-AVENGERS-A1-A2`.
2. **Mock Email Service:** Since this is local, create a service that **Logs to Console**:
   * "📨 EMAIL SENT to [user@email.com]: Your ticket for [Movie] is confirmed. ID: [12345]."
   * This allows debugging without a real SMTP server.

### Outputs Required
* `NotificationService.java`

---

## 🤖 AGENT 11: The Data Seeder & Asset Manager (The Missing Piece)
### Role & Objective
Ensure the app is fully populated with data and images immediately upon startup.

### Instructions
1. **Asset Storage:**
   * Create a folder `user-uploads` in the project root.
   * Create `FileController.java` to serve images from this folder via `GET /images/{filename}`.
2. **Data Seeding (`CommandLineRunner`):**
   * **Check:** If DB is empty ->
   * **Create:** 2 Cities (Bengaluru, Mumbai).
   * **Create:** 5 Movies (e.g., "Kalki 2898 AD", "Inception") using dummy URLs.
   * **Create:** 3 Theaters with 100 Seats each.
   * **Create:** 1 Admin User (`admin@bookmyshow.com` / `password`).
3. **Result:** The user should be able to log in and see movies instantly after running the app.

### Outputs Required
* `DataSeeder.java`
* `FileStorageService.java`
