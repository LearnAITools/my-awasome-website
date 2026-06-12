# BookMyShow - Movie Ticket Booking Platform

Enterprise-grade full-stack movie ticket booking application with Spring Boot 3.4.0, React + Vite, and Razorpay integration.

**Tech Stack:**
- **Backend:** Java 17, Spring Boot 3.4.0, Maven, H2 Database, Spring Security + JWT
- **Frontend:** React 18.2, Vite 5.0, Tailwind CSS, Axios
- **Payment:** Razorpay Sandbox API
- **Database:** H2 (in-memory, auto-create schema)
- **Code Quality:** SonarQube, JUnit 5, Mockito, JaCoCo (85%+ coverage target)

## ✨ Features

- **Movie Browsing**: Search and filter movies by genre, language, and release date
- **Show Management**: View available shows with interactive seat matrix
- **Seat Selection**: 10x10 interactive grid with real-time availability
- **User Authentication**: JWT-based signup/login with role-based access control
- **Payment Integration**: Razorpay Sandbox with HMAC-SHA256 signature verification
- **Booking Management**: Create, view, and cancel bookings with reference tracking
- **Admin Features**: Add movies, schedule shows, view analytics
- **Optimistic Locking**: Prevent double-booking via @Version concurrency control
- **Error Tracking**: Standardized error codes (ERRxxx) for debugging
- **Responsive Design**: Mobile-first UI with Tailwind CSS

## 🚀 Quick Start

### Prerequisites
- **Java**: JDK 17 or higher (tested with Java 25)
- **Maven**: 3.8.0 or higher
- **Node.js**: v18 or higher
- **IDE**: IntelliJ IDEA (recommended - has built-in Lombok support)

### Installation

#### Option 1: Automated Setup (macOS/Linux) - RECOMMENDED
```bash
chmod +x setup-local.sh
./setup-local.sh
```

This automatically verifies dependencies and starts both servers.

#### Option 2: Manual Setup

**Backend Setup (Maven):**
```bash
cd backend
mvn clean compile spring-boot:run
```

**Frontend Setup (new terminal):**
```bash
cd frontend
npm install
npm run dev
```
npm install
npm run dev
```

#### Option 3: Windows Setup
```bash
setup-local.bat
```

### Access the Application
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

## 🔐 Authentication

### Test Credentials
- **Email**: admin@bookmyshow.com
- **Password**: password

Alternatively, you can create a new account through the signup page.

## 📁 Project Structure

```
my-awasome-website/
├── backend/                          # Spring Boot Backend
│   ├── src/main/java/org/website/
│   │   ├── config/                   # Security & Web Configuration
│   │   ├── controller/               # REST Endpoints
│   │   ├── model/                    # JPA Entities
│   │   ├── repository/               # Data Access Layer
│   │   ├── service/                  # Business Logic
│   │   ├── security/                 # JWT & Authentication
│   │   ├── exception/                # Error Handling
│   │   └── dto/                      # Data Transfer Objects
│   ├── src/main/resources/
│   │   └── application.properties    # Configuration
│   └── build.gradle                  # Gradle Build File
│
├── frontend/                         # React Vite Frontend
│   ├── src/
│   │   ├── api/                      # API Client & Endpoints
│   │   ├── components/               # Reusable React Components
│   │   ├── context/                  # React Context (Auth, Booking)
│   │   ├── pages/                    # Page Components
│   │   ├── App.jsx                   # Main App Component
│   │   ├── main.jsx                  # Entry Point
│   │   └── index.css                 # Global Styles
│   ├── index.html                    # HTML Template
│   ├── vite.config.js                # Vite Configuration
│   ├── tailwind.config.js            # Tailwind Configuration
│   └── package.json                  # Dependencies
│
├── setup-local.sh                    # macOS/Linux Setup Script
├── setup-local.bat                   # Windows Setup Script
└── README.md                         # This File
```

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login user

### Movies
- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie details
- `GET /api/movies/search?title=xyz` - Search movies
- `GET /api/movies/genre/{genre}` - Filter by genre
- `GET /api/movies/language/{language}` - Filter by language

### Shows
- `GET /api/shows` - Get all shows
- `GET /api/shows/{id}` - Get show details
- `GET /api/shows/movie/{movieId}` - Get shows by movie
- `GET /api/shows/theater/{theaterId}` - Get shows by theater

### Bookings (Authenticated)
- `POST /api/bookings` - Create booking
- `GET /api/bookings/{id}` - Get booking details
- `GET /api/bookings/reference/{reference}` - Get by reference
- `GET /api/bookings/user` - Get user's bookings
- `DELETE /api/bookings/{id}` - Cancel booking

### Payments (Authenticated)
- `POST /api/payments/create-order` - Create Razorpay order
- `POST /api/payments/verify` - Verify payment

## 💾 Database

The application uses H2 embedded database which auto-initializes on startup.

### Access H2 Console
1. Navigate to http://localhost:8080/h2-console
2. Connection URL: `jdbc:h2:mem:bookmyshow`
3. Username: `sa`
4. Password: (leave empty)

### Sample Data
The application automatically seeds the database with:
- 1 Admin user + 1 regular user
- 5 Movies (Kalki 2898 AD, Inception, Pushpa, The Dark Knight, Avatar)
- 3 Theaters (PVR, IMAX, Cineplex)
- 9+ Shows across different times
- 100 seats per show (10x10 matrix)

## 🔒 Security Features

- **JWT Authentication**: Stateless authentication using JWT tokens
- **Optimistic Locking**: @Version annotation prevents race conditions
- **Password Hashing**: BCrypt for secure password storage
- **CORS Configuration**: Properly configured for frontend-backend communication
- **Role-Based Access**: ROLE_USER and ROLE_ADMIN support

## 💳 Payment Integration

Uses Razorpay Sandbox/Test Mode:
- Test Key ID and Key Secret needed in `application.properties`
- Razorpay signature verification implemented
- Transaction logging and audit trail

## 🧪 Testing

### Backend Tests
```bash
cd backend
./gradlew test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 🐛 Troubleshooting

