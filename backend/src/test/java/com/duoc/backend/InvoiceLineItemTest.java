package com.duoc.backend;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvoiceLineItemTest {

    @Test
    void recalculateLineTotalShouldHandleNulls() {
        InvoiceLineItem item = new InvoiceLineItem();
        item.setQuantity(null);
        item.setUnitPrice(null);

        item.recalculateLineTotal();

        assertEquals(0, item.getLineTotal().compareTo(BigDecimal.ZERO));
    }

    @Test
    void recalculateLineTotalShouldMultiplyQuantityByUnitPrice() {
        InvoiceLineItem item = new InvoiceLineItem();
        item.setQuantity(3);
        item.setUnitPrice(new BigDecimal("12.50"));

        item.recalculateLineTotal();

        assertEquals(0, item.getLineTotal().compareTo(new BigDecimal("37.50")));
    }
}
