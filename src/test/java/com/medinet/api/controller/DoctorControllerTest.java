package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    @Mock
    private DoctorService doctorService;
    @Mock
    private Model model;
    @Mock
    private Principal principal;
    @Mock
    private DoctorMapper doctorMapper;
    @InjectMocks
    private DoctorController doctorController;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private ModelAndView modelAndView;

    @Mock
    private Formatter formatter;


    @Test
    public void testPrepareNecessaryDataForDoctor() {
        // given
        String userEmail = "doctor@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password");
        when(principal.getName()).thenReturn(userEmail);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        DoctorDto doctorDto = new DoctorDto();
        doctorDto.setDoctorId(1);
        when(doctorService.findByEmail(userEmail)).thenReturn(doctorDto);
        when(doctorService.findDoctorById(doctorDto.getDoctorId())).thenReturn(doctorDto);


        List<AppointmentDto> completedAppointments = Arrays.asList(new AppointmentDto(), new AppointmentDto());
        List<AppointmentDto> pendingAppointments = Arrays.asList(new AppointmentDto());
        List<AppointmentDto> upcomingAppointments = Arrays.asList(new AppointmentDto(), new AppointmentDto());
        when(appointmentService.findAllAppointmentsByStatusAndDoctorID("done", doctorDto.getDoctorId()))
                .thenReturn(completedAppointments);
        when(appointmentService.findAllAppointmentsByStatusAndDoctorID("pending", doctorDto.getDoctorId()))
                .thenReturn(pendingAppointments);
        when(appointmentService.findAllAppointmentsByStatusAndDoctorID("upcoming", doctorDto.getDoctorId()))
                .thenReturn(upcomingAppointments);

        // wehn
        Map<String, ?> data = doctorController.prepareNecessaryDataForDoctor(principal);

        // then
        assertEquals(doctorDto, data.get("doctor"));
        assertEquals(completedAppointments, data.get("completedAppointment"));
        assertEquals(pendingAppointments, data.get("pendingAppointment"));
        assertEquals(upcomingAppointments, data.get("upcomingAppointment"));
    }

    @Test
    public void testDoctorMainPage() {
        // given
        String userEmail = "doctor@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password");
        when(principal.getName()).thenReturn(userEmail);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        DoctorDto doctorDto = new DoctorDto();
        doctorDto.setDoctorId(1);

        when(doctorService.findByEmail(userEmail)).thenReturn(doctorDto);
        when(doctorService.findDoctorById(doctorDto.getDoctorId())).thenReturn(doctorDto);

        // when
        ModelAndView modelAndView = doctorController.doctorMainPage(principal);

        // then
        assertEquals("DoctorUpcomingAppointments", modelAndView.getViewName());
    }

    @Test
    @DisplayName("Should throw an exception when invalid doctorId is provided")
    void showDoctorDetailsPageWhenInvalidDoctorIdIsProvidedThenThrowException() {
        Integer invalidDoctorId = -1;
        when(doctorService.findDoctorById(invalidDoctorId)).thenReturn(null);

        String result = doctorController.showDoctorDetailsPage(invalidDoctorId, model);

        verify(doctorService, times(1)).findDoctorById(invalidDoctorId);
        verify(model, times(1)).addAttribute(eq("doctor"), isNull());
        verify(model, times(1)).addAttribute(eq("dateFormatter"), any(DateTimeFormatter.class));
        verify(model, times(1)).addAttribute(eq("polishDayFormatter"), any(DateTimeFormatter.class));
        assertEquals("doctorDetails", result);
    }

    @Test
    public void testShowSortedDoctorsPage() {
        //given
        Integer doctorId = 1;
        DoctorDto doctorDto = new DoctorDto();

        when(doctorService.findDoctorById(doctorId)).thenReturn(doctorDto);

        //when
        String result = doctorController.showDoctorDetailsPage(doctorId, model);

        //then
        assertEquals("doctorDetails", result);
        verify(model).addAttribute("doctor", doctorDto);
    }

    @Test
    void testGetUser() {

        DoctorDto requestedDoctor = new DoctorDto();
        requestedDoctor.setDoctorId(1);
        requestedDoctor.setName("John Doe");
        requestedDoctor.setEmail("john.doe@example.com");

        when(doctorService.findDoctorById(1)).thenReturn(requestedDoctor);


        String viewName = doctorController.getUser(1, model);


        assertEquals("myAccountDoctor", viewName);

        verify(model, times(1)).addAttribute(eq("doctor"), eq(requestedDoctor));
    }
}