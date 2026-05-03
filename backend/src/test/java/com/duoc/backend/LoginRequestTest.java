package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRequestTest {

    @Test
    void constructorAndSettersShouldWork() {
        LoginRequest request = new LoginRequest("carlos", "secret");
        assertEquals("carlos", request.getUsername());
        assertEquals("secret", request.getPassword());

        request.setUsername("ana");
        request.setPassword("p");
        assertEquals("ana", request.getUsername());
        assertEquals("p", request.getPassword());
    }
}