### Backend Won't Start
- Check if port 8080 is already in use
- Verify Java 17+ is installed: `java -version`
- Check logs: `tail -f backend.log` (if using setup script)

### Frontend Won't Start
- Check if port 5173 is already in use
- Clear node_modules: `cd frontend && rm -rf node_modules && npm install`
- Check logs: `tail -f frontend.log` (if using setup script)

### H2 Console Connection Issues
- Ensure backend is running
- Use correct connection URL: `jdbc:h2:mem:bookmyshow`
- Refresh the page if data doesn't appear

### Razorpay Payment Issues
- Verify Razorpay keys are set in `application.properties`
- Use Razorpay test credentials
- Check browser console for JavaScript errors

## 📝 Environment Configuration

### Backend (application.properties)
```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:bookmyshow
jwt.secret=your-secret-key
jwt.expiration=86400000
razorpay.key-id=your_key_id
razorpay.key-secret=your_key_secret
```

### Frontend (vite.config.js)
```javascript
server: {
  port: 5173,
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

## 🚢 Deployment

### Production Checklist
- [ ] Update JWT secret in `application.properties`
- [ ] Configure production database (MySQL/PostgreSQL)
- [ ] Add Razorpay production credentials
- [ ] Enable HTTPS
- [ ] Configure CORS for production domains
- [ ] Set up logging and monitoring
- [ ] Run security audit and code analysis
- [ ] Build frontend: `cd frontend && npm run build`
- [ ] Build backend: `cd backend && ./gradlew build`

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [Tailwind CSS Documentation](https://tailwindcss.com)
- [Razorpay Integration Guide](https://razorpay.com/docs/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7519)

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/amazing-feature`
2. Commit your changes: `git commit -m 'Add amazing feature'`
3. Push to the branch: `git push origin feature/amazing-feature`
4. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Developer Notes

### Code Quality
- SonarQube compliance: Zero issues in production code
- Test coverage: Target >85% for services
- Code style: Follows Spring Boot and React conventions

### Performance
- Optimistic locking prevents race conditions
- Lazy loading for related entities
- Frontend pagination for large datasets
- Caching strategies implemented

### Scalability
- Stateless JWT authentication
- Database-agnostic JPA layer
- Microservices-ready architecture
- Docker-ready (coming soon)

## 📞 Support

For issues, questions, or suggestions:
1. Check the Troubleshooting section above
2. Review the backend.log and frontend.log files
3. Open an issue on GitHub
4. Contact the development team

---

**Happy Booking! 🎬🎟️**
