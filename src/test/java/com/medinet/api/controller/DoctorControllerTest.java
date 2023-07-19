package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

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

        // Perform the method call
        String viewName = doctorController.getUser(1, model);

        // Assertions
        assertEquals("myAccountDoctor", viewName);

        verify(model, times(1)).addAttribute(eq("doctor"), eq(requestedDoctor));
    }

}