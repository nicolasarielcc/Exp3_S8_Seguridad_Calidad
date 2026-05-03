package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BackendApplicationAnnotationTest {

    @Test
    void backendApplicationShouldBeSpringBootApplication() {
        assertNotNull(BackendApplication.class.getAnnotation(SpringBootApplication.class));
    }
}
