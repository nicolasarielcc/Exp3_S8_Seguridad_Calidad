package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceModelTest {

    @Test
    void defaultsShouldBeInitialized() {
        Invoice invoice = new Invoice();

        assertNull(invoice.getId());
        assertNull(invoice.getAppointmentId());
        assertNull(invoice.getIssueDate());
        assertNull(invoice.getVatRate());
        assertNull(invoice.getSubtotal());
        assertNull(invoice.getVatAmount());
        assertNull(invoice.getTotal());
        assertNull(invoice.getNotes());
        assertNotNull(invoice.getItems());
        assertEquals(0, invoice.getItems().size());
    }

    @Test
    void gettersAndSettersShouldWork() {
        Invoice invoice = new Invoice();

        invoice.setId(1L);
        invoice.setAppointmentId(10L);
        invoice.setIssueDate(LocalDate.of(2026, 5, 2));
        invoice.setVatRate(new BigDecimal("0.19"));
        invoice.setSubtotal(new BigDecimal("100"));
        invoice.setVatAmount(new BigDecimal("19"));
        invoice.setTotal(new BigDecimal("119"));
        invoice.setNotes("notes");

        InvoiceLineItem item = new InvoiceLineItem();
        invoice.setItems(List.of(item));

        assertEquals(1L, invoice.getId());
        assertEquals(10L, invoice.getAppointmentId());
        assertEquals(LocalDate.of(2026, 5, 2), invoice.getIssueDate());
        assertEquals(0, invoice.getVatRate().compareTo(new BigDecimal("0.19")));
        assertEquals(0, invoice.getSubtotal().compareTo(new BigDecimal("100")));
        assertEquals(0, invoice.getVatAmount().compareTo(new BigDecimal("19")));
        assertEquals(0, invoice.getTotal().compareTo(new BigDecimal("119")));
        assertEquals("notes", invoice.getNotes());
        assertEquals(1, invoice.getItems().size());
        assertEquals(item, invoice.getItems().get(0));
    }

    @Test
    void allArgsConstructorShouldWork() {
        InvoiceLineItem item = new InvoiceLineItem();
        Invoice invoice = new Invoice(
                1L,
                10L,
                LocalDate.of(2026, 5, 2),
                new BigDecimal("0.19"),
                new BigDecimal("100"),
                new BigDecimal("19"),
                new BigDecimal("119"),
                "notes",
                List.of(item)
        );

        assertEquals(1L, invoice.getId());
        assertEquals(10L, invoice.getAppointmentId());
        assertEquals(LocalDate.of(2026, 5, 2), invoice.getIssueDate());
        assertEquals(0, invoice.getVatRate().compareTo(new BigDecimal("0.19")));
        assertEquals(0, invoice.getSubtotal().compareTo(new BigDecimal("100")));
        assertEquals(0, invoice.getVatAmount().compareTo(new BigDecimal("19")));
        assertEquals(0, invoice.getTotal().compareTo(new BigDecimal("119")));
        assertEquals("notes", invoice.getNotes());
        assertEquals(1, invoice.getItems().size());
        assertEquals(item, invoice.getItems().get(0));
    }
}
