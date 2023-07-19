package com.medinet.api.controller;

import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.PatientService;
import com.medinet.business.services.RegisterService;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {
    @InjectMocks
    private RegisterController registerController;
    @Mock
    private RegisterService registerService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    @Captor
    private ArgumentCaptor<PatientEntity> patientEntityCaptor;

    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private PatientService patientService;
    @Test
    void login() {
        String login = registerController.login();
        assertEquals("login", login);
    }

    @Test
    void thatShowRegisterForm(){
        //given
        RegistrationFormDto form = new RegistrationFormDto();
        //when
        String result = registerController.showRegistrationForm(model);

        //then
        verify(model).addAttribute("form", form);
        assertEquals("register", result);
    }


    @Test
    void registrationValidFormRedirectToBooking() {
        // given
        RegistrationFormDto formDto = new RegistrationFormDto();
        formDto.setEmail("test@example.com");
        formDto.setPassword("testPassword");
        formDto.setName("John");
        formDto.setSurname("Doe");
        formDto.setPhoneNumber("123456789");
        formDto.setCity("SampleCity");
        formDto.setStreet("SampleStreet");
        formDto.setPostalCode("12345");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(patientService.findByPhoneNumber("123456789")).thenReturn(false);
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");

        // Act
        String result = registerController.registration(formDto, bindingResult);

        // Assert
        verify(userRepository).existsByEmail("test@example.com");
        verify(patientService).findByPhoneNumber("123456789");
        verify(patientService).createNewPatient(patientEntityCaptor.capture());
        verify(registerService).save(userEntityCaptor.capture());

        assertEquals("redirect:/booking", result);

        UserEntity capturedUserEntity = userEntityCaptor.getValue();
        assertEquals("test@example.com", capturedUserEntity.getEmail());
        assertEquals("encodedPassword", capturedUserEntity.getPassword());
        assertTrue(capturedUserEntity.getRoles().isEmpty());

        PatientEntity capturedPatientEntity = patientEntityCaptor.getValue();
        assertEquals("John", capturedPatientEntity.getName());
        assertEquals("Doe", capturedPatientEntity.getSurname());
        assertEquals("test@example.com", capturedPatientEntity.getEmail());
        assertEquals("123456789", capturedPatientEntity.getPhoneNumber());
        assertEquals("SampleCity", capturedPatientEntity.getAddress().getCity());
        assertEquals("SampleStreet", capturedPatientEntity.getAddress().getStreet());
        assertEquals("12345", capturedPatientEntity.getAddress().getPostalCode());
    }



}