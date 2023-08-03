package com.medinet.api.controller;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import com.medinet.util.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    public void testPolicy(){
        //when
        String viewName = homeController.policy();
        //then
        assertEquals("policy", viewName);
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
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();
        CalendarEntity calendar1 = CalendarEntity.builder()
                .doctor(doctorEntity)
                .hours(List.of())
                .date(LocalDate.of(2022, 7, 22))
                .build();
        CalendarEntity calendar2 = CalendarEntity.builder()
                .doctor(doctorEntity)
                .hours(List.of())
                .date(LocalDate.of(2022, 7, 15))
                .build();
        CalendarEntity calendar3 = CalendarEntity.builder()
                .doctor(doctorEntity)
                .hours(List.of())
                .date(LocalDate.of(2014, 7, 15))
                .build();
        DoctorDto doctorDto = EntityFixtures.someDoctorDto();
        DoctorDto doctorDto2 = EntityFixtures.someDoctorDto();
        TreeSet<CalendarEntity> calendars = new TreeSet<>();
        calendars.add(calendar1);
        calendars.add(calendar2);
        calendars.add(calendar3);

        doctorDto.setCalendars(calendars);
        doctorDto2.setCalendars(calendars);

        List<DoctorDto> doctorsList = new ArrayList<>();
        doctorsList.add(doctorDto);
        doctorsList.add(doctorDto2);

        Page<DoctorDto> doctorsPage = new PageImpl<>(doctorsList);
        doctorDto.setCalendars(calendars);
        List<DoctorDto> allDoctorsInDatabase = List.of(doctorDto, doctorDto2);

        when(doctorService.findAllAvailableCities()).thenReturn(availableCities);
        when(doctorService.findAllAvailableSpecialization()).thenReturn(availableSpecializations);
        when(doctorService.findAllDoctorsBySpecializationAndCity(anyString(), anyString(), anyInt())).thenReturn(doctorsPage);
        when(doctorService.findAllDoctors()).thenReturn(allDoctorsInDatabase);

        // when
        String viewName = homeController.showSortedDoctorsPage("Spec1", "City1", 0, principal, model);

        // then
        TreeSet<CalendarEntity> calendarsAfter = doctorDto.getCalendars();
        assertEquals(3, calendarsAfter.size());
        assertEquals(calendarsAfter.first().getDate(), LocalDate.of(2014, 7, 15));
        assertEquals(calendarsAfter.stream()
                .map(CalendarEntity::getDate)
                .toList(), calendarsAfter.stream()
                .map(CalendarEntity::getDate)
                .sorted()
                .collect(Collectors.toList()));
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