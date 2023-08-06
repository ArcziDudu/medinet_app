package com.medinet.business.services;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduledTasksService should")
class ScheduledTasksServiceTest {
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private AppointmentMapper appointmentMapper;
    @InjectMocks
    private ScheduledTasksService scheduledTasksService;

    @Test
    @DisplayName("Should not change the status of the appointment" +
            "if appointment has not taken place")
    void shouldNotChangeTheStatus() {
        List<AppointmentDto> upcomingAppointments = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        AppointmentDto upcomingAppointment = new AppointmentDto();
        upcomingAppointment.setAppointmentId(2);
        upcomingAppointment.setDateOfAppointment(today);
        upcomingAppointment.setTimeOfVisit(now.plusMinutes(5));
        upcomingAppointment.setStatus("upcoming");
        upcomingAppointments.add(upcomingAppointment);

        when(appointmentService.findAllAppointmentsByStatus("upcoming")).thenReturn(upcomingAppointments);

        // When
        scheduledTasksService.statusConversionIfTheVisitTookPlace();

        // Then
        verify(appointmentService).findAllAppointmentsByStatus("upcoming");

        assertEquals("upcoming", upcomingAppointment.getStatus());

        verify(appointmentService, never()).save(appointmentMapper.mapFromDto(upcomingAppointment));
    }
    @Test
    @DisplayName("Should change the status of the appointment" +
            " took place")
    public void shouldChangeTheStatus() {
        // Given
        List<AppointmentDto> upcomingAppointments = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        AppointmentDto upcomingAppointment = new AppointmentDto();
        upcomingAppointment.setAppointmentId(1);
        upcomingAppointment.setDateOfAppointment(today);
        upcomingAppointment.setTimeOfVisit(now.minusMinutes(5));
        upcomingAppointment.setStatus("upcoming");
        upcomingAppointments.add(upcomingAppointment);

        when(appointmentService.findAllAppointmentsByStatus("upcoming")).thenReturn(upcomingAppointments);
        when(appointmentService.findById(1)).thenReturn(upcomingAppointment);

        // When
        scheduledTasksService.statusConversionIfTheVisitTookPlace();

        // Then
        verify(appointmentService).findAllAppointmentsByStatus("upcoming");
        verify(appointmentService).findById(1);

        assertEquals("pending", upcomingAppointment.getStatus());

        verify(appointmentService).save(appointmentMapper.mapFromDto(upcomingAppointment));
    }

}