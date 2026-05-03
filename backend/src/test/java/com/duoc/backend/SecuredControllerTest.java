package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecuredControllerTest {

    @Test
    void greetingsShouldUseDefaultValueWhenNullPassed() {
        SecuredController controller = new SecuredController();
        // defaultValue="World" only applies when Spring binds the request parameter.
        // When calling the method directly, null stays null.
        assertEquals("Hello {null}", controller.greetings(null));
    }

    @Test
    void greetingsShouldReturnHelloWithProvidedName() {
        SecuredController controller = new SecuredController();
        assertEquals("Hello {Carlos}", controller.greetings("Carlos"));
    }
}
