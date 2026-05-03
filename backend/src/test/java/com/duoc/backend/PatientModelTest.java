package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientModelTest {

    @Test
    void constructorAndSettersShouldWork() {
        Patient patient = new Patient("Luna", "Dog", "Labrador", 4, "Ana");
        patient.setId(10);

        assertEquals(10, patient.getId());
        assertEquals("Luna", patient.getName());
        assertEquals("Dog", patient.getSpecies());
        assertEquals("Labrador", patient.getBreed());
        assertEquals(4, patient.getAge());
        assertEquals("Ana", patient.getOwner());
    }
}
