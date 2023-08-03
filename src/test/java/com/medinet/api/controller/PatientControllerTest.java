package com.medinet.api.controller;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.ChangePasswordForm;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    @InjectMocks
    private PatientController patientController;
    @Mock
    private PatientService patientService;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Principal principal;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private Model model;

    @Test
    void showUsersPage() {
        //given
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientId(1);
        patientDto.setOpinions(Set.of());
        List<AppointmentDto> upcoming = new ArrayList<>();
        List<AppointmentDto> completed = new ArrayList<>();
        List<AppointmentDto> pending = new ArrayList<>();
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();

        when(patientService.findByUserId(1)).thenReturn(patientDto);
        when(appointmentService.findUpcomingAppointments(patientDto)).thenReturn(upcoming);
        when(appointmentService.findCompletedAppointments(patientDto)).thenReturn(completed);
        when(appointmentService.findPendingAppointments(patientDto)).thenReturn(pending);

        //when
        String result = patientController.showUsersPage(1, model);

        //then
        verify(model).addAttribute("CurrentPatient", patientDto);
        verify(model).addAttribute("passwordForm", changePasswordForm);
        verify(model).addAttribute("UpcomingAppointments", upcoming);
        verify(model).addAttribute("CompletedAppointments", completed);
        verify(model).addAttribute("pendingAppointments", pending);
        assertEquals("myAccount", result);
    }

    @Test
    public void testChangePassword() {

        ChangePasswordForm passwordForm = new ChangePasswordForm();
        passwordForm.setCurrentPassword("oldPassword");
        passwordForm.setNewPassword("newPassword");

        UserEntity user = new UserEntity();
        user.setPassword("oldPassword");

        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("oldPassword", "oldPassword")).thenReturn(true);


        String result = patientController.changePassword(passwordForm, bindingResult, model, principal);

        verify(userRepository, times(1)).save(any(UserEntity.class));
        assertThat(result).isEqualTo("myAccount");
    }

    @Test
    public void testChangePasswordIncorrect() {

        ChangePasswordForm passwordForm = new ChangePasswordForm();
        passwordForm.setCurrentPassword("oldPassword");
        passwordForm.setNewPassword("newPassword");

        UserEntity user = new UserEntity();
        user.setPassword("oldPassword");

        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("oldPassword", "oldPassword")).thenReturn(false);


        String result = patientController.changePassword(passwordForm, bindingResult, model, principal);
        verify(model).addAttribute("error", "aktualne hasło nieprawidłowe!");
        verify(userRepository, times(0)).save(any(UserEntity.class));
        assertThat(result).isEqualTo("myAccount");
    }

    @Test
    public void testChangePasswordWithBindingErrors() {

        ChangePasswordForm passwordForm = new ChangePasswordForm();
        UserEntity currentUser = new UserEntity();


        when(principal.getName()).thenReturn("test@test.com");
        when(userRepository.findByEmail(anyString())).thenReturn(currentUser);
        when(passwordEncoder.matches(null, null)).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(true); // binding result has errors


        String returnValue = patientController.changePassword(passwordForm, bindingResult, model, principal);


        verify(model, times(1)).addAttribute("error", "Nowe hasło musi składać się z minimum sześciu znaków oraz musi zawierać co najmniej jedną cyfrę.");
        verify(userRepository, times(1)).save(any(UserEntity.class));


        assertEquals("myAccount", returnValue);
    }
}