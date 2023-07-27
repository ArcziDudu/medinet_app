package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AppointmentControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private PatientService patientService;

    @MockBean
    private PatientMapper patientMapper;
    @MockBean
    private AppointmentMapper appointmentMapper;
    @MockBean
    private DoctorMapper doctorMapper;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private Principal principal;
    @Mock
    private Model model;

    @Test
    @WithMockUser
    public void testSendRequestToQueue() throws Exception {

        LocalDate dateOfAppointment = LocalDate.now();
        String timeOfVisit = "10:00 AM";
        String UUID = "123456";
        int calendarId = 1;
        Integer doctorId = 2;
        String email = "user@example.com";
        int userId = 1;
        DoctorDto doctorDto = DoctorDto.builder().build();
        PatientDto patientDto = PatientDto.builder().build();
        UserEntity userEntity = UserEntity.builder().id(userId).build();


        when(userRepository.findByEmail(email)).thenReturn(userEntity);


        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);

        when(patientService.findByUserId(userId)).thenReturn(patientDto);

        AppointmentDto appointmentDto = new AppointmentDto();
        when(appointmentService.processAppointment(Mockito.any(AppointmentEntity.class)))
                .thenReturn(appointmentDto);


        mockMvc.perform(post("/booking/appointment")
                        .param("DateOfAppointment", dateOfAppointment.toString())
                        .param("HourOfAppointment", timeOfVisit)
                        .param("UUID", UUID)
                        .param("calendarId", Integer.toString(calendarId))
                        .param("doctorId", doctorId.toString())
                        .principal(() -> email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/booking"));
    }

    @Test
    @WithMockUser
    public void ThatApproveAppointment() throws Exception {
        Integer appointmentId = 1;
        String message = "Approved";

        mockMvc.perform(post("/appointment/approve/{appointmentId}", appointmentId)
                        .param("message", message))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/booking"));
        verify(appointmentService, times(1)).approveAppointment(appointmentId, message);
    }

    @Test
    void testBookingAppointment() throws Exception {
        // Test data
        int doctorId = 1;
        int patientId = 2;
        LocalTime selectedHour = LocalTime.of(10, 0);
        LocalDate selectedDate = LocalDate.of(2023, 7, 19);
        int calendarId = 3;
        String uuid = UUID.randomUUID().toString();


        DoctorDto doctor = new DoctorDto();
        doctor.setDoctorId(doctorId);
        when(doctorService.findDoctorById(doctorId)).thenReturn(doctor);


        PatientDto patient = new PatientDto();
        patient.setPatientId(patientId);
        when(patientService.findByUserId(patientId)).thenReturn(patient);


        when(appointmentService.getVisitNumber()).thenReturn(uuid);
        AddressEntity address = new AddressEntity();
        address.setCity("City Name");
        address.setStreet("Street Name");
        doctor.setAddress(address);

        doctor.setPriceForVisit(new BigDecimal(200));

        mockMvc.perform(get("/request")
                        .param("doctorId", String.valueOf(doctorId))
                        .param("patientId", String.valueOf(patientId))
                        .param("selectedHour", selectedHour.toString())
                        .param("selectedDate", selectedDate.toString())
                        .param("calendarId", String.valueOf(calendarId)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("appointmentBooking"))
                .andExpect(model().attribute("doctor", doctor))
                .andExpect(model().attribute("calendarId", calendarId))
                .andExpect(model().attribute("patient", patient))
                .andExpect(model().attribute("selectedHour", selectedHour))
                .andExpect(model().attribute("selectedDate", selectedDate))
                .andExpect(model().attribute("visitNumber", uuid));

        // Verify that the appropriate service methods were called
        verify(doctorService, times(1)).findDoctorById(doctorId);
        verify(patientService, times(1)).findByUserId(patientId);
        verify(appointmentService, times(1)).getVisitNumber();
    }

    @Test
    void testRemoveAppointment() throws Exception {
        //given
        int appointmentId = 1;
        LocalTime selectedHour = LocalTime.of(10, 0);
        int calendarId = 2;
        String userEmail = "user@example.com";
        int userId = 1;

        when(principal.getName()).thenReturn(userEmail);
        UserEntity currentUser = new UserEntity();
        currentUser.setId(userId);
        when(userRepository.findByEmail(userEmail)).thenReturn(currentUser);

        PatientDto currentPatient = new PatientDto();
        currentPatient.setPatientId(userId);
        when(patientService.findById(userId)).thenReturn(currentPatient);

        List<AppointmentDto> upcomingAppointments = List.of();
        List<AppointmentDto> completedAppointments = List.of();
        when(appointmentService.findUpcomingAppointments(currentPatient)).thenReturn(upcomingAppointments);
        when(appointmentService.findCompletedAppointments(currentPatient)).thenReturn(completedAppointments);

        //when
        mockMvc.perform(delete("/booking/remove/{appointmentId}", appointmentId)
                        .param("selectedHour", selectedHour.toString())
                        .param("calendarId", String.valueOf(calendarId))
                        .principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/booking"));

        //then
        verify(appointmentService, times(1)).processRemovingAppointment(appointmentId, selectedHour, calendarId);
        verify(patientService, times(1)).findById(userId);
        verify(appointmentService, times(1)).findUpcomingAppointments(currentPatient);
        verify(appointmentService, times(1)).findCompletedAppointments(currentPatient);
        verify(userRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    void testGeneratePdf() throws Exception {

        int appointmentId = 1;


        AppointmentEntity appointmentEntity = new AppointmentEntity();
        when(appointmentService.findById(appointmentId)).thenReturn(Optional.of(appointmentEntity));

        mockMvc.perform(post("/invoice/generatePdf/{appointmentId}", appointmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/account/user/1"));

        verify(appointmentService, times(1)).findById(appointmentId);
        verify(appointmentService, times(1)).generatePdf(Optional.of(appointmentEntity));
    }
}