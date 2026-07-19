# TicketToMyShow - Security Hardening & Rename Guide

## PART 1: APPLICATION RENAME (Cinemax → TicketToMyShow)

### Files to Update

#### Backend Changes

**1. pom.xml**
```xml
<name>TicketToMyShow</name>
<description>Movie Ticket Booking Platform - Book tickets online</description>
<artifactId>tickettomyshow</artifactId>
```

**2. application.properties**
```properties
app.name=TicketToMyShow
app.description=Book movie tickets online for your favorite shows
```

**3. Constants (if any class constants exist)**
```java
// org/website/constant/AppConstants.java
public class AppConstants {
    public static final String APP_NAME = "TicketToMyShow";
    public static final String APP_DESCRIPTION = "Movie Ticket Booking Platform";
}
```

#### Frontend Changes

**1. frontend/package.json**
```json
{
  "name": "tickettomyshow-frontend",
  "description": "Movie ticket booking web application",
  "version": "1.0.0"
}
```

**2. frontend/app/layout.tsx**
```typescript
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'TicketToMyShow - Book Movie Tickets Online',
  description: 'Reserve your favorite movie tickets with TicketToMyShow. Easy, fast, and secure booking.',
  keywords: 'movie tickets, cinema tickets, book tickets online, TicketToMyShow',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <SiteHeader />
        {children}
        <SiteFooter />
      </body>
    </html>
  );
}
```

**3. frontend/components/site-header.tsx** (Update brand name)
```typescript
<Link href="/" className="flex items-center gap-2">
  <span className="text-2xl font-bold text-red-600">
    🎬 TicketToMyShow
  </span>
</Link>
```

**4. frontend/components/site-footer.tsx** (Update footer)
```typescript
<div className="text-center text-sm text-gray-600">
  <p>&copy; 2024 TicketToMyShow. All rights reserved.</p>
  <p>Book your favorite movie tickets online.</p>
</div>
```

**5. frontend/.env.local** (Environment variables)
```env
NEXT_PUBLIC_APP_NAME=TicketToMyShow
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

#### Documentation Files

**1. README.md** (Root)
```markdown
# TicketToMyShow - Movie Ticket Booking Platform

Book your favorite movie tickets online with TicketToMyShow.

## Features

- 🎬 Browse and book movie tickets
- 💳 Secure payment processing via Razorpay
- 🎟️ Digital tickets with QR codes
- 🔒 JWT-based authentication
- 📱 Responsive web design

## Tech Stack

- **Frontend**: Next.js 16.2.6 + React 19 + TypeScript
- **Backend**: Spring Boot 3.4.0 + Java 17
- **Database**: PostgreSQL
- **Payment**: Razorpay
- **Authentication**: JWT

## Getting Started

[Setup instructions...]
```

**2. backend/pom.xml**
```xml
<name>TicketToMyShow Backend</name>
<description>REST API for TicketToMyShow movie ticket booking platform</description>
<artifactId>tickettomyshow-backend</artifactId>
```

#### Package Rename (Optional but Recommended)

If you want to rename the package from `org.website` to `org.tickettomyshow`:

```bash
# Terminal commands (one-time operation)
cd backend/src/main/java

# Create new package structure
mkdir -p org/tickettomyshow/{model,service,repository,controller,config,security,dto,exception,constant}

# Move all files (use IDE's "Refactor > Rename Package" for safety)
# Or manually copy and update imports

# Update all import statements from:
# import org.website.* 
# TO:
# import org.tickettomyshow.*
```

**Note**: This is a major refactoring. Use IDE's refactoring tools (Ctrl+Shift+R in IntelliJ) rather than manual file operations.

---

## PART 2: SECURITY HARDENING

### 🔴 CRITICAL - Must Implement Immediately

#### 1. HTTPS/TLS Configuration

**Development (Self-signed certificate)**
```properties
# application.properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

**Generate self-signed certificate (development only)**
```bash
keytool -genkeypair -alias tomcat \
  -keyalg RSA -keysize 2048 \
  -keystore keystore.p12 -keystore-type PKCS12 \
  -validity 365 -storepass password
```

