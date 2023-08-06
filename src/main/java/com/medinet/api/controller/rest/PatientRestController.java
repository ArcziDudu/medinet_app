package com.medinet.api.controller.rest;


import com.medinet.api.controller.RegisterController;
import com.medinet.api.dto.PatientDto;
import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.PatientService;
import com.medinet.business.services.RegisterService;
import com.medinet.domain.exception.PatientEmailConflictException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.medinet.api.controller.RegisterController.*;

@RestController
@AllArgsConstructor
@RequestMapping(PatientRestController.API_PATIENT)
public class PatientRestController {
    public static final String API_PATIENT = "/api/patient";
    public static final String API_ONE_PATIENT = "/{patientId}";
    public static final String API_PATIENT_NEW = "/new";
    public static final String API_PATIENT_ACTIVE = "/active";
    public static final String PATIENT_ID_RESULT = "/%s";

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PatientService patientService;
    private final RegisterController registerController;
    private final RegisterService registerService;

    @GetMapping(value = API_ONE_PATIENT)
    @Operation(summary = "Get one patient by ID", description = "Retrieve information about a patient based on their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<PatientDto> onePatientById(@PathVariable Integer patientId) {
        PatientDto patientById = patientService.findById(patientId);
        if (Objects.isNull(patientById)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patientById);
    }

    @PostMapping(value = API_PATIENT_NEW, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new patient",
            description = "Create a new patient based on the provided registration form")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created"),
            @ApiResponse(responseCode = "409", description = "Conflict - Patient with the given email already exists"),
            @ApiResponse(responseCode = "400", description = "BadRequest - Email does not exists")
    })
    public ResponseEntity<?> createNewPatient(@Valid @RequestBody RegistrationFormDto registrationFormDto) {
        RoleEntity patient = roleRepository.findByRole("PATIENT");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(patient);
        String uuid = UUID.randomUUID().toString();
        if (userRepository.existsByEmail(registrationFormDto.getEmail())) {
            throw new PatientEmailConflictException("Patient with the given email already exists");
        }
        PatientEntity newPatient = prepareNewPatient(registrationFormDto, roles, uuid);

        registerController.sendEmail(registrationFormDto.getEmail(),
                String.valueOf(uuid),
                EMAIL_SUBJECT,
                TEXT_1_ACTIVE_CODE,
                TEXT_2_ACTIVE_YOUR_ACCOUNT);

        patientService.createNewPatient(newPatient);

        return ResponseEntity
                .created(URI.create(API_PATIENT_NEW + PATIENT_ID_RESULT.formatted(newPatient.getPatientId())))
                .build();
    }

    @PostMapping(value = API_PATIENT_ACTIVE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Activate patient ",
            description = "Activate patient by email and verify code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient active"),
            @ApiResponse(responseCode = "400", description = "Ten email nie istnieje w bazie danych"),
            @ApiResponse(responseCode = "400", description = "To konto nie wymaga aktywacji"),
            @ApiResponse(responseCode = "400", description = "To konto jest już aktywne"),
            @ApiResponse(responseCode = "400", description = "Niepoprawny kod aktywacyjny"),
    })
    public ResponseEntity<?> activatePatient(@RequestParam("email") String email,
                                             @RequestParam("activationCode") String activationCode) {

        UserEntity user = userRepository.findByEmail(email);
        RoleEntity doctorRole = roleRepository.findByRole("DOCTOR");
        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ten email nie istnieje w bazie danych");
        }
        if (user.getRoles().contains(doctorRole) || user.getEmail().equals("admin@admin.pl")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("To konto nie wymaga aktywacji");
        } else if (user.getActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("To konto jest już aktywne");
        } else if (!user.getVerifyCode().equals(activationCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Niepoprawny kod aktywacyjny");
        }

        user.setActive(true);
        registerService.save(user);
        return ResponseEntity
                .ok()
                .build();
    }


    private PatientEntity prepareNewPatient(RegistrationFormDto registrationFormDto, Set<RoleEntity> roles, String uuid) {
        UserEntity newPatientUser = getUserEntity(registrationFormDto, roles);
        newPatientUser.setVerifyCode(uuid);
        userRepository.save(newPatientUser);
        return newPatient(registrationFormDto, newPatientUser);
    }

    private UserEntity getUserEntity(RegistrationFormDto registrationFormDto, Set<RoleEntity> roles) {
        return UserEntity.builder()
                .email(registrationFormDto.getEmail())
                .password(passwordEncoder.encode(registrationFormDto.getPassword()))
                .roles(roles)
                .active(false)
                .build();
    }

    private PatientEntity newPatient(RegistrationFormDto registrationFormDto, UserEntity newPatientUser) {
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
