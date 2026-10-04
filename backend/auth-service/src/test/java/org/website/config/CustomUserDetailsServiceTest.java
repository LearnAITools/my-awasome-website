package org.website.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import org.website.model.User;
import org.website.model.UserRole;
import org.website.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CustomUserDetailsService service = new CustomUserDetailsService();

    CustomUserDetailsServiceTest() {
        ReflectionTestUtils.setField(service, "userRepository", userRepository);
    }

    @Test
    void mapsStoredCredentialsAndRoleToSpringUser() {
        User user = new User("admin@example.com", "encoded-password", "Admin", UserRole.ROLE_ADMIN);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin@example.com");

        assertEquals("admin@example.com", details.getUsername());
        assertEquals("encoded-password", details.getPassword());
        assertEquals("ROLE_ADMIN", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void throwsSpringUsernameNotFoundForUnknownEmail() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@example.com")
        );

        assertEquals("User not found with email: missing@example.com", exception.getMessage());
    }
}