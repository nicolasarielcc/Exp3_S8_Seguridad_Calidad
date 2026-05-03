package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvoicePageControllerTests {

    private final InvoicePageController controller = new InvoicePageController();

    @Test
    void listInvoicesShouldReturnInvoicesView() {
        assertEquals("invoices", controller.listInvoices());
    }

    @Test
    void showCreateFormShouldReturnNewInvoiceView() {
        assertEquals("new_invoice", controller.showCreateForm());
    }

    @Test
    void showInvoiceDetailShouldReturnInvoiceDetailView() {
        assertEquals("invoice_detail", controller.showInvoiceDetail(1L));
    }
}
