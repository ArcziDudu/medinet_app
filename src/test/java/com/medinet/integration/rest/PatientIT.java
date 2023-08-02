package com.medinet.integration.rest;

import com.medinet.api.dto.PatientDto;
import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.jpa.PatientJpaRepository;
import com.medinet.integration.configuration.RestAssuredIntegrationTestBase;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.medinet.api.controller.rest.PatientRestController.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class PatientIT extends RestAssuredIntegrationTestBase {
    @Autowired
    private PatientJpaRepository patientJpaRepository;
    @Autowired
    private PatientService patientService;

    @Test
    public void testCreateNewPatient() {
        List<PatientEntity> all = patientJpaRepository.findAll();
        assertEquals(1, all.size());
        RegistrationFormDto registrationFormDto = getRegistrationFormDto();
        given().spec(requestSpecification())
                .contentType(ContentType.JSON)
                .body(registrationFormDto)
                .when()
                .post("http://localhost:" + port + basePath + API_PATIENT +
                        API_PATIENT_NEW)
                .then()
                .log().everything()
                .statusCode(201);
        List<PatientEntity> allAfter = patientJpaRepository.findAll();
        assertEquals(2, allAfter.size());
        patientJpaRepository.deleteById(allAfter.get(1).getPatientId());
    }

    @Test
    public void testGetOnePatientById() {
        PatientDto patientById = patientService.findById(1);
        given().spec(requestSpecification())
                .contentType(ContentType.JSON)
                .pathParam("patientId", 1)
                .when()
                .get("http://localhost:" + port + basePath + API_PATIENT +
                        API_ONE_PATIENT)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("patientId", equalTo(patientById.getPatientId()))
                .body("name", equalTo(patientById.getName()))
                .body("email", equalTo(patientById.getEmail()));
    }

    private static RegistrationFormDto getRegistrationFormDto() {
        return RegistrationFormDto
                .builder()
                .name("Test")
                .surname("Testowy")
                .email("email@testowy.test")
                .password("test123A@")
                .city("Test")
                .street("Testowa 23")
                .postalCode("33-314")
                .phoneNumber("+48 511 111 111")
                .build();
    }
}
