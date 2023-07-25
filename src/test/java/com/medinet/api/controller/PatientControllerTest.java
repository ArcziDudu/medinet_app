package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.util.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    @InjectMocks
    private PatientController patientController;
    @Mock
    private PatientService patientService;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private Model model;
    @Test
    void showUsersPage() {
        //given
        PatientDto patientDto = patientMapper.mapFromEntity(EntityFixtures.patient1());
        List<AppointmentDto> upcoming = new ArrayList<>();
        List<AppointmentDto> completed = new ArrayList<>();

        when(appointmentService.findUpcomingAppointments(patientDto)).thenReturn(upcoming);
        when(appointmentService.findCompletedAppointments(patientDto)).thenReturn(completed);

        //when
        String result = patientController.showUsersPage(1, model);

        //then
        verify(model).addAttribute("CurrentPatient", patientDto);
        verify(model).addAttribute("UpcomingAppointments", upcoming);
        verify(model).addAttribute("CompletedAppointments", completed);
        assertEquals("myAccount", result);
    }
}