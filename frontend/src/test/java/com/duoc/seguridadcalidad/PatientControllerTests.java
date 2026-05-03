package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientControllerTests {

    private final PatientController controller = new PatientController();

    @Test
    void listPatientsShouldReturnPatientsView() {
        assertEquals("patients", controller.listPatients());
    }

    @Test
    void showCreateFormShouldReturnNewPatientView() {
        assertEquals("new_patient", controller.showCreateForm());
    }

    @Test
    void savePatientShouldRedirectToPatients() {
        assertEquals("redirect:/patients", controller.savePatient());
    }
}
