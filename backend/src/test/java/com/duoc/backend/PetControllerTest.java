package com.duoc.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

    private Pet existingPet;

    @BeforeEach
    void setUp() {
        existingPet = new Pet();
        existingPet.setId(5);
        existingPet.setName("Old");
        existingPet.setSpecies("Dog");
        existingPet.setBreed("OldBreed");
        existingPet.setAge(1);
        existingPet.setGender("M");
        existingPet.setLocation("OldLoc");
        existingPet.setPhotos(List.of("old"));
        existingPet.setStatus("available");
    }

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
        Pet changes = new Pet();
        changes.setName("New");

        when(petRepository.findById(5)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(existingPet)).thenReturn(existingPet);

        ResponseEntity<?> response = petController.updatePet(5, changes);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingPet, response.getBody());
        assertEquals("New", existingPet.getName());
        assertEquals("Dog", existingPet.getSpecies());
        verify(petRepository).save(existingPet);
    }

    @Test
    void updatePetShouldUpdateAllFieldsWhenProvided() {
        Pet changes = new Pet();
        changes.setName("NewName");
        changes.setSpecies("Cat");
        changes.setBreed("Siamese");
        changes.setAge(3);
        changes.setGender("F");
        changes.setLocation("Santiago");
        changes.setPhotos(List.of("p1", "p2"));
        changes.setStatus("adopted");

        when(petRepository.findById(5)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(existingPet)).thenReturn(existingPet);

        ResponseEntity<?> response = petController.updatePet(5, changes);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingPet, response.getBody());

        assertEquals("NewName", existingPet.getName());
        assertEquals("Cat", existingPet.getSpecies());
        assertEquals("Siamese", existingPet.getBreed());
        assertEquals(3, existingPet.getAge());
        assertEquals("F", existingPet.getGender());
        assertEquals("Santiago", existingPet.getLocation());
        assertEquals(List.of("p1", "p2"), existingPet.getPhotos());
        assertEquals("adopted", existingPet.getStatus());
    }

    @Test
    void updatePetShouldReturnBadRequestWithMessageWhenFindThrows() {
        when(petRepository.findById(5)).thenThrow(new RuntimeException("DB down"));

        ResponseEntity<?> response = petController.updatePet(5, new Pet());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertEquals("Error al actualizar mascota: DB down", error.get("message"));
    }

    @Test
    void updatePetShouldReturnBadRequestWithMessageWhenSaveThrows() {
        when(petRepository.findById(5)).thenReturn(Optional.of(existingPet));
        doThrow(new RuntimeException("DB down")).when(petRepository).save(existingPet);

        ResponseEntity<?> response = petController.updatePet(5, new Pet());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertEquals("Error al actualizar mascota: DB down", error.get("message"));
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


        private enum SearchCase {
        ALL,
        SPECIES_GENDER_LOCATION,
        SPECIES_GENDER_AGE,
        SPECIES_LOCATION_AGE,
        GENDER_LOCATION_AGE,
        SPECIES_GENDER,
        SPECIES_LOCATION,
        SPECIES_AGE,
        GENDER_LOCATION,
        GENDER_AGE,
        LOCATION_AGE,
        SPECIES_ONLY,
        GENDER_ONLY,
        LOCATION_ONLY,
        AGE_ONLY,
        STATUS_ONLY
        }

        static Stream<Arguments> searchCases() {
        return Stream.of(
            Arguments.of(SearchCase.ALL, "Dog", "F", "Santiago", 2, "available"),
            Arguments.of(SearchCase.SPECIES_GENDER_LOCATION, "Dog", "F", "Santiago", null, "available"),
            Arguments.of(SearchCase.SPECIES_GENDER_AGE, "Dog", "F", null, 2, "available"),
            Arguments.of(SearchCase.SPECIES_LOCATION_AGE, "Dog", null, "Santiago", 2, "available"),
            Arguments.of(SearchCase.GENDER_LOCATION_AGE, null, "F", "Santiago", 2, "available"),
            Arguments.of(SearchCase.SPECIES_GENDER, "Dog", "F", null, null, "available"),
            Arguments.of(SearchCase.SPECIES_LOCATION, "Dog", null, "Santiago", null, "available"),
            Arguments.of(SearchCase.SPECIES_AGE, "Dog", null, null, 2, "available"),
            Arguments.of(SearchCase.GENDER_LOCATION, null, "F", "Santiago", null, "available"),
            Arguments.of(SearchCase.GENDER_AGE, null, "F", null, 2, "available"),
            Arguments.of(SearchCase.LOCATION_AGE, null, null, "Santiago", 2, "available"),
            Arguments.of(SearchCase.SPECIES_ONLY, "Dog", null, null, null, "available"),
            Arguments.of(SearchCase.GENDER_ONLY, null, "F", null, null, "available"),
            Arguments.of(SearchCase.LOCATION_ONLY, null, null, "Santiago", null, "available"),
            Arguments.of(SearchCase.AGE_ONLY, null, null, null, 2, "available"),
            Arguments.of(SearchCase.STATUS_ONLY, null, null, null, null, "available")
        );
        }

        @ParameterizedTest
        @MethodSource("searchCases")
        void searchPetsShouldRouteToCorrectRepositoryMethod(SearchCase searchCase,
                                   String species,
                                   String gender,
                                   String location,
                                   Integer age,
                                   String status) {
        List<Pet> expected = List.of(new Pet());

        switch (searchCase) {
            case ALL -> when(petRepository.findBySpeciesAndGenderAndLocationAndAgeAndStatus(species, gender, location, age, status))
                .thenReturn(expected);
            case SPECIES_GENDER_LOCATION -> when(petRepository.findBySpeciesAndGenderAndLocationAndStatus(species, gender, location, status))
                .thenReturn(expected);
            case SPECIES_GENDER_AGE -> when(petRepository.findBySpeciesAndGenderAndAgeAndStatus(species, gender, age, status))
                .thenReturn(expected);
            case SPECIES_LOCATION_AGE -> when(petRepository.findBySpeciesAndLocationAndAgeAndStatus(species, location, age, status))
                .thenReturn(expected);
            case GENDER_LOCATION_AGE -> when(petRepository.findByGenderAndLocationAndAgeAndStatus(gender, location, age, status))
                .thenReturn(expected);
            case SPECIES_GENDER -> when(petRepository.findBySpeciesAndGenderAndStatus(species, gender, status))
                .thenReturn(expected);
            case SPECIES_LOCATION -> when(petRepository.findBySpeciesAndLocationAndStatus(species, location, status))
                .thenReturn(expected);
            case SPECIES_AGE -> when(petRepository.findBySpeciesAndAgeAndStatus(species, age, status))
                .thenReturn(expected);
            case GENDER_LOCATION -> when(petRepository.findByGenderAndLocationAndStatus(gender, location, status))
                .thenReturn(expected);
            case GENDER_AGE -> when(petRepository.findByGenderAndAgeAndStatus(gender, age, status))
                .thenReturn(expected);
            case LOCATION_AGE -> when(petRepository.findByLocationAndAgeAndStatus(location, age, status))
                .thenReturn(expected);
            case SPECIES_ONLY -> when(petRepository.findBySpeciesAndStatus(species, status))
                .thenReturn(expected);
            case GENDER_ONLY -> when(petRepository.findByGenderAndStatus(gender, status))
                .thenReturn(expected);
            case LOCATION_ONLY -> when(petRepository.findByLocationAndStatus(location, status))
                .thenReturn(expected);
            case AGE_ONLY -> when(petRepository.findByAgeAndStatus(age, status))
                .thenReturn(expected);
            case STATUS_ONLY -> when(petRepository.findByStatus(status))
                .thenReturn(expected);
        }

        ResponseEntity<List<Pet>> response = petController.searchPets(species, gender, location, age, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        switch (searchCase) {
            case ALL -> verify(petRepository).findBySpeciesAndGenderAndLocationAndAgeAndStatus(species, gender, location, age, status);
            case SPECIES_GENDER_LOCATION -> verify(petRepository).findBySpeciesAndGenderAndLocationAndStatus(species, gender, location, status);
            case SPECIES_GENDER_AGE -> verify(petRepository).findBySpeciesAndGenderAndAgeAndStatus(species, gender, age, status);
            case SPECIES_LOCATION_AGE -> verify(petRepository).findBySpeciesAndLocationAndAgeAndStatus(species, location, age, status);
            case GENDER_LOCATION_AGE -> verify(petRepository).findByGenderAndLocationAndAgeAndStatus(gender, location, age, status);
            case SPECIES_GENDER -> verify(petRepository).findBySpeciesAndGenderAndStatus(species, gender, status);
            case SPECIES_LOCATION -> verify(petRepository).findBySpeciesAndLocationAndStatus(species, location, status);
            case SPECIES_AGE -> verify(petRepository).findBySpeciesAndAgeAndStatus(species, age, status);
            case GENDER_LOCATION -> verify(petRepository).findByGenderAndLocationAndStatus(gender, location, status);
            case GENDER_AGE -> verify(petRepository).findByGenderAndAgeAndStatus(gender, age, status);
            case LOCATION_AGE -> verify(petRepository).findByLocationAndAgeAndStatus(location, age, status);
            case SPECIES_ONLY -> verify(petRepository).findBySpeciesAndStatus(species, status);
            case GENDER_ONLY -> verify(petRepository).findByGenderAndStatus(gender, status);
            case LOCATION_ONLY -> verify(petRepository).findByLocationAndStatus(location, status);
            case AGE_ONLY -> verify(petRepository).findByAgeAndStatus(age, status);
            case STATUS_ONLY -> verify(petRepository).findByStatus(status);
        }
        }

        @Test
        void searchPetsShouldReturnInternalServerErrorWhenRepositoryThrows() {
        when(petRepository.findByStatus("available")).thenThrow(new RuntimeException("DB down"));

        ResponseEntity<List<Pet>> response = petController.searchPets(null, null, null, null, "available");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        }
}
