package com.medinet.api.controller.rest;

import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.security.RoleEntity;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static com.medinet.api.controller.rest.PatientRestController.API_PATIENT_NEW;
import static com.medinet.api.controller.rest.PatientRestController.PATIENT_ID_RESULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientRestControllerTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PatientService patientService;
    @InjectMocks
    private PatientRestController patientRestController;

    @Test
    public void testCreateNewPatient() {
        RegistrationFormDto registrationForm = new RegistrationFormDto();
        RoleEntity patientRole = new RoleEntity();
        patientRole.setRole("PATIENT");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(patientRole);

        PatientEntity newPatient = new PatientEntity();
        PatientEntity expectedPatient = new PatientEntity();

        when(roleRepository.findByRole("PATIENT")).thenReturn(patientRole);

        ArgumentCaptor<PatientEntity> patientCaptor = ArgumentCaptor.forClass(PatientEntity.class);

        patientRestController.createNewPatient(registrationForm);

        verify(patientService).createNewPatient(patientCaptor.capture());
        PatientEntity capturedPatient = patientCaptor.getValue();

        ResponseEntity<?> responseEntity = patientRestController.createNewPatient(registrationForm);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(URI.create(API_PATIENT_NEW + PATIENT_ID_RESULT.formatted(expectedPatient.getPatientId())),
                responseEntity.getHeaders().getLocation());
    }
}