package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackendService backendService;

    @Test
    void getAllShouldReturnUnauthorizedWhenMissingToken() throws Exception {
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createShouldReturnUnauthorizedWhenMissingToken() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllShouldProxyToBackendServiceWhenBearerTokenPresent() throws Exception {
        when(backendService.getAppointments("t"))
                .thenReturn(List.of(Map.of("id", 10, "date", "2026-05-01")));

        mockMvc.perform(get("/api/appointments")
                        .header("Authorization", "Bearer t"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void createShouldProxyToBackendServiceWhenBearerTokenPresent() throws Exception {
        when(backendService.createAppointment(eq("t"), anyMap()))
                .thenReturn(Map.of("id", 11, "date", "2026-05-01"));

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer t")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-05-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11));
    }
}
