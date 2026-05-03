package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackendService backendService;

    @Test
    void loginShouldSetCookieAndReturnNoContentOnSuccess() throws Exception {
        when(backendService.login(any(AuthRequest.class))).thenReturn(new AuthResponse("tok"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u\",\"password\":\"p\"}"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString(JwtCookieService.COOKIE_NAME + "=tok")));
    }

    @Test
    void loginShouldPropagateBackendHttpStatusAndBody() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                HttpHeaders.EMPTY,
                "bad".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        when(backendService.login(any(AuthRequest.class))).thenThrow(ex);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u\",\"password\":\"p\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("bad"));
    }

    @Test
    void loginShouldReturnServiceUnavailableWhenBackendIsDown() throws Exception {
        when(backendService.login(any(AuthRequest.class))).thenThrow(new ResourceAccessException("down"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u\",\"password\":\"p\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("El servicio de backend no está disponible."));
    }

    @Test
    void loginShouldReturnInternalServerErrorOnUnexpectedException() throws Exception {
        when(backendService.login(any(AuthRequest.class))).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u\",\"password\":\"p\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor"));
    }

    @Test
    void logoutShouldClearCookieAndReturnNoContent() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString(JwtCookieService.COOKIE_NAME + "=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")));
    }

    @Test
    void sessionShouldReturnUnauthorizedWhenTokenMissing() throws Exception {
        mockMvc.perform(get("/api/auth/session"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sessionShouldReturnNoContentWhenTokenPresent() throws Exception {
        mockMvc.perform(get("/api/auth/session")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNoContent());
    }
}
