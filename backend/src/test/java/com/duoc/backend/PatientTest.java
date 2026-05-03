package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PatientTest {

    @Test
    void defaultConstructorAndSettersShouldPopulateFields() {
        Patient patient = new Patient();

        assertNull(patient.getId());
        assertNull(patient.getName());
        assertNull(patient.getSpecies());
        assertNull(patient.getBreed());
        assertNull(patient.getAge());
        assertNull(patient.getOwner());

        patient.setId(123);
        patient.setName("Luna");
        patient.setSpecies("Dog");
        patient.setBreed("Labrador");
        patient.setAge(4);
        patient.setOwner("Nico");

        assertEquals(123, patient.getId());
        assertEquals("Luna", patient.getName());
        assertEquals("Dog", patient.getSpecies());
        assertEquals("Labrador", patient.getBreed());
        assertEquals(4, patient.getAge());
        assertEquals("Nico", patient.getOwner());
    }

    @Test
    void allArgsConstructorShouldPopulateFields() {
        Patient patient = new Patient("Milo", "Cat", "Siamese", 2, "Ana");

        assertNull(patient.getId());
        assertEquals("Milo", patient.getName());
        assertEquals("Cat", patient.getSpecies());
        assertEquals("Siamese", patient.getBreed());
        assertEquals(2, patient.getAge());
        assertEquals("Ana", patient.getOwner());
    }
}