**Production (Use real certificate)**
```properties
# Use certificate from Let's Encrypt or commercial CA
server.ssl.key-store=file:/etc/ssl/certs/tickettomyshow.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
```

---

#### 2. JWT Token Invalidation on Logout

**Create TokenBlacklist Service**
```java
// backend/src/main/java/org/website/service/TokenBlacklistService.java
package org.website.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.website.security.JwtTokenProvider;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    private static final String BLACKLIST_PREFIX = "blacklist:";
    
    /**
     * Add token to blacklist on logout
     */
    public void addToBlacklist(String token) {
        try {
            // Get token expiration time
            long expirationTime = jwtTokenProvider.getExpirationTime(token);
            long ttl = expirationTime - System.currentTimeMillis();
            
            if (ttl > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "revoked", ttl, TimeUnit.MILLISECONDS);
                System.out.println("✓ Token added to blacklist");
            }
        } catch (Exception e) {
            System.err.println("Error adding token to blacklist: " + e.getMessage());
        }
    }
    
    /**
     * Check if token is blacklisted
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
```

**Update JwtAuthenticationFilter**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null && tokenBlacklistService.isBlacklisted(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked");
            return;
        }
        
        // ... rest of filter logic
        filterChain.doFilter(request, response);
    }
}
```

**Add Logout Endpoint**
```java
@PostMapping("/logout")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);  // Remove "Bearer "
    tokenBlacklistService.addToBlacklist(token);
    
    return ResponseEntity.ok(new GenericResponse("Logged out successfully"));
}
```

---

#### 3. JWT Refresh Token Implementation

**Create RefreshToken Entity**
```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
```

**Create Refresh Token Service**
```java
@Service
public class RefreshTokenService {
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Value("${jwt.refresh_expiration:604800000}")
    private long refreshTokenDurationMs;
    
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }
    
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token expired");
        }
        return token;
    }
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
```

**Add Refresh Endpoint**
```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();
    
    RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));
    
    User user = refreshToken.getUser();
    String newAccessToken = jwtTokenProvider.generateToken(user);
    
    return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken.getToken()));
}
```

---

#### 4. Move Sensitive Config to Environment Variables

**application.properties** (Safe to commit)
```properties
app.name=TicketToMyShow

# Security - use environment variables
jwt.secret=${JWT_SECRET:change_this_in_production}
jwt.expiration=${JWT_EXPIRATION:86400000}

razorpay.key_id=${RAZORPAY_KEY_ID}
razorpay.secret_key=${RAZORPAY_SECRET_KEY}

spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

**Docker environment or .env file (DO NOT commit)**
```env
JWT_SECRET=your_super_secret_key_minimum_32_characters
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

RAZORPAY_KEY_ID=rzp_test_xxxxx
RAZORPAY_SECRET_KEY=xxxxx

DB_USERNAME=postgres
DB_PASSWORD=secure_password_here

SSL_KEYSTORE_PASSWORD=keystore_password
```

---

#### 5. Request Rate Limiting

**Add Spring Cloud to pom.xml**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
    <version>3.1.1</version>
</dependency>

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>
```

**Rate Limiting Config**
```java
@Configuration
public class RateLimitingConfig {
    
    @Bean
    public RateLimiter authRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .limitForPeriod(5)  // 5 requests per minute
            .timeoutDuration(Duration.ofSeconds(1))
            .build();
        
        return RateLimiter.of("authLimiter", config);
    }
}
```

**Apply to Login Endpoint**
```java
@PostMapping("/login")
@RateLimiter(name = "authRateLimiter")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    // ... login logic
}
```

---

### 🟠 HIGH - Strongly Recommended

#### 6. Password Strength Policy

**Password Validator**
```java
@Service
public class PasswordValidator {
    
    public void validate(String password) {
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain lowercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("Password must contain digit");
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            throw new ValidationException("Password must contain special character");
        }
    }
}
```

**Use in SignupService**
```java
@Service
public class AuthService {
    
