package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import com.medinet.util.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Model model;
    @Mock
    private Principal principal;
    @Mock
    private DoctorService doctorService;

    @Test
    public void testHomepage() {
        //when
        String viewName = homeController.homepage();

        // then
        assertEquals("home", viewName);
    }


    @Test
    public void testShowBookingPageAsAdminOrPatient() {
        //given
        String userEmail = "user@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password",
                List.of(new SimpleGrantedAuthority("ADMIN"), new SimpleGrantedAuthority("PATIENT")));

        when(principal.getName()).thenReturn(userEmail);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity currentUser = EntityFixtures.someDoctor1().getUser();
        when(userRepository.findByEmail(userEmail)).thenReturn(currentUser);

        Set<String> availableCities = Set.of("City1", "City2");
        Set<String> availableSpecializations = Set.of("Spec1", "Spec2");
        Page<DoctorDto> doctorsPage = mock(Page.class);
        when(doctorService.findAllAvailableCities()).thenReturn(availableCities);
        when(doctorService.findAllAvailableSpecialization()).thenReturn(availableSpecializations);
        when(doctorService.findAllDoctors(0)).thenReturn(doctorsPage);
        when(doctorsPage.getTotalElements()).thenReturn(10L);

        // when
        String viewName = homeController.showBookingPage(0, model, principal);

        // then
        assertEquals("mainPageBookingAppointments", viewName);
        verify(model, times(1)).addAttribute(eq("doctors"), any());
        verify(model, times(1)).addAttribute(eq("totalElements"), anyLong());
        verify(model, times(1)).addAttribute(eq("specializations"), any());
        verify(model, times(1)).addAttribute(eq("cities"), any());
        verify(model, times(1)).addAttribute(eq("dateFormatter"), any());
        verify(model, times(1)).addAttribute(eq("polishDayFormatter"), any());
        verify(model, times(1)).addAttribute(eq("user"), anyInt());
    }

    @Test
    public void testShowBookingPageAsDoctor() {
        // given
        String userEmail = "doctor@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password",
                Collections.singletonList(new SimpleGrantedAuthority("DOCTOR")));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        String viewName = homeController.showBookingPage(0, model, principal);

        // then
        assertEquals("redirect:/doctor", viewName);
    }

    @Test
    public void testShowSortedDoctorsPage() {
        ///given
        String userEmail = "user@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password",
                List.of(new SimpleGrantedAuthority("ADMIN"), new SimpleGrantedAuthority("PATIENT")));

        when(principal.getName()).thenReturn(userEmail);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity currentUser = EntityFixtures.someDoctor1().getUser();
        when(userRepository.findByEmail(userEmail)).thenReturn(currentUser);

        Set<String> availableCities = Set.of("City1", "City2");
        Set<String> availableSpecializations = Set.of("Spec1", "Spec2");
        Page<DoctorDto> doctorsPage = mock(Page.class);
        List<DoctorDto> allDoctorsInDatabase = List.of(new DoctorDto(), new DoctorDto()); // Sample data for allDoctorsInDatabase
        when(doctorService.findAllAvailableCities()).thenReturn(availableCities);
        when(doctorService.findAllAvailableSpecialization()).thenReturn(availableSpecializations);
        when(doctorService.findAllDoctorsBySpecializationAndCity(anyString(), anyString(), anyInt())).thenReturn(doctorsPage);
        when(doctorsPage.getTotalElements()).thenReturn(10L);
        when(doctorService.findAllDoctors()).thenReturn(allDoctorsInDatabase);

        // when
        String viewName = homeController.showSortedDoctorsPage("Spec1", "City1", 0, principal, model);

        // then
        assertEquals("mainPageBookingAppointments", viewName);
        verify(model, times(1)).addAttribute(eq("doctors"), any());
        verify(model, times(1)).addAttribute(eq("totalElements"), anyLong());
        verify(model, times(1)).addAttribute(eq("doctorsInDatabase"), any());
        verify(model, times(1)).addAttribute(eq("specializations"), any());
        verify(model, times(1)).addAttribute(eq("cities"), any());
        verify(model, times(1)).addAttribute(eq("dateFormatter"), any());
        verify(model, times(1)).addAttribute(eq("polishDayFormatter"), any());
        verify(model, times(1)).addAttribute(eq("user"), anyInt());
    }
}