package com.medinet.api.controller.rest;

import com.medinet.api.dto.PatientDto;
import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.security.RoleEntity;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(PatientRestController.API_PATIENT)
public class PatientRestController {
    public static final String API_PATIENT = "/api/patient";
    public static final String API_ONE_PATIENT = "/{patientId}";
    public static final String API_PATIENT_CREATE = "/create";
    public static final String PATIENT_ID_RESULT = "/%s";

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PatientService patientService;

    @GetMapping(value = API_ONE_PATIENT)
    @Operation(summary = "Get one patient by ID", description = "Retrieve information about a patient based on their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<PatientDto> onePatientById(@PathVariable Integer patientId) {
        PatientDto patientById = patientService.findById(patientId);
        if (Objects.isNull(patientById)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patientById);
    }

    @PostMapping(value = API_PATIENT_CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new patient",
            description = "Create a new patient based on the provided registration form")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created")
    })
    public ResponseEntity<?> createNewPatient(@Valid @RequestBody RegistrationFormDto registrationFormDto) {
        RoleEntity patient = roleRepository.findByRole("PATIENT");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(patient);

        PatientEntity newPatient = prepareNewPatient(registrationFormDto, roles);
        patientService.createNewPatient(newPatient);

        return ResponseEntity
                .created(URI.create(API_PATIENT_CREATE + PATIENT_ID_RESULT.formatted(newPatient.getPatientId())))
                .build();
    }


    private PatientEntity prepareNewPatient(RegistrationFormDto registrationFormDto, Set<RoleEntity> roles) {
        UserEntity newPatientUser = getUserEntity(registrationFormDto, roles);
        userRepository.save(newPatientUser);
        return newPatient(registrationFormDto, newPatientUser);
    }

    private UserEntity getUserEntity(RegistrationFormDto registrationFormDto, Set<RoleEntity> roles) {
        return UserEntity.builder()
                .email(registrationFormDto.getEmail())
                .password(passwordEncoder.encode(registrationFormDto.getPassword()))
                .roles(roles)
                .active(true)
                .build();
    }

    private static PatientEntity newPatient(RegistrationFormDto registrationFormDto, UserEntity newPatientUser) {
        return PatientEntity.builder()
                .user(newPatientUser)
                .appointments(new HashSet<>())
                .phoneNumber(registrationFormDto.getPhoneNumber())
                .name(registrationFormDto.getName())
                .surname(registrationFormDto.getSurname())
                .address(AddressEntity.builder()
                        .country("Poland")
                        .city(registrationFormDto.getCity())
                        .street(registrationFormDto.getStreet())
                        .postalCode(registrationFormDto.getPostalCode())
                        .build())
                .email(registrationFormDto.getEmail())
                .opinions(new HashSet<>())
                .build();
    }

}
