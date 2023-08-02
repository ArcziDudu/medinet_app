package com.medinet.api.controller;

import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.PatientService;
import com.medinet.business.services.RegisterService;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.security.RoleEntity;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {
    @Mock
    private RegisterService registerService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private RoleRepository roleRepository;
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
    @InjectMocks
    private RegisterController registerController;

    @Test
    void login() {
        String login = registerController.login();
        assertEquals("login", login);
    }

    @Test
    void verification() {
        String verify = registerController.verifyAccount();
        assertEquals("accountActivate", verify);
    }

    @Test
    void thatShowRegisterForm() {
        //given
        RegistrationFormDto form = new RegistrationFormDto();
        //when
        String result = registerController.showRegistrationForm(model);

        //then
        verify(model).addAttribute("form", form);
        assertEquals("register", result);
    }

    @Test
    public void testShowPasswordReminderForm() {
        String viewName = registerController.showPasswordReminderForm(model);
        verify(model, times(1)).addAttribute("email", "");
        assertEquals("PasswordRecovery", viewName);
    }

    @Test
    public void testRecoveryPasswordNonExistingEmail() throws MessagingException, UnsupportedEncodingException {
        String email = "doctor@example.com";
        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");
        UserEntity user = new UserEntity();
        user.setRoles(Collections.singleton(doctorRole));
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        String viewName = registerController.recoveryPassword(email, model);
        verify(model, times(1)).addAttribute("error", "Ten email nie istnieje w bazie danych");
        assertEquals("PasswordRecovery", viewName);
    }

    @Test
    public void testRecoveryPasswordEmptyEmail() {
        String email = null;
        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");
        UserEntity user = new UserEntity();
        user.setRoles(Collections.singleton(doctorRole));
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);

        String viewName = registerController.recoveryPassword(email, model);

        verify(model, times(1)).addAttribute("error", "Wprowadź adres email");
        assertEquals("PasswordRecovery", viewName);
    }

    @Test
    public void testRecoveryPasswordDoctorEmail() {
        String email = "doctor@example.com";
        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");
        UserEntity user = new UserEntity();
        user.setRoles(Collections.singleton(doctorRole));
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        String viewName = registerController.recoveryPassword(email, model);
        verify(model, times(1)).addAttribute("error", "Ten email należy do lekarza!");
        assertEquals("PasswordRecovery", viewName);
    }

    @Test
    public void testRecoveryPasswordCorrect() {
        // given
        String email = "test@example.com";
        String generatedUuid = "b3b7ec30-7ef0-4c82-970e-a408b28349a3";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setRoles(new HashSet<>());

        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        // when
        String result = registerController.recoveryPassword(email, model);

        // then
        verify(model, never()).addAttribute(eq("error"), anyString());
        verify(userRepository).save(userEntity);

        assertEquals("redirect:/password/reminder?success=true", result);
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
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        String result = registerController.registration(formDto, bindingResult);

        // then
        verify(userRepository).existsByEmail("test@example.com");
        verify(patientService).findByPhoneNumber("123456789");
        verify(patientService).createNewPatient(patientEntityCaptor.capture());
        verify(registerService).save(userEntityCaptor.capture());

        assertEquals("redirect:/register?success=true", result);

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

    @Test
    void verificationSuccess() {
        // given
        String email = "test@example.com";
        String code = "generated_code";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setRoles(new HashSet<>());
        userEntity.setActive(false);
        userEntity.setVerifyCode(code);

        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);

        // when
        String result = registerController.verification(email, code, model);

        // then
        verify(model, never()).addAttribute(eq("error"), anyString());
        verify(registerService).save(userEntity);

        assertEquals("redirect:/verification?success=true", result);
        assertTrue(userEntity.getActive());
    }
    @Test
    void verificationExistedEmail() {
        // given
        String email = "test@example.com";
        String code = "generated_code";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setRoles(new HashSet<>());
        userEntity.setActive(false);
        userEntity.setVerifyCode(code);

        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);

        // when
        String result = registerController.verification(email, code, model);

        // then
        assertEquals("accountActivate", result);
        verify(model).addAttribute("error", "Ten email nie istnieje w bazie danych");
        assertFalse(userEntity.getActive());
    }
    @Test
    void verificationDoesNotRequireActivation() {
        // given
        String email = "admin@admin.pl";
        String code = "generated_code";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setRoles(new HashSet<>());
        userEntity.setActive(false);
        userEntity.setVerifyCode(code);

        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);

        // when
        String result = registerController.verification(email, code, model);

        // then
        verify(model).addAttribute("error", "To konto nie wymaga aktywacji");
        verify(registerService, never()).save(userEntity);

        assertEquals("accountActivate", result);
        assertFalse(userEntity.getActive());
    }
    @Test
    void verificationDoesNotRequireActivationSecond() {
        // given
        String email = "patient@patient.pl";
        String code = "generated_code";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setRoles(new HashSet<>());
        userEntity.setActive(true);
        userEntity.setVerifyCode(code);

        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);

        // when
        String result = registerController.verification(email, code, model);

        // then
        verify(model).addAttribute("error", "To konto nie wymaga aktywacji");
        verify(registerService, never()).save(userEntity);

        assertEquals("accountActivate", result);
        assertTrue(userEntity.getActive());
    }
    @Test
    void verificationInvalidActivationCode() {
        // given
        String email = "test@example.com";
        String invalidCode = "invalid_code";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setRoles(new HashSet<>());
        userEntity.setActive(false);
        userEntity.setVerifyCode("generated_code");

        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("DOCTOR");

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleRepository.findByRole("DOCTOR")).thenReturn(doctorRole);

        // when
        String result = registerController.verification(email, invalidCode, model);

        // then
        verify(model).addAttribute("error", "Kod niepoprawny");
        verify(registerService, never()).save(userEntity);

        assertEquals("accountActivate", result);
        assertFalse(userEntity.getActive());
    }
}