package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

class SeguridadcalidadApplicationMainTest {

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void mainShouldRunWithoutStartingWebServer() {
        SeguridadcalidadApplication.main(new String[]{
                "--spring.main.web-application-type=none",
                "--spring.main.banner-mode=off",
                "--logging.level.root=OFF"
        });
    }
}
