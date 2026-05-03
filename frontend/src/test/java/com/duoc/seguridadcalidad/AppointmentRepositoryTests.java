package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentRepositoryTests {

    private AppointmentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AppointmentRepository();
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoAppointmentsSaved() {
        List<Appointment> result = repository.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void saveShouldAssignIdWhenAppointmentHasNoId() {
        Appointment appt = new Appointment(null, 2L, LocalDate.of(2026, 5, 2), LocalTime.of(10, 0), "Checkup", "Dr");

        Appointment saved = repository.save(appt);

        assertNotNull(saved.getId());
        assertEquals(saved, appt);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void saveShouldNotOverwriteExistingId() {
        Appointment appt = new Appointment(99L, 2L, LocalDate.of(2026, 5, 2), LocalTime.of(10, 0), "Checkup", "Dr");

        Appointment saved = repository.save(appt);

        assertEquals(99L, saved.getId());
    }

    @Test
    void saveShouldAssignSequentialIdsToMultipleAppointments() {
        Appointment a1 = repository.save(new Appointment(null, 1L, LocalDate.of(2026, 5, 2), LocalTime.of(10, 0), "A", "Dr"));
        Appointment a2 = repository.save(new Appointment(null, 1L, LocalDate.of(2026, 5, 3), LocalTime.of(11, 0), "B", "Dr"));

        assertEquals(a1.getId() + 1, a2.getId());
    }

    @Test
    void findAllShouldReturnDefensiveCopy() {
        repository.save(new Appointment(null, 1L, LocalDate.of(2026, 5, 2), LocalTime.of(10, 0), "A", "Dr"));

        List<Appointment> first = repository.findAll();
        List<Appointment> second = repository.findAll();

        assertNotSame(first, second);
        assertEquals(first, second);
    }

    @Test
    void modifyingReturnedListShouldNotAffectRepository() {
        repository.save(new Appointment(null, 1L, LocalDate.of(2026, 5, 2), LocalTime.of(10, 0), "A", "Dr"));

        List<Appointment> list = repository.findAll();
        list.clear();

        assertEquals(1, repository.findAll().size());
    }
}
