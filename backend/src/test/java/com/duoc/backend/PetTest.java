package com.duoc.backend;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetTest {

    @Test
    void constructorShouldDefaultStatusToAvailable() {
        Pet pet = new Pet("Luna", "Dog", "Labrador", 4, "F", "Santiago", List.of("a"));
        assertEquals("available", pet.getStatus());
    }

    @Test
    void gettersAndSettersShouldWork() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Luna");
        pet.setSpecies("Dog");
        pet.setBreed("Labrador");
        pet.setAge(4);
        pet.setGender("F");
        pet.setLocation("Santiago");
        pet.setPhotos(List.of("p1"));
        pet.setStatus("adopted");

        assertEquals(1, pet.getId());
        assertEquals("Luna", pet.getName());
        assertEquals("Dog", pet.getSpecies());
        assertEquals("Labrador", pet.getBreed());
        assertEquals(4, pet.getAge());
        assertEquals("F", pet.getGender());
        assertEquals("Santiago", pet.getLocation());
        assertEquals(List.of("p1"), pet.getPhotos());
        assertEquals("adopted", pet.getStatus());
    }
}