    @Autowired
    private PasswordValidator passwordValidator;
    
    public User signup(SignupRequest request) {
        // Validate password strength
        passwordValidator.validate(request.getPassword());
        
        // ... rest of signup logic
    }
}
```

---

#### 7. Password Reset Flow

**PasswordResetToken Entity**
```java
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToOne
    @JoinColumn(nullable = false)
    private User user;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
```

**Password Reset Endpoints**
```java
@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        
        passwordResetTokenRepository.save(resetToken);
        
        // Send email with reset link
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        
        return ResponseEntity.ok("Password reset email sent");
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));
        
        if (token.isExpired()) {
            throw new InvalidTokenException("Reset token has expired");
        }
        
        // Validate new password
        passwordValidator.validate(request.getNewPassword());
        
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        passwordResetTokenRepository.delete(token);
        
        return ResponseEntity.ok("Password reset successfully");
    }
}
```

---

#### 8. Audit Logging

**AuditLog Entity** (Already in schema)

**Audit Aspect**
```java
@Aspect
@Component
public class AuditLoggingAspect {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Around("@annotation(org.website.annotation.Auditable)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        User currentUser = authenticationService.getCurrentUser();
        String action = joinPoint.getSignature().getName();
        
        try {
            Object result = joinPoint.proceed();
            
            // Log successful action
            AuditLog log = new AuditLog();
            log.setUser(currentUser);
            log.setAction(action);
            log.setDetails("Success");
            auditLogRepository.save(log);
            
            return result;
        } catch (Exception e) {
            // Log failed action
            AuditLog log = new AuditLog();
            log.setUser(currentUser);
            log.setAction(action);
            log.setDetails("Failed: " + e.getMessage());
            auditLogRepository.save(log);
            
            throw e;
        }
    }
}
```

---

### 🟡 MEDIUM - Good to Have

#### 9. CSRF Token Protection

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .// ... rest of config
    }
}
```

#### 10. Input Validation

```java
public class LoginRequest {
    @Email(message = "Email should be valid")
    @NotBlank
    private String email;
    
    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

#### 11. SQL Injection Prevention

Already handled by using JPA repositories with parameterized queries. Ensure no native SQL with string concatenation.

---

## PART 3: Security Checklist

### Before Production Deployment

- [ ] HTTPS/TLS configured with valid certificate
- [ ] JWT token invalidation implemented
- [ ] Refresh token mechanism added
- [ ] Sensitive configs moved to environment variables
- [ ] Rate limiting on auth endpoints
- [ ] Password strength policy enforced
- [ ] Password reset flow implemented
- [ ] Audit logging enabled
- [ ] CSRF token protection added
- [ ] Input validation on all endpoints
- [ ] SQL injection protection verified
- [ ] Secrets not in source code
- [ ] Security headers configured (HSTS, X-Frame-Options, etc.)
- [ ] CORS properly configured
- [ ] Razorpay keys secured (environment variables)
- [ ] Database user has limited privileges
- [ ] No debug logs in production
- [ ] Error responses don't leak sensitive info

---

## PART 4: Security Headers

**Add to SecurityConfig**
```java
@Configuration
public class SecurityHeadersConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers()
                .httpStrictTransportSecurity()
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                .and()
                .xssProtection()
                .and()
                .contentSecurityPolicy("default-src 'self'")
                .and()
                .frameOptions().deny();
        
        return http.build();
    }
}
```

---

## Timeline Estimate

**Security Implementation Timeline**:
- HTTPS/TLS: 1 hour
- JWT invalidation: 1.5 hours
- Refresh tokens: 1 hour
- Environment variables: 30 minutes
- Rate limiting: 1 hour
- Password strength: 1 hour
- Password reset: 1.5 hours
- Audit logging: 1 hour

**Total: ~9 hours**

---

**NEXT STEPS**:
1. Implement critical security features first (HTTPS, JWT invalidation, environment variables)
2. Then high-priority features (refresh tokens, rate limiting, password policies)
3. Then medium-priority features (audit logging, CSRF, headers)
4. Conduct security audit before production deployment
