package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private DoctorService doctorService;
    @Mock
    private PatientService patientService;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Model model;

    @Mock
    private Principal principal;
    @Mock
    private InvoiceJpaRepository invoiceJpaRepository;
    private AppointmentController appointmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appointmentController = new AppointmentController(appointmentService, doctorService, patientService,
                doctorMapper, patientMapper, userRepository, invoiceJpaRepository);
    }

    @Test
    void sendRequestToQueueWithValidData() {
        //given
        Integer doctorId = 1;
        int patientId = 1;
        LocalTime timeOfVisit = LocalTime.of(10, 0);
        LocalDate dateOfAppointment = LocalDate.now();
        Integer calendarId = 1;
        String email = "test@example.com";
        UserEntity currentUser = new UserEntity();
        currentUser.setId(1);
        when(principal.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(currentUser);
        DoctorDto doctorDto = new DoctorDto();
        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);
        PatientDto patientDto = new PatientDto();
        when(patientService.findByUserId(patientId)).thenReturn(patientDto);
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        when(doctorMapper.mapFromDto(doctorDto)).thenReturn(new DoctorEntity());
        when(patientMapper.mapFromDto(patientDto)).thenReturn(new PatientEntity());
        //when
        String result = appointmentController.sendRequestToQueue(dateOfAppointment, timeOfVisit, "123456",
                calendarId, doctorId, principal);
        //then
        Assertions.assertEquals("redirect:/booking?success=true", result);
    }

    @Test
    void ThatReturnsAppointmentBookingPage() {
        // given
        Integer doctorId = 11;
        int patientId = 2;
        LocalTime timeOfVisit = LocalTime.of(12, 0);
        LocalDate dateOfAppointment = LocalDate.of(2023, 7, 12);
        Integer calendarId = 3;
        String expectedViewName = "appointmentBooking";

        DoctorDto doctorDto = new DoctorDto();
        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);

        PatientDto patientDto = new PatientDto();
        when(patientService.findByUserId(patientId)).thenReturn(patientDto);

        String UUID = "abcd1234";
        when(appointmentService.getVisitNumber()).thenReturn(UUID);

        //when
        String actualViewName = appointmentController.bookingAppointment(doctorId, patientId, timeOfVisit,
                dateOfAppointment, calendarId, model);

        //then
        Assertions.assertEquals(expectedViewName, actualViewName);

    }

    @Test
    void ThatSendRequestToQueueAndRedirectsToBookingPage() {
        // Given
        LocalDate dateOfAppointment = LocalDate.of(2023, 7, 12);
        LocalTime timeOfVisit = LocalTime.of(12, 0);
        String UUID = "abcd1234";
        Integer calendarId = 3;
        Integer doctorId = 1;
        UserEntity currentUser = new UserEntity();
        currentUser.setId(2);
        String email = principal.getName();
        when(userRepository.findByEmail(email)).thenReturn(currentUser);

        DoctorDto doctorDto = new DoctorDto();
        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);

        PatientDto patientDto = new PatientDto();
        when(patientService.findByUserId(currentUser.getId())).thenReturn(patientDto);

        // When
        String viewName = appointmentController.sendRequestToQueue(dateOfAppointment, timeOfVisit, UUID,
                calendarId, doctorId, principal);

        // Then
        Assertions.assertEquals("redirect:/booking?success=true, viewName);
    }

    @Test
    void thatApproveAppointmentAndRedirectToBookingPage() {
        //given
        int appointmentId = 1;
        String message = "test message";

        //when
        String viewName = appointmentController.approveAppointment(appointmentId, message);

        //then
        Assertions.assertEquals("redirect:/booking", viewName);
    }

    @Test
    void thatRemoveAppointmentAndRedirectToBooking() {
        int appointmentID = 1;
        LocalTime calendarHour = LocalTime.of(12, 0);
        int calendarId = 1;
        String email = "test@example.com";
        UserEntity currentUser = new UserEntity();
        currentUser.setId(2);

        when(principal.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(currentUser);

        //when
        String viewName = appointmentController.removeAppointment(appointmentID, calendarHour, calendarId, principal, model);

        //then
        Assertions.assertEquals("redirect:/account/user/2?remove=true", viewName);
    }

    @Test
    void thatGeneratePdfAndRedirectToBooking() throws InterruptedException {
        //given
        int appointmentId = 1;
       AppointmentEntity appointmentEntity = new AppointmentEntity();
        when(appointmentService.findById(appointmentId)).thenReturn(appointmentEntity);
        //when
        String viewName = appointmentController.generatePdf(appointmentId);
        //then
        Assertions.assertEquals("redirect:/account/user/1", viewName);

    }
}