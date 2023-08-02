package com.medinet.business.services;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.AppointmentDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock
    private AppointmentDao appointmentDao;
    @Mock
    private CalendarService calendarService;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @InjectMocks
    private AppointmentService appointmentService;


    @Test
    public void processRemovingAppointment_RemovesAppointmentAndAddsHourToCalendar() {
        //given
        Integer appointmentId = 123;
        LocalTime calendarHour = LocalTime.of(10, 0);
        Integer calendarId = 456;

        CalendarEntity calendarEntity = new CalendarEntity();
        calendarEntity.setCalendarId(calendarId);
        calendarEntity.setHours(new ArrayList<>());

        when(calendarService.findById(calendarId)).thenReturn(Optional.of(calendarEntity));

        //when

        appointmentService.processRemovingAppointment(appointmentId, calendarHour, calendarId);
        //then
        verify(appointmentDao).removeAppointment(appointmentId);

        List<LocalTime> hoursAfterProcess = calendarEntity.getHours();
        assertEquals(calendarHour, hoursAfterProcess.get(0));
    }

    @Test
    void findCompletedAppointmentsReturnsCompletedAppointments() {
        // Given
        PatientDto currentPatient = new PatientDto();
        Set<AppointmentEntity> appointments = new HashSet<>();
        AppointmentEntity appointment1 = new AppointmentEntity();
        appointment1.setStatus("done");
        appointments.add(appointment1);

        AppointmentEntity appointment2 = new AppointmentEntity();
        appointment2.setStatus("upcoming");
        appointments.add(appointment2);

        currentPatient.setAppointments(appointments);

        // When
        List<AppointmentDto> completedAppointments = appointmentService.findCompletedAppointments(currentPatient);

        // Then
        assertEquals(1, completedAppointments.size());

    }

    @Test
    void findUpcomingAppointmentsReturnsUpcomingAppointments() {
        // Given
        PatientDto currentPatient = new PatientDto();
        Set<AppointmentEntity> appointments = new HashSet<>();
        AppointmentEntity appointment1 = new AppointmentEntity();
        appointment1.setStatus("upcoming");
        appointments.add(appointment1);

        AppointmentEntity appointment2 = new AppointmentEntity();
        appointment2.setStatus("done");
        appointments.add(appointment2);

        currentPatient.setAppointments(appointments);

        // When
        List<AppointmentDto> upcomingAppointments = appointmentService.findUpcomingAppointments(currentPatient);

        // Then
        assertEquals(1, upcomingAppointments.size());

    }

    @Test
    @DisplayName("Should return all appointments with the given statuses")
    void findAllAppointmentsWithGivenStatuses() {
        List<String> statuses = Arrays.asList("done", "pending", "upcoming");

        List<AppointmentEntity> appointmentEntities = Arrays.asList(
                AppointmentEntity.builder().appointmentId(1).status("done").build(),
                AppointmentEntity.builder().appointmentId(2).status("done").build(),
                AppointmentEntity.builder().appointmentId(3).status("pending").build(),
                AppointmentEntity.builder().appointmentId(4).status("upcoming").build()
        );

        for (String status : statuses) {
            List<AppointmentDto> expectedAppointments = appointmentEntities.stream()
                    .filter(a -> a.getStatus().equals(status))
                    .map(appointmentMapper::mapFromEntity)
                    .collect(Collectors.toList());

            when(appointmentDao.findAllByStatus(status)).thenReturn(expectedAppointments);

            List<AppointmentDto> actualAppointments = appointmentService.findAllAppointmentsByStatus(status);

            assertEquals(expectedAppointments.size(), actualAppointments.size());
            assertThat(actualAppointments).containsExactlyInAnyOrderElementsOf(expectedAppointments);
            verify(appointmentDao, times(1)).findAllByStatus(status);
        }
    }

    @Test
    @DisplayName("Should return an empty list when there are no completed appointments for a specific doctor")
    void findAllCompletedAppointmentsForSpecificDoctorReturnsEmptyList() {
        String status = "done";
        Integer doctorId = 1;
        List<AppointmentDto> appointmentDtos = Collections.emptyList();
        when(appointmentDao.findAllByStatus(status)).thenReturn(appointmentDtos);

        List<AppointmentDto> result = appointmentService.findAllAppointmentsByStatusAndDoctorID(status, doctorId);

        assertThat(result).isEmpty();
        verify(appointmentDao, times(1)).findAllByStatus(status);
        verifyNoMoreInteractions(appointmentDao);
    }

    @Test
    @DisplayName("Should change the status of the appointment to 'done' and set the note when the appointment is found")
    void approveAppointmentWhenAppointmentIsFound() {
        Integer appointmentId = 1;
        String message = "Appointment approved";

        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setStatus("pending");

        Optional<AppointmentEntity> optionalAppointment = Optional.of(appointmentEntity);

        when(appointmentDao.findById(appointmentId)).thenReturn(optionalAppointment);

        appointmentService.approveAppointment(appointmentId, message);

        verify(appointmentDao, times(1)).findById(appointmentId);


        assertEquals("done", appointmentEntity.getStatus());
        assertEquals(message, appointmentEntity.getNoteOfAppointment());
    }

    @Test
    @DisplayName("Should throw NotFoundException when the appointment is not found")
    void approveAppointmentWhenAppointmentIsNotFoundThenThrowException() {
        Integer appointmentId = 1;
        String message = "Approved";

        when(appointmentDao.findById(appointmentId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> {
            appointmentService.approveAppointment(appointmentId, message);
        });

        // Verify
        verify(appointmentDao, times(1)).findById(appointmentId);

    }



    @Test
    @DisplayName("Should return the appointment when the appointmentId is valid")
    void findByIdWhenAppointmentIdIsValid() {
        Integer appointmentId = 1;
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setAppointmentId(appointmentId);
        when(appointmentDao.findById(appointmentId)).thenReturn(Optional.of(appointmentEntity));

     AppointmentEntity result = appointmentService.findById(appointmentId);

        assertNotNull(result);
        assertTrue(true);
        assertEquals(appointmentEntity, result);
        verify(appointmentDao, times(1)).findById(appointmentId);
    }

    @Test
    @DisplayName("Should return an empty list when there are no completed appointments with the given status")
    void findAllCompletedAppointmentsWithNoGivenStatus() {
        String status = "completed";
        when(appointmentDao.findAllByStatus(status)).thenReturn(Collections.emptyList());

        List<AppointmentDto> result = appointmentService.findAllAppointmentsByStatus(status);

        assertThat(result).isEmpty();
        verify(appointmentDao, times(1)).findAllByStatus(status);
    }
}