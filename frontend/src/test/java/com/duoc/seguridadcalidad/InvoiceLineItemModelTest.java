package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceLineItemModelTest {

    @Test
    void defaultConstructorAndSettersShouldWork() {
        InvoiceLineItem item = new InvoiceLineItem();

        assertNull(item.getId());
        assertNull(item.getType());
        assertNull(item.getDescription());
        assertNull(item.getQuantity());
        assertNull(item.getUnitPrice());
        assertNull(item.getLineTotal());

        item.setId(1L);
        item.setType(InvoiceLineItemType.SERVICE);
        item.setDescription("Consultation");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("100"));
        item.setLineTotal(new BigDecimal("200"));

        assertEquals(1L, item.getId());
        assertEquals(InvoiceLineItemType.SERVICE, item.getType());
        assertEquals("Consultation", item.getDescription());
        assertEquals(2, item.getQuantity());
        assertEquals(0, item.getUnitPrice().compareTo(new BigDecimal("100")));
        assertEquals(0, item.getLineTotal().compareTo(new BigDecimal("200")));
    }

    @Test
    void allArgsConstructorShouldWork() {
        InvoiceLineItem item = new InvoiceLineItem(
                1L,
                InvoiceLineItemType.MEDICATION,
                "Antibiotic",
                3,
                new BigDecimal("10"),
                new BigDecimal("30")
        );

        assertEquals(1L, item.getId());
        assertEquals(InvoiceLineItemType.MEDICATION, item.getType());
        assertEquals("Antibiotic", item.getDescription());
        assertEquals(3, item.getQuantity());
        assertEquals(0, item.getUnitPrice().compareTo(new BigDecimal("10")));
        assertEquals(0, item.getLineTotal().compareTo(new BigDecimal("30")));
    }
}
