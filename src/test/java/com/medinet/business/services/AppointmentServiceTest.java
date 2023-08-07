package com.medinet.business.services;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.AppointmentDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import com.medinet.infrastructure.repository.mapper.CalendarMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    private CalendarMapper calendarMapper;
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

        CalendarDto calendar = new CalendarDto();
        calendar.setCalendarId(calendarId);
        calendar.setHours(new ArrayList<>());

        when(calendarService.findById(calendarId)).thenReturn(calendar);

        //when
        appointmentService.processRemovingAppointment(appointmentId, calendarHour, calendarId);
        //then
        verify(appointmentDao).removeAppointment(appointmentId);

        List<LocalTime> hoursAfterProcess = calendar.getHours();
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
        //given
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

            //when
            List<AppointmentDto> actualAppointments = appointmentService.findAllAppointmentsByStatus(status);

            //then
            assertEquals(expectedAppointments.size(), actualAppointments.size());
            assertThat(actualAppointments).containsExactlyInAnyOrderElementsOf(expectedAppointments);
            verify(appointmentDao, times(1)).findAllByStatus(status);
        }
    }

    @Test
    @DisplayName("Should return an empty list when there are no completed appointments for a specific doctor")
    void findAllCompletedAppointmentsForSpecificDoctorReturnsEmptyList() {
        //given
        String status = "done";
        Integer doctorId = 1;
        List<AppointmentDto> appointmentDtos = Collections.emptyList();
        when(appointmentDao.findAllByStatus(status)).thenReturn(appointmentDtos);

        //when
        List<AppointmentDto> result = appointmentService.findAllAppointmentsByStatusAndDoctorID(status, doctorId);

        //then
        assertThat(result).isEmpty();
        verify(appointmentDao, times(1)).findAllByStatus(status);
        verifyNoMoreInteractions(appointmentDao);
    }

    @Test
    public void testProcessAppointmentPendingStatus() {
        // given
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setDateOfAppointment(today.minusDays(1));
        appointment.setTimeOfVisit(now);
        appointment.setCalendarId(1);

        CalendarDto calendar = new CalendarDto();
        List<LocalTime> hours = new ArrayList<>();
        hours.add(now);
        calendar.setHours(hours);

        when(calendarService.findById(appointment.getCalendarId())).thenReturn(calendar);

        // when
        appointmentService.processAppointment(appointment);

        // then
        assertEquals("pending", appointment.getStatus());
        verify(appointmentDao, times(1)).saveAppointment(appointment);

    }

    @Test
    void testFindByIdWhenAppointmentDoesNotExist() {
        //given
        Integer appointmentId = 2;
        when(appointmentDao.findById(appointmentId)).thenReturn(Optional.empty());

        //when //then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> appointmentService.findById(appointmentId));
        assertEquals("Could not find appointment by Id: [2]", exception.getMessage());
        verify(appointmentDao, times(1)).findById(appointmentId);
    }

    @Test
    void testApproveAppointmentWhenAppointmentExists() {
        //given
        Integer appointmentId = 1;
        String message = "Appointment approved";
        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setStatus("pending");
        when(appointmentDao.findById(appointmentId)).thenReturn(Optional.of(appointmentDto));

        //when
        appointmentService.approveAppointment(appointmentId, message);

        //then
        assertEquals("done", appointmentDto.getStatus());
        assertEquals(message, appointmentDto.getNoteOfAppointment());
        verify(appointmentDao, times(1)).findById(appointmentId);
    }

    @Test
    void testApproveAppointmentWhenAppointmentDoesNotExist() {
        //given
        Integer appointmentId = 2;
        String message = "Appointment approved";
        when(appointmentDao.findById(appointmentId)).thenReturn(Optional.empty());

        //when then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> appointmentService.approveAppointment(appointmentId, message));
        assertEquals("Could not find appointment by Id: [2]", exception.getMessage());
        verify(appointmentDao, times(1)).findById(appointmentId);
    }

    @Test
    void testFindByIdWhenAppointmentExists() {
        //given
        Integer appointmentId = 1;
        AppointmentDto appointmentDto = new AppointmentDto();
        when(appointmentDao.findById(appointmentId)).thenReturn(Optional.of(appointmentDto));

        //when
        AppointmentDto result = appointmentService.findById(appointmentId);

        //then
        assertNotNull(result);
        assertEquals(appointmentDto, result);
        verify(appointmentDao, times(1)).findById(appointmentId);
    }

    @Test
    @DisplayName("Should return an empty list when there are no completed appointments with the given status")
    void findAllCompletedAppointmentsWithNoGivenStatus() {
        //given
        String status = "completed";
        when(appointmentDao.findAllByStatus(status)).thenReturn(Collections.emptyList());

        //when
        List<AppointmentDto> result = appointmentService.findAllAppointmentsByStatus(status);

        //then
        assertThat(result).isEmpty();
        verify(appointmentDao, times(1)).findAllByStatus(status);
    }
}