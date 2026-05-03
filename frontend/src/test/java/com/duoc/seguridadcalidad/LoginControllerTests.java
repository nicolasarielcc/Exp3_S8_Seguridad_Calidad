package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginControllerTests {

    private final LoginController controller = new LoginController();

    @Test
    void loginShouldReturnLoginView() {
        assertEquals("login", controller.login());
    }
}
