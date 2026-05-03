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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackendService backendService;

    @Test
    void getAllShouldReturnOk() throws Exception {
        when(backendService.getPets()).thenReturn(List.of(Map.of("id", 1, "name", "Rex")));

        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAvailableShouldReturnOk() throws Exception {
        when(backendService.getAvailablePets()).thenReturn(List.of(Map.of("id", 2, "status", "available")));

        mockMvc.perform(get("/api/pets/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("available"));
    }

    @Test
    void searchShouldForwardQueryParamsAndReturnOk() throws Exception {
        when(backendService.searchPets(eq("Dog"), eq("F"), eq("Santiago"), eq(2), eq("available")))
                .thenReturn(List.of(Map.of("id", 3, "species", "Dog")));

        mockMvc.perform(get("/api/pets/search")
                        .param("species", "Dog")
                        .param("gender", "F")
                        .param("location", "Santiago")
                        .param("age", "2")
                        .param("status", "available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    void createShouldReturnUnauthorizedWhenMissingToken() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createShouldReturnCreatedWhenTokenPresent() throws Exception {
        when(backendService.createPet(eq("t"), anyMap()))
                .thenReturn(Map.of("id", 5, "name", "Luna"));

        mockMvc.perform(post("/api/pets")
                        .header("Authorization", "Bearer t")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Luna\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void updateShouldReturnUnauthorizedWhenMissingToken() throws Exception {
        mockMvc.perform(put("/api/pets/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateShouldReturnOkWhenTokenPresent() throws Exception {
        when(backendService.updatePet(eq("t"), eq(10), anyMap()))
                .thenReturn(Map.of("id", 10, "name", "Updated"));

        mockMvc.perform(put("/api/pets/10")
                        .header("Authorization", "Bearer t")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteShouldReturnUnauthorizedWhenMissingToken() throws Exception {
        mockMvc.perform(delete("/api/pets/10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShouldReturnOkWhenTokenPresent() throws Exception {
        when(backendService.deletePet("t", 10)).thenReturn(Map.of("id", 10, "deleted", true));

        mockMvc.perform(delete("/api/pets/10")
                        .header("Authorization", "Bearer t"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));
    }
}
