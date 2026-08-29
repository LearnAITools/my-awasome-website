package org.website.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    private static final int MIN_SECRET_LENGTH = 32;

    private final SecretKey key;
    private final long jwtExpirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration}") long jwtExpirationMs) {
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_LENGTH) {
            throw new IllegalArgumentException("JWT secret must be at least " + MIN_SECRET_LENGTH + " bytes; current jwt.secret is too short.");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(String email, Long userId) {
        return Jwts.builder()
            .subject(email)
            .claim("userId", userId)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(key)
            .compact();
    }

    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("userId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
