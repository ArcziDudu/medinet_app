package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.infrastructure.security.UserRepository;
import com.medinet.util.EntityFixtures;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = AppointmentController.class)
@WithMockUser
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
    private DoctorMapper doctorMapper;
    @MockBean
    private UserRepository userRepository;



    @Mock
    private Model model;

    @Test
    public void testBookingAppointment() throws Exception {
        // Given
        int doctorId = 1;
        int patientId = 2;
        String timeOfVisit = "10:00 AM";
        LocalDate dateOfAppointment = LocalDate.of(2023, 7, 12);
        int calendarId = 3;
        String expectedViewName = "appointmentBooking";
        DoctorDto doctorDto = EntityFixtures.someDoctor(passwordEncoder);
        PatientDto patientDto = new PatientDto();
        String UUID = "abcd1234";

        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);
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
                .andExpect(MockMvcResultMatchers.model().attribute("doctor", doctorDto))
                .andExpect(MockMvcResultMatchers.model().attribute("patient", patientDto))
                .andExpect(MockMvcResultMatchers.model().attribute("visitNumber", UUID))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedHour", timeOfVisit))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedDate", dateOfAppointment))
                .andExpect(MockMvcResultMatchers.model().attribute("calendarId", calendarId));
    }

}
