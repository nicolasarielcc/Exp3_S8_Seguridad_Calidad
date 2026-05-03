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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    void createInvoiceShouldReturnBadRequestWhenInvoicePayloadIsNull() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invoice payload is required", response.getBody());
        verify(invoiceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

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
    void createInvoiceShouldReturnBadRequestWhenVatRateIsNegative() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        invoice.setVatRate(new BigDecimal("-0.01"));
        invoice.setItems(List.of(new InvoiceLineItem(InvoiceLineItemType.SERVICE, "X", 1, BigDecimal.ONE)));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("vatRate cannot be negative", response.getBody());
    }

    @Test
    void createInvoiceShouldReturnBadRequestWhenItemTypeMissing() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem();
        item.setType(null);
        item.setDescription("Desc");
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.ONE);
        invoice.setItems(List.of(item));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Item 1 must define type (SERVICE, MEDICATION, ADDITIONAL_CHARGE)", response.getBody());
    }

    @Test
    void createInvoiceShouldReturnBadRequestWhenItemDescriptionNull() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem();
        item.setType(InvoiceLineItemType.SERVICE);
        item.setDescription(null);
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.ONE);
        invoice.setItems(List.of(item));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Item 1 must include a description", response.getBody());
    }

    @Test
    void createInvoiceShouldReturnBadRequestWhenItemDescriptionBlank() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem();
        item.setType(InvoiceLineItemType.SERVICE);
        item.setDescription("   ");
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.ONE);
        invoice.setItems(List.of(item));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Item 1 must include a description", response.getBody());
    }

    @Test
    void createInvoiceShouldReturnBadRequestWhenItemQuantityNull() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem();
        item.setType(InvoiceLineItemType.SERVICE);
        item.setDescription("Desc");
        item.setQuantity(null);
        item.setUnitPrice(BigDecimal.ONE);
        invoice.setItems(List.of(item));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Item 1 must include a quantity greater than zero", response.getBody());
    }

    @Test
    void createInvoiceShouldReturnBadRequestWhenItemQuantityZero() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem();
        item.setType(InvoiceLineItemType.SERVICE);
        item.setDescription("Desc");
        item.setQuantity(0);
        item.setUnitPrice(BigDecimal.ONE);
        invoice.setItems(List.of(item));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Item 1 must include a quantity greater than zero", response.getBody());
    }

    @Test
    void createInvoiceShouldReturnBadRequestWhenItemUnitPriceNull() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem();
        item.setType(InvoiceLineItemType.SERVICE);
        item.setDescription("Desc");
        item.setQuantity(1);
        item.setUnitPrice(null);
        invoice.setItems(List.of(item));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Item 1 must include a unitPrice greater or equal to zero", response.getBody());
    }

    @Test
    void createInvoiceShouldReturnBadRequestWhenItemUnitPriceNegative() {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(1)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        InvoiceLineItem item = new InvoiceLineItem();
        item.setType(InvoiceLineItemType.SERVICE);
        item.setDescription("Desc");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("-1"));
        invoice.setItems(List.of(item));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(1, invoice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Item 1 must include a unitPrice greater or equal to zero", response.getBody());
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
    void createInvoiceShouldRespectProvidedIssueDateAndVatRate() {
        when(appointmentRepository.existsById(10)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(10)).thenReturn(List.of());

        Invoice invoice = new Invoice();
        invoice.setIssueDate(LocalDate.of(2024, 1, 20));
        invoice.setVatRate(new BigDecimal("0.20"));

        InvoiceLineItem item = new InvoiceLineItem(InvoiceLineItemType.SERVICE, "Consultation", 1, new BigDecimal("50"));
        invoice.setItems(List.of(item));

        when(invoiceRepository.save(invoice)).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = invoiceController.createInvoiceForAppointment(10, invoice);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(invoice, response.getBody());

        assertEquals(10, invoice.getAppointmentId());
        assertEquals(LocalDate.of(2024, 1, 20), invoice.getIssueDate());
        assertEquals(0, invoice.getVatRate().compareTo(new BigDecimal("0.20")));
        assertEquals(invoice, item.getInvoice());
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

    @Test
    void getInvoiceByAppointmentShouldReturnOkWithFirstInvoiceWhenExists() {
        when(appointmentRepository.existsById(5)).thenReturn(true);
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();
        when(invoiceRepository.findByAppointmentId(5)).thenReturn(List.of(invoice1, invoice2));

        ResponseEntity<?> response = invoiceController.getInvoiceByAppointment(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(invoice1, response.getBody());
    }

    @Test
    void getInvoiceByAppointmentShouldReturnInternalServerErrorWhenRepositoryThrows() {
        when(appointmentRepository.existsById(5)).thenReturn(true);
        when(invoiceRepository.findByAppointmentId(5)).thenThrow(new RuntimeException("DB down"));

        ResponseEntity<?> response = invoiceController.getInvoiceByAppointment(5);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllInvoicesShouldReturnOkWhenRepositorySucceeds() {
        List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice());
        when(invoiceRepository.findAll()).thenReturn(invoices);

        ResponseEntity<Iterable<Invoice>> response = invoiceController.getAllInvoices();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(invoices, response.getBody());
    }

    @Test
    void getAllInvoicesShouldReturnInternalServerErrorWhenRepositoryThrows() {
        when(invoiceRepository.findAll()).thenThrow(new RuntimeException("DB down"));

        ResponseEntity<Iterable<Invoice>> response = invoiceController.getAllInvoices();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getInvoiceByIdShouldReturnOkWhenFound() {
        Invoice invoice = new Invoice();
        when(invoiceRepository.findById(7)).thenReturn(Optional.of(invoice));

        ResponseEntity<Invoice> response = invoiceController.getInvoiceById(7);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(invoice, response.getBody());
    }

    @Test
    void getInvoiceByIdShouldReturnNotFoundWhenMissing() {
        when(invoiceRepository.findById(7)).thenReturn(Optional.empty());

        ResponseEntity<Invoice> response = invoiceController.getInvoiceById(7);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getInvoiceByIdShouldReturnInternalServerErrorWhenRepositoryThrows() {
        when(invoiceRepository.findById(7)).thenThrow(new RuntimeException("DB down"));

        ResponseEntity<Invoice> response = invoiceController.getInvoiceById(7);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
