package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthRequestTest {

    @Test
    void gettersAndSettersShouldWork() {
        AuthRequest req = new AuthRequest();

        assertNull(req.getUsername());
        assertNull(req.getPassword());

        req.setUsername("user");
        req.setPassword("pass");

        assertEquals("user", req.getUsername());
        assertEquals("pass", req.getPassword());
    }
}
