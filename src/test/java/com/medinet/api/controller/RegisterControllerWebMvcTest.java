package com.medinet.api.controller;

import com.medinet.business.services.PatientService;
import com.medinet.business.services.RegisterService;
import com.medinet.infrastructure.security.RoleEntity;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

;

@WebMvcTest(controllers = RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RegisterControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PatientService patientService;
    @MockBean
    private RegisterService registerService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void testVerificationSuccess() throws Exception {
        String email = "patient@patient.pl";
        String code = "generated_code";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setRoles(new HashSet<>());
        userEntity.setActive(false);
        userEntity.setVerifyCode(code);

        RoleEntity doctorRole = new RoleEntity();
        doctorRole.setRole("PATIENT");


        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleRepository.findByRole("DOCTOR")).thenReturn(null);

        mockMvc.perform(post("/account/is-active")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/verification?success=true"));

        verify(roleRepository, times(1)).findByRole("DOCTOR");
        verify(registerService, times(1)).save(userEntity);
        assertTrue(userEntity.getActive());
    }

    @Test
    public void testVerificationAccountNotFound() throws Exception {

        String email = "nonexistent@example.com";
        String code = "1234";
        UserEntity user = new UserEntity();
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userRepository.existsByEmail(email)).thenReturn(false);


        mockMvc.perform(post("/account/is-active")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(view().name("accountActivate"))
                .andExpect(model().attribute("error", "Ten email nie istnieje w bazie danych"));
        verify(registerService, never()).save(any(UserEntity.class));
    }

    @Test
    public void testVerificationAccountNotRequired() throws Exception {

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


        mockMvc.perform(post("/account/is-active")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(view().name("accountActivate"))
                .andExpect(model().attribute("error", "To konto nie wymaga aktywacji"));
        verify(registerService, never()).save(any(UserEntity.class));
    }

    @Test
    public void testVerificationAccountBadCode() throws Exception {
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


        mockMvc.perform(post("/account/is-active")
                        .param("email", email)
                        .param("code", invalidCode))
                .andExpect(status().isOk())
                .andExpect(view().name("accountActivate"))
                .andExpect(model().attribute("error", "Kod niepoprawny"));
        verify(registerService, never()).save(any(UserEntity.class));
    }
}
