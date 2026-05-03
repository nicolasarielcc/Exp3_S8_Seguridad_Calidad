package com.duoc.backend;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppointmentTest {

    @Test
    void constructorShouldSetFields() {
        LocalDate date = LocalDate.of(2026, 5, 2);
        LocalTime time = LocalTime.of(10, 30);
        Appointment appointment = new Appointment(10, date, time, "Checkup", "Dr. Who");

        assertEquals(10, appointment.getPatientId());
        assertEquals(date, appointment.getDate());
        assertEquals(time, appointment.getTime());
        assertEquals("Checkup", appointment.getReason());
        assertEquals("Dr. Who", appointment.getVeterinarian());
    }

    @Test
    void gettersAndSettersShouldWork() {
        Appointment appointment = new Appointment();
        appointment.setId(1);
        appointment.setPatientId(2);
        appointment.setReason("X");

        assertEquals(1, appointment.getId());
        assertEquals(2, appointment.getPatientId());
        assertEquals("X", appointment.getReason());
    }
}
