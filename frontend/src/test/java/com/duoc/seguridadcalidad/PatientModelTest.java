package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PatientModelTest {

    @Test
    void defaultConstructorAndSettersShouldWork() {
        Patient p = new Patient();

        assertNull(p.getId());
        assertNull(p.getName());
        assertNull(p.getSpecies());
        assertNull(p.getBreed());
        assertNull(p.getAge());
        assertNull(p.getOwner());

        p.setId(1L);
        p.setName("Luna");
        p.setSpecies("Dog");
        p.setBreed("Labrador");
        p.setAge(4);
        p.setOwner("Nico");

        assertEquals(1L, p.getId());
        assertEquals("Luna", p.getName());
        assertEquals("Dog", p.getSpecies());
        assertEquals("Labrador", p.getBreed());
        assertEquals(4, p.getAge());
        assertEquals("Nico", p.getOwner());
    }

    @Test
    void allArgsConstructorShouldWork() {
        Patient p = new Patient(7L, "Milo", "Cat", "Siamese", 2, "Ana");

        assertEquals(7L, p.getId());
        assertEquals("Milo", p.getName());
        assertEquals("Cat", p.getSpecies());
        assertEquals("Siamese", p.getBreed());
        assertEquals(2, p.getAge());
        assertEquals("Ana", p.getOwner());
    }
}
