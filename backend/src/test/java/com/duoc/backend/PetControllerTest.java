package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetController petController;

    @Test
    void createPetShouldReturnCreatedWhenSaveSucceeds() {
        Pet pet = new Pet();
        pet.setName("Luna");

        ResponseEntity<?> response = petController.createPet(pet);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pet, response.getBody());
        verify(petRepository).save(pet);
    }

    @Test
    void createPetShouldReturnBadRequestWhenSaveFails() {
        Pet pet = new Pet();
        pet.setName("Luna");
        doThrow(new RuntimeException("DB down")).when(petRepository).save(pet);

        ResponseEntity<?> response = petController.createPet(pet);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertEquals("Error al registrar mascota: DB down", error.get("message"));
    }

    @Test
    void getAllPetsShouldReturnOkWhenRepositorySucceeds() {
        List<Pet> pets = List.of(new Pet(), new Pet());
        when(petRepository.findAll()).thenReturn(pets);

        ResponseEntity<Iterable<Pet>> response = petController.getAllPets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pets, response.getBody());
    }

    @Test
    void getAllPetsShouldReturnInternalServerErrorWhenRepositoryFails() {
        when(petRepository.findAll()).thenThrow(new RuntimeException("DB down"));

        ResponseEntity<Iterable<Pet>> response = petController.getAllPets();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAvailablePetsShouldReturnOkWhenRepositorySucceeds() {
        List<Pet> available = List.of(new Pet());
        when(petRepository.findByStatus("available")).thenReturn(available);

        ResponseEntity<List<Pet>> response = petController.getAvailablePets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(available, response.getBody());
    }

    @Test
    void getPetByIdShouldReturnOkWhenFound() {
        Pet pet = new Pet();
        pet.setId(10);
        when(petRepository.findById(10)).thenReturn(Optional.of(pet));

        ResponseEntity<Pet> response = petController.getPetById(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pet, response.getBody());
    }

    @Test
    void getPetByIdShouldReturnNotFoundWhenMissing() {
        when(petRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<Pet> response = petController.getPetById(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updatePetShouldUpdateOnlyNonNullFieldsAndReturnOk() {
        Pet existing = new Pet();
        existing.setId(5);
        existing.setName("Old");
        existing.setSpecies("Dog");

        Pet changes = new Pet();
        changes.setName("New");

        when(petRepository.findById(5)).thenReturn(Optional.of(existing));
        when(petRepository.save(existing)).thenReturn(existing);

        ResponseEntity<?> response = petController.updatePet(5, changes);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existing, response.getBody());
        assertEquals("New", existing.getName());
        assertEquals("Dog", existing.getSpecies());
        verify(petRepository).save(existing);
    }

    @Test
    void updatePetShouldReturnNotFoundWhenMissing() {
        when(petRepository.findById(123)).thenReturn(Optional.empty());

        ResponseEntity<?> response = petController.updatePet(123, new Pet());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(petRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deletePetShouldReturnOkWithMessageWhenFound() {
        when(petRepository.findById(7)).thenReturn(Optional.of(new Pet()));

        ResponseEntity<?> response = petController.deletePet(7);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        verify(petRepository).deleteById(7);
    }

    @Test
    void deletePetShouldReturnNotFoundWhenMissing() {
        when(petRepository.findById(7)).thenReturn(Optional.empty());

        ResponseEntity<?> response = petController.deletePet(7);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(petRepository, never()).deleteById(7);
    }

    @Test
    void searchPetsShouldDelegateToMostSpecificRepositoryMethod() {
        List<Pet> expected = List.of(new Pet());
        when(petRepository.findBySpeciesAndGenderAndLocationAndAgeAndStatus("Dog", "F", "Santiago", 2, "available"))
                .thenReturn(expected);

        ResponseEntity<List<Pet>> response = petController.searchPets("Dog", "F", "Santiago", 2, "available");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(petRepository).findBySpeciesAndGenderAndLocationAndAgeAndStatus("Dog", "F", "Santiago", 2, "available");
    }
}
