package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import com.medinet.util.EntityFixtures;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

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
    public void testBookingAppointment() throws Exception {
        // Given
        int doctorId = 1;
        int patientId = 2;
        String timeOfVisit = "10:00 AM";
        LocalDate dateOfAppointment = LocalDate.of(2023, 7, 12);
        int calendarId = 3;
        String expectedViewName = "appointmentBooking";
        DoctorEntity doctorEntity = EntityFixtures.someDoctor(passwordEncoder);
        PatientDto patientDto = new PatientDto();
        String UUID = "abcd1234";

        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorMapper.mapFromEntity(doctorEntity));
        when(patientService.findByUserId(patientId)).thenReturn(patientDto);
        when(appointmentService.getVisitNumber()).thenReturn(UUID);

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/request")
                        .param("doctorId", String.valueOf(doctorId))
                        .param("patientId", String.valueOf(patientId))
                        .param("selectedHour", timeOfVisit)
                        .param("selectedDate", dateOfAppointment.toString())
                        .param("calendarId", String.valueOf(calendarId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(expectedViewName))
                .andExpect(MockMvcResultMatchers.model().attribute("doctor", doctorEntity))
                .andExpect(MockMvcResultMatchers.model().attribute("patient", patientDto))
                .andExpect(MockMvcResultMatchers.model().attribute("visitNumber", UUID))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedHour", timeOfVisit))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedDate", dateOfAppointment))
                .andExpect(MockMvcResultMatchers.model().attribute("calendarId", calendarId));
    }

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


        Mockito.when(userRepository.findByEmail(email)).thenReturn(userEntity);


        Mockito.when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);

        Mockito.when(patientService.findByUserId(userId)).thenReturn(patientDto);

        AppointmentDto appointmentDto = new AppointmentDto();
        Mockito.when(appointmentService.processAppointment(Mockito.any(AppointmentEntity.class)))
                .thenReturn(appointmentDto);


        mockMvc.perform(MockMvcRequestBuilders.post("/booking/appointment")
                        .param("DateOfAppointment", dateOfAppointment.toString())
                        .param("HourOfAppointment", timeOfVisit)
                        .param("UUID", UUID)
                        .param("calendarId", Integer.toString(calendarId))
                        .param("doctorId", doctorId.toString())
                        .principal(() -> email)) // Set the authenticated user
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/booking"));
    }

    @Test
    @WithMockUser
    public void ThatApproveAppointment() throws Exception {
        Integer appointmentId = 1;
        String message = "Approved";

        mockMvc.perform(MockMvcRequestBuilders.post("/appointment/approve/{appointmentId}", appointmentId)
                        .param("message", message))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/booking"));
        verify(appointmentService, times(1)).approveAppointment(appointmentId, message);
    }



    @Test
    public void testRemoveAppointment() throws Exception {
        // Given
        int appointmentId = 1;
        String selectedHour = "12:00";
        int calendarId = 2;
        String principalEmail = "test@example.com";
        UserEntity currentUser = new UserEntity();
        currentUser.setId(1);
        currentUser.setEmail(principalEmail);
        when(userRepository.findByEmail(principalEmail)).thenReturn(currentUser);
        PatientDto currentPatient = new PatientDto();
        when(patientService.findById(currentUser.getId())).thenReturn(currentPatient);

        // When & Then
        mockMvc.perform(delete("/booking/remove/{appointmentId}", appointmentId)
                        .param("selectedHour", selectedHour)
                        .param("calendarId", String.valueOf(calendarId))
                        .principal(principal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(redirectedUrl("/booking"));

        verify(appointmentService, times(1)).processRemovingAppointment(appointmentId, selectedHour, calendarId);
        verify(patientService, times(1)).findById(currentUser.getId());
    }


}