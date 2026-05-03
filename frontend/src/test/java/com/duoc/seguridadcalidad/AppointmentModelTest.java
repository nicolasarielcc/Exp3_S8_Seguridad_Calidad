package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AppointmentModelTest {

    @Test
    void defaultConstructorAndSettersShouldWork() {
        Appointment appt = new Appointment();

        assertNull(appt.getId());
        assertNull(appt.getPatientId());
        assertNull(appt.getDate());
        assertNull(appt.getTime());
        assertNull(appt.getReason());
        assertNull(appt.getVeterinarian());

        appt.setId(1L);
        appt.setPatientId(2L);
        appt.setDate(LocalDate.of(2026, 5, 2));
        appt.setTime(LocalTime.of(10, 30));
        appt.setReason("Checkup");
        appt.setVeterinarian("Dr. Smith");

        assertEquals(1L, appt.getId());
        assertEquals(2L, appt.getPatientId());
        assertEquals(LocalDate.of(2026, 5, 2), appt.getDate());
        assertEquals(LocalTime.of(10, 30), appt.getTime());
        assertEquals("Checkup", appt.getReason());
        assertEquals("Dr. Smith", appt.getVeterinarian());
    }

    @Test
    void allArgsConstructorShouldWork() {
        Appointment appt = new Appointment(
                1L,
                2L,
                LocalDate.of(2026, 5, 2),
                LocalTime.of(10, 30),
                "Checkup",
                "Dr. Smith"
        );

        assertEquals(1L, appt.getId());
        assertEquals(2L, appt.getPatientId());
        assertEquals(LocalDate.of(2026, 5, 2), appt.getDate());
        assertEquals(LocalTime.of(10, 30), appt.getTime());
        assertEquals("Checkup", appt.getReason());
        assertEquals("Dr. Smith", appt.getVeterinarian());
    }
}
