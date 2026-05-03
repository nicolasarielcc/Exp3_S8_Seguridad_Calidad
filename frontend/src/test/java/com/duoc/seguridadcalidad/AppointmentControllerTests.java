package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppointmentControllerTests {

    private final AppointmentController controller = new AppointmentController();

    @Test
    void listAppointmentsShouldReturnAppointmentsView() {
        assertEquals("appointments", controller.listAppointments());
    }

    @Test
    void showCreateFormShouldReturnNewAppointmentView() {
        assertEquals("new_appointment", controller.showCreateForm());
    }

    @Test
    void saveAppointmentShouldRedirectToAppointments() {
        assertEquals("redirect:/appointments", controller.saveAppointment());
    }
}
