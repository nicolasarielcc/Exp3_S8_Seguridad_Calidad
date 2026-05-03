package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRegistrationRequestTest {

    @Test
    void gettersAndSettersShouldWork() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("carlos");
        request.setEmail("carlos@duoc.cl");
        request.setPassword("secret");

        assertEquals("carlos", request.getUsername());
        assertEquals("carlos@duoc.cl", request.getEmail());
        assertEquals("secret", request.getPassword());
    }
}
