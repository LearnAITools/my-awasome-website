package org.website.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private static final String SECRET = "a-secure-test-secret-that-is-at-least-32-bytes";

    @Test
    void generatesTokenWithEmailAndUserIdClaims() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000);

        String token = provider.generateToken("user@example.com", 42L);

        assertTrue(provider.validateToken(token));
        assertEquals("user@example.com", provider.getEmailFromToken(token));
        assertEquals(42L, provider.getUserIdFromToken(token));
    }

    @Test
    void rejectsSecretsShorterThanMinimumLength() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new JwtTokenProvider("too-short", 60_000)
        );

        assertTrue(exception.getMessage().contains("at least 32 bytes"));
    }

    @Test
    void rejectsTamperedToken() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000);
        String token = provider.generateToken("user@example.com", 42L);
        String tamperedToken = token + "x";

        assertFalse(provider.validateToken(tamperedToken));
    }
}
