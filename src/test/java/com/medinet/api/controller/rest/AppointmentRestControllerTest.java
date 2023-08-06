package com.medinet.api.controller.rest;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.RequestDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.CalendarService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;

import static com.medinet.util.EntityFixtures.someAppointment1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentRestControllerTest {
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private DoctorService doctorService;
    @Mock
    private CalendarService calendarService;

    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private PatientService patientService;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InvoiceJpaRepository invoiceJpaRepository;
    @InjectMocks
    private AppointmentRestController appointmentRestController;

    @Test
    public void testCreateAppointmentValidAppointment() {
        RequestDto requestDto = new RequestDto();
        LocalDate nextWednesday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        requestDto.setDateOfAppointment(nextWednesday);
        requestDto.setTimeOfVisit(LocalTime.of(10, 0));
        requestDto.setDoctorId(1);


        when(doctorService.findDoctorById(any(Integer.class))).thenReturn(new DoctorDto());
        when(calendarService.findByDoctorIdAndDateOfAppointment(isNull(), any(LocalDate.class))).thenReturn(new CalendarDto());

        ResponseEntity<?> responseEntity = appointmentRestController.createAppointment(requestDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    public void testCreateAppointmentDateMoreThanTwoWeeksFromToday() {
        //given
        RequestDto requestDto = new RequestDto();
        requestDto.setDateOfAppointment(LocalDate.now().plusWeeks(3));

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.createAppointment(requestDto);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid appointment date - you cannot schedule an appointment more than two weeks from today!", responseEntity.getBody());
    }

    @Test
    public void testCreateAppointmentDateEarlierThanTomorrow() {
        //given
        RequestDto requestDto = new RequestDto();
        requestDto.setDateOfAppointment(LocalDate.now());

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.createAppointment(requestDto);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid appointment date - you cannot schedule an appointment earlier than tomorrow!", responseEntity.getBody());
    }

    @Test
    public void testCreateAppointmentDateOnSaturday() {
        //given
        RequestDto requestDto = new RequestDto();
        LocalDate nextSaturday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        requestDto.setDateOfAppointment(nextSaturday);

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.createAppointment(requestDto);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid appointment date - you cannot schedule an appointment on the weekend!", responseEntity.getBody());
    }


    @Test
    public void testCreateAppointmentSlotAlreadyBooked() {
        // given
        LocalDate localDate = LocalDate.now().plusDays(1);
        if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            localDate = localDate.plusDays(4);
        }
        RequestDto requestDto = new RequestDto();
        LocalTime visitTime = LocalTime.of(14, 0);
        requestDto.setDateOfAppointment(localDate);
        requestDto.setTimeOfVisit(visitTime);
        requestDto.setDoctorId(1);

        when(appointmentService.existByDateAndTimeOfVisit(any(LocalDate.class), any(LocalTime.class))).thenReturn(true);

        // when
        ResponseEntity<?> responseEntity = appointmentRestController.createAppointment(requestDto);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("The selected time slot is already booked!", responseEntity.getBody());
    }

    @Test
    public void testFindAppointmentById() {
        //given
        Integer appointmentId = 1;

        AppointmentDto appointment = new AppointmentDto();

        when(appointmentService.findById(appointmentId)).thenReturn(appointment);

        //when
        ResponseEntity<AppointmentDto> responseEntity = appointmentRestController.AppointmentById(appointmentId);


        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(appointment, responseEntity.getBody());
    }


    @Test
    public void testAppointmentsByStatus() {
        //given
        String status = "done";
        List<AppointmentDto> appointments =
                Collections.singletonList(someAppointment1());

        when(appointmentService.findAllAppointmentsByStatus(status))
                .thenReturn(appointments);

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.AppointmentsByStatus(status);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(appointments, responseEntity.getBody());
    }

    @Test
    public void testUpdateAppointmentMessageDoneStatus() {
        AppointmentDto appointment = new AppointmentDto();
        appointment.setStatus("done");

        when(appointmentService.findById(anyInt())).thenReturn(appointment);

        ResponseEntity<?> response = appointmentRestController.updateAppointmentMessage(1, "New message");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You can not modify appointment with status done!", response.getBody());
    }

    @Test
    public void testUpdateAppointmentMessageUpcomingStatus() {
        AppointmentDto appointment = new AppointmentDto();
        appointment.setStatus("upcoming");

        when(appointmentService.findById(anyInt())).thenReturn(appointment);

        ResponseEntity<?> response = appointmentRestController.updateAppointmentMessage(1, "New message");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You can not modify appointment with status upcoming!", response.getBody());
    }

    @Test
    public void testUpdateAppointmentMessageNotFound() {
        when(appointmentService.findById(anyInt())).thenThrow(new NotFoundException("Appointment not found"));

        assertThrows(NotFoundException.class, () -> {
            appointmentRestController.updateAppointmentMessage(1, "New message");
        });
    }

    @Test
    public void testUpdateAppointmentMessageSuccess() {
        AppointmentDto appointment = new AppointmentDto();
        appointment.setStatus("pending");

        when(appointmentService.findById(anyInt())).thenReturn(appointment);
        Mockito.doNothing().when(appointmentService).approveAppointment(anyInt(), any());

        ResponseEntity<?> response = appointmentRestController.updateAppointmentMessage(1, "New message");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAppointmentsByStatusInvalid() {
        //given
        String status = "invalid";

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.AppointmentsByStatus(status);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid appointment status", responseEntity.getBody());
    }

    @Test
    public void testAppointmentsByStatusAndDoctorId() {
        //given
        String status = "done";
        Integer doctorId = 1;
        List<AppointmentDto> appointments = Collections.singletonList(someAppointment1());

        //when
        when(appointmentService.findAllAppointmentsByStatusAndDoctorID(status, doctorId)).thenReturn(appointments);

        //then
        ResponseEntity<?> responseEntity = appointmentRestController.AppointmentsByStatusAndDoctorId(status, doctorId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(appointments, responseEntity.getBody());
    }

    @Test
    public void testAppointmentsByStatusAndDoctorIdInvalid() {
        //given
        String status = "invalid";
        Integer doctorId = 1;

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.AppointmentsByStatusAndDoctorId(status, doctorId);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid appointment status", responseEntity.getBody());
    }


    @Test
    public void testDeleteAppointment() {
        //given
        Integer appointmentId = 1;
        AppointmentDto appointment = new AppointmentDto();
        appointment.setStatus("upcoming");

        when(appointmentService.findById(appointmentId)).thenReturn(appointment);

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.deleteAppointment(appointmentId);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteAppointmentNonExist() {
        //given
        Integer appointmentId = -1;

        when(appointmentService.findById(appointmentId)).thenReturn(null);

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.deleteAppointment(appointmentId);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Appointment with the provided ID does not exist", responseEntity.getBody());
    }

    @Test
    public void testDeleteAppointmentAlreadyDone() {
        //given
        Integer appointmentId = 1;
        AppointmentDto appointment = new AppointmentDto();
        appointment.setStatus("done");

        when(appointmentService.findById(appointmentId)).thenReturn(appointment);

        //when
        ResponseEntity<?> responseEntity = appointmentRestController.deleteAppointment(appointmentId);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Cannot cancel an appointment that has already taken place", responseEntity.getBody());
    }
}