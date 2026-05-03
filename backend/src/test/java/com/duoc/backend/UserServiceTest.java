package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUserShouldThrowWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(null));
    }

    @Test
    void registerUserShouldThrowWhenRequiredFieldsBlank() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(" ");
        request.setEmail(" ");
        request.setPassword(" ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
        assertEquals("Username, email and password are required", ex.getMessage());
    }

    @Test
    void registerUserShouldThrowWhenUsernameAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("carlos");
        request.setEmail("carlos@duoc.cl");
        request.setPassword("secret");

        when(userRepository.existsByUsername("carlos")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
        assertEquals("Username already exists", ex.getMessage());
    }

    @Test
    void registerUserShouldTrimAndEncodePasswordAndSave() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("  carlos ");
        request.setEmail("  carlos@duoc.cl ");
        request.setPassword("secret");

        when(userRepository.existsByUsername("  carlos ")).thenReturn(false);
        when(userRepository.existsByEmail("  carlos@duoc.cl ")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("$2a$10$hashed");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(request);

        assertNotNull(saved);
        assertEquals("carlos", saved.getUsername());
        assertEquals("carlos@duoc.cl", saved.getEmail());
        assertEquals("$2a$10$hashed", saved.getPassword());
        verify(userRepository).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void authenticateShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(null);

        assertFalse(userService.authenticate("missing", "secret"));
    }

    @Test
    void authenticateShouldReturnFalseWhenRawPasswordBlank() {
        User user = new User();
        user.setUsername("carlos");
        user.setPassword("plain");
        when(userRepository.findByUsername("carlos")).thenReturn(user);

        assertFalse(userService.authenticate("carlos", " "));
    }

    @Test
    void authenticateShouldUsePasswordEncoderForHashedPassword() {
        User user = new User();
        user.setUsername("carlos");
        String bcryptHash = "$2a$10$" + "a".repeat(53);
        user.setPassword(bcryptHash);
        when(userRepository.findByUsername("carlos")).thenReturn(user);
        when(passwordEncoder.matches("secret", bcryptHash)).thenReturn(true);

        assertTrue(userService.authenticate("carlos", "secret"));
        verify(passwordEncoder).matches("secret", user.getPassword());
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void authenticateShouldMigratePlainTextPasswordWhenMatches() {
        User user = new User();
        user.setUsername("carlos");
        user.setPassword("secret");
        when(userRepository.findByUsername("carlos")).thenReturn(user);
        when(passwordEncoder.encode("secret")).thenReturn("$2a$10$hashed");

        assertTrue(userService.authenticate("carlos", "secret"));
        assertEquals("$2a$10$hashed", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void migratePlainTextPasswordsShouldEncodeOnlyNonHashedNonBlank() {
        User blank = new User();
        blank.setPassword("  ");
        User hashed = new User();
        hashed.setPassword("$2a$10$" + "a".repeat(53));
        User plain = new User();
        plain.setPassword("plain");

        when(userRepository.findAll()).thenReturn(List.of(blank, hashed, plain));
        when(passwordEncoder.encode("plain")).thenReturn("$2a$10$hashed");

        int migrated = userService.migratePlainTextPasswords();

        assertEquals(1, migrated);
        verify(userRepository).save(plain);
    }
}
