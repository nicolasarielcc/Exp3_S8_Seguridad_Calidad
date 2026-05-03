package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private InvoiceController invoiceController;

    @Test
    void createInvoiceShouldReturnBadRequestWhenAppointmentMissing() {
        when(appointmentRepository.existsById(1)).thenReturn(false);

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, new Invoice());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Appointment not found for appointmentId: 1", response.getBody());
        verify(invoiceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createInvoiceShouldReturnConflictWhenInvoiceAlreadyExistsForAppointment() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of(new Invoice()));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, new Invoice());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("An invoice already exists for appointmentId: 1", response.getBody());
        verify(invoiceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createInvoiceShouldValidatePayloadAndReturnBadRequest() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        invoice.setItems(List.of());

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invoice must include at least one detail item", response.getBody());
    }

    @Test
    void createInvoiceShouldReturnCreatedAndComputeTotalsWithDefaults() {
        when(appointmentRepository.existsById(10)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(10)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem(InvoiceLineItemType.SERVICE, "Consultation", 2, new BigDecimal("100"));
        invoice.setItems(List.of(item));

        when(invoiceRepository.save(invoice)).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(10, invoice);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(invoice, response.getBody());

        assertEquals(10, invoice.getAppointmentId());
        assertEquals(LocalDate.now(), invoice.getIssueDate());
        assertNotNull(invoice.getVatRate());
        assertEquals(0, invoice.getVatRate().compareTo(new BigDecimal("0.19")));

        assertEquals(invoice, item.getInvoice());

        assertEquals(0, invoice.getSubtotal().compareTo(new BigDecimal("200")));
        assertEquals(0, invoice.getVatAmount().compareTo(new BigDecimal("38.00")));
        assertEquals(0, invoice.getTotal().compareTo(new BigDecimal("238.00")));

        verify(invoiceRepository).save(invoice);
    }

    @Test
    void createInvoiceShouldReturnBadRequestWithErrorMapWhenExceptionOccurs() {
        when(appointmentRepository.existsById(10)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(10)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem(InvoiceLineItemType.SERVICE, "Consultation", 1, BigDecimal.TEN);
        invoice.setItems(List.of(item));

        doThrow(new RuntimeException("DB down")).when(invoiceRepository).save(org.mockito.ArgumentMatchers.any());

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(10, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertTrue(error.get("message").startsWith("Error al generar factura: "));
    }

    @Test
    void getInvoiceByAppointmentShouldReturnNotFoundWhenAppointmentMissing() {
        when(appointmentRepository.existsById(5)).thenReturn(false);

        ResponseEntity<?> response = invoiceController.getInvoiceByAppointment(5);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getInvoiceByAppointmentShouldReturnNotFoundWhenNoInvoiceFound() {
        when(appointmentRepository.existsById(5)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(5)).thenReturn(List.of());

        ResponseEntity<?> response = invoiceController.getInvoiceByAppointment(5);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
