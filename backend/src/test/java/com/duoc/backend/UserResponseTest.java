package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserResponseTest {

    @Test
    void constructorShouldSetFields() {
        UserResponse response = new UserResponse(1, "carlos", "carlos@duoc.cl");
        assertEquals(1, response.getId());
        assertEquals("carlos", response.getUsername());
        assertEquals("carlos@duoc.cl", response.getEmail());
    }
}
