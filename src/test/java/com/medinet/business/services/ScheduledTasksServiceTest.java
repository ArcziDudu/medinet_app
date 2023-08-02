package com.medinet.business.services;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    @DisplayName("Should throw an exception when the appointment with the given id is not found")
    void WhenAppointmentWithGivenIdIsNotFoundThenThrowException() {
        AppointmentDto upcomingAppointment = new AppointmentDto();
        upcomingAppointment.setAppointmentId(1);
        upcomingAppointment.setDateOfAppointment(LocalDate.now());
        upcomingAppointment.setTimeOfVisit(LocalTime.now());

        when(appointmentService.findAllAppointmentsByStatus("upcoming")).thenReturn(List.of(upcomingAppointment));


        assertThrows(RuntimeException.class, () -> scheduledTasksService.myMethod());
    }

    @Test
    public void testMyMethod() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime timeOfVisit = now.minusMinutes(30);

        AppointmentDto upcomingAppointmentDto = new AppointmentDto();
        upcomingAppointmentDto.setAppointmentId(1);
        upcomingAppointmentDto.setDateOfAppointment(today);
        upcomingAppointmentDto.setTimeOfVisit(timeOfVisit);
        upcomingAppointmentDto.setStatus("upcoming");

        AppointmentEntity upcomingAppointmentEntity = new AppointmentEntity();
        upcomingAppointmentEntity.setAppointmentId(1);
        upcomingAppointmentEntity.setDateOfAppointment(today);
        upcomingAppointmentEntity.setTimeOfVisit(timeOfVisit);
        upcomingAppointmentEntity.setStatus("upcoming");

        when(appointmentService.findAllAppointmentsByStatus("upcoming")).thenReturn(List.of(upcomingAppointmentDto));
        when(appointmentMapper.mapFromDto(upcomingAppointmentDto)).thenReturn(upcomingAppointmentEntity);
        when(appointmentService.findById(1)).thenReturn(upcomingAppointmentEntity);

        scheduledTasksService.myMethod();
        verify(appointmentService, times(1)).findById(1);
        verify(appointmentService, times(1)).save(upcomingAppointmentEntity);

        assertEquals("pending", upcomingAppointmentEntity.getStatus());
    }

    @Test
    @DisplayName("Should not change the status of the appointment if the appointment time is equal to the current time")
    void testMyMethod_NoChangeInStatusIfAppointmentTimeIsEqualToCurrentTime() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        AppointmentDto upcomingAppointmentDto = new AppointmentDto();
        upcomingAppointmentDto.setAppointmentId(1);
        upcomingAppointmentDto.setDateOfAppointment(today);
        upcomingAppointmentDto.setTimeOfVisit(now);
        upcomingAppointmentDto.setStatus("upcoming");

        AppointmentEntity upcomingAppointmentEntity = new AppointmentEntity();
        upcomingAppointmentEntity.setAppointmentId(1);
        upcomingAppointmentEntity.setDateOfAppointment(today);
        upcomingAppointmentEntity.setTimeOfVisit(now);
        upcomingAppointmentEntity.setStatus("upcoming");

        scheduledTasksService.myMethod();


        verify(appointmentService, never()).findById(1);
        verify(appointmentService, never()).save(any(AppointmentEntity.class));

        assertEquals("upcoming", upcomingAppointmentEntity.getStatus());
    }


}