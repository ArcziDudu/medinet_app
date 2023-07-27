package com.medinet.business.services;

import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.util.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {
    @Mock
    private CalendarService calendarService;

    @Test
    void findByDoctorIdAndDateOfAppointmentDoctorAndDateExistsReturnsCalendarEntity() {
        // Given
        DoctorEntity doctor = EntityFixtures.someDoctor1();
        LocalDate dateOfAppointment = LocalDate.of(2023, 7, 31);

        CalendarEntity calendarEntity = new CalendarEntity();


        when(calendarService.findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment))
                .thenReturn(calendarEntity);

        // When
        CalendarEntity result = calendarService.findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);

        // Then
        assertNotNull(result);
        assertEquals(calendarEntity, result);

        verify(calendarService, times(1)).findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);
    }

    @Test
    void findByDoctorIdAndDateOfAppointmentDoctorAndDateNotExistsReturnsNull() {
        // Given
        DoctorEntity doctor = new DoctorEntity();
        LocalDate dateOfAppointment = LocalDate.of(2023, 7, 31);

        when(calendarService.findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment))
                .thenReturn(null);

        // When
        CalendarEntity result = calendarService.findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);

        // Then
        assertNull(result);
        verify(calendarService, times(1)).findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);
    }

    @Test
    void findByIdCalendarExistsReturnsOptionalWithCalendarEntity() {
        // Given
        int calendarId = 1;
        CalendarEntity calendarEntity = new CalendarEntity();
        when(calendarService.findById(calendarId))
                .thenReturn(Optional.of(calendarEntity));

        // When
        Optional<CalendarEntity> result = calendarService.findById(calendarId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(calendarEntity, result.get());
        verify(calendarService, times(1)).findById(calendarId);
    }

    @Test
    void findByIdCalendarNotExistsReturnsEmptyOptional() {
        // Given
        int calendarId = 1;
        when(calendarService.findById(calendarId))
                .thenReturn(Optional.empty());

        // When
        Optional<CalendarEntity> result = calendarService.findById(calendarId);

        // Then
        assertFalse(result.isPresent());
        verify(calendarService, times(1)).findById(calendarId);
    }
}