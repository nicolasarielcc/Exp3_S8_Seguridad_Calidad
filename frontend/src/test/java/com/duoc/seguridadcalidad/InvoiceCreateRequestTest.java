package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceCreateRequestTest {

    @Test
    void defaultsShouldBeInitialized() {
        InvoiceCreateRequest req = new InvoiceCreateRequest();

        assertNull(req.getIssueDate());
        assertNull(req.getVatRate());
        assertNull(req.getNotes());
        assertNotNull(req.getItems());
        assertEquals(0, req.getItems().size());
    }

    @Test
    void gettersAndSettersShouldWork() {
        InvoiceCreateRequest req = new InvoiceCreateRequest();
        LocalDate date = LocalDate.of(2026, 5, 2);

        req.setIssueDate(date);
        req.setVatRate(new BigDecimal("0.19"));
        req.setNotes("note");

        InvoiceLineItem item = new InvoiceLineItem();
        req.setItems(List.of(item));

        assertEquals(date, req.getIssueDate());
        assertEquals(0, req.getVatRate().compareTo(new BigDecimal("0.19")));
        assertEquals("note", req.getNotes());
        assertEquals(1, req.getItems().size());
        assertEquals(item, req.getItems().get(0));
    }
}
