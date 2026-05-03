package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    @Test
    void loadUserByUsernameShouldReturnUserWhenFound() {
        User user = new User();
        user.setUsername("carlos");
        when(userRepository.findByUsername("carlos")).thenReturn(user);

        UserDetails details = myUserDetailsService.loadUserByUsername("carlos");

        assertSame(user, details);
    }

    @Test
    void loadUserByUsernameShouldThrowWhenMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> myUserDetailsService.loadUserByUsername("missing"));
    }

    @Test
    void passwordEncoderBeanShouldBeBCryptAndWork() {
        PasswordEncoder encoder = myUserDetailsService.passwordEncoder();
        assertNotNull(encoder);
        String hash = encoder.encode("secret");
        assertTrue(encoder.matches("secret", hash));
    }
}
