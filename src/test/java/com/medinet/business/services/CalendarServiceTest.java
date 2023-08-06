package com.medinet.business.services;

import com.medinet.api.dto.CalendarDto;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.util.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Objects;

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

        CalendarDto calendar = new CalendarDto();


        when(calendarService.findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment))
                .thenReturn(calendar);

        // When
        CalendarDto result = calendarService.findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);

        // Then
        assertNotNull(result);
        assertEquals(calendar, result);

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
        CalendarDto result = calendarService.findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);

        // Then
        assertNull(result);
        verify(calendarService, times(1)).findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);
    }

    @Test
    void findByIdCalendarExistsReturnsOptionalWithCalendarEntity() {
        // Given
        int calendarId = 1;
        CalendarDto calendar = new CalendarDto();
        when(calendarService.findById(calendarId))
                .thenReturn(calendar);

        // When
        CalendarDto result = calendarService.findById(calendarId);

        // Then
        assertTrue(Objects.nonNull(result));
        assertEquals(calendar, result);
        verify(calendarService, times(1)).findById(calendarId);
    }

    @Test
    void findByIdCalendarNotExistsReturnsEmptyOptional() {
        // Given
        int calendarId = 1;
        CalendarDto calendar = new CalendarDto();
        when(calendarService.findById(calendarId))
                .thenReturn(calendar);

        // When
        CalendarDto result = calendarService.findById(calendarId);

        // Then
        assertFalse(Objects.isNull(result));
        verify(calendarService, times(1)).findById(calendarId);
    }
}