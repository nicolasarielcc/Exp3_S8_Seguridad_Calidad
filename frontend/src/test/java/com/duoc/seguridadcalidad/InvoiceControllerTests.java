package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackendService backendService;

    @Test
    void shouldProxyGetInvoices() throws Exception {
  when(backendService.getInvoices("valid-token"))
    .thenReturn(List.of(
      Map.of("id", 1, "appointmentId", 10, "total", 47600.0),
      Map.of("id", 2, "appointmentId", 11, "total", 25000.0)
    ));

  mockMvc.perform(get("/api/invoices")
      .header("Authorization", "Bearer valid-token"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$[0].id").value(1))
    .andExpect(jsonPath("$[1].appointmentId").value(11));
    }

    @Test
    void shouldProxyCreateInvoice() throws Exception {
  when(backendService.createInvoice(
    org.mockito.ArgumentMatchers.eq("valid-token"),
    org.mockito.ArgumentMatchers.eq(10L),
    org.mockito.ArgumentMatchers.anyMap()
  )).thenReturn(Map.of("id", 1, "appointmentId", 10, "total", 47600.0));

        String payload = """
                {
                  "issueDate": "2026-03-28",
                  "vatRate": 0.19,
                  "notes": "Paciente estable. Control en 10 dias.",
                  "items": [
                    {
                      "type": "SERVICE",
                      "description": "Consulta general",
                      "quantity": 1,
                      "unitPrice": 25000
                    },
                    {
                      "type": "MEDICATION",
                      "description": "Antibiotico",
                      "quantity": 2,
                      "unitPrice": 7500
                    }
                  ]
                }
                """;

              mockMvc.perform(post("/api/invoices/appointments/10")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").value(10))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(47600.0));
    }

    @Test
                void shouldProxyGetInvoiceByAppointment() throws Exception {
              when(backendService.getInvoiceByAppointmentId("valid-token", 10L))
                .thenReturn(Map.of("id", 1, "appointmentId", 10, "total", 47600.0));

              mockMvc.perform(get("/api/invoices/appointment/10")
                  .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.appointmentId").value(10));
    }

                @Test
                void shouldProxyGetInvoiceById() throws Exception {
              when(backendService.getInvoiceById("valid-token", 99L))
                .thenReturn(Map.of("id", 99, "appointmentId", 10, "total", 1000.0));

              mockMvc.perform(get("/api/invoices/99")
                  .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99));
                }

    @Test
                void shouldRequireBearerToken() throws Exception {
              mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isUnauthorized());
    }

                @Test
                void shouldRequireBearerTokenForGetById() throws Exception {
              mockMvc.perform(get("/api/invoices/1"))
                .andExpect(status().isUnauthorized());
                }

                @Test
                void shouldRequireBearerTokenForGetByAppointment() throws Exception {
              mockMvc.perform(get("/api/invoices/appointment/10"))
                .andExpect(status().isUnauthorized());
                }

                @Test
                void shouldRequireBearerTokenForCreate() throws Exception {
              mockMvc.perform(post("/api/invoices/appointments/10")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
                .andExpect(status().isUnauthorized());
                }

                @Test
                void getAllShouldReturnServiceUnavailableWhenBackendDown() throws Exception {
              when(backendService.getInvoices("valid-token"))
                .thenThrow(new ResourceAccessException("down"));

              mockMvc.perform(get("/api/invoices")
                  .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("El backend de facturación no está disponible"));
                }

                @Test
                void getAllShouldPropagateBackendHttpStatusAndBody() throws Exception {
              HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                HttpHeaders.EMPTY,
                "nope".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
              );
              when(backendService.getInvoices("valid-token")).thenThrow(ex);

              mockMvc.perform(get("/api/invoices")
                  .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("nope"));
                }

                @Test
                void getByIdShouldReturnServiceUnavailableWhenBackendDown() throws Exception {
              when(backendService.getInvoiceById("valid-token", 1L))
                .thenThrow(new ResourceAccessException("down"));

              mockMvc.perform(get("/api/invoices/1")
                  .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("El backend de facturación no está disponible"));
                }

                @Test
                void createShouldReturnServiceUnavailableWhenBackendDown() throws Exception {
              when(backendService.createInvoice(
                org.mockito.ArgumentMatchers.eq("valid-token"),
                org.mockito.ArgumentMatchers.eq(10L),
                org.mockito.ArgumentMatchers.anyMap()
              )).thenThrow(new ResourceAccessException("down"));

              mockMvc.perform(post("/api/invoices/appointments/10")
                  .header("Authorization", "Bearer valid-token")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("El backend de facturación no está disponible"));
                }
}