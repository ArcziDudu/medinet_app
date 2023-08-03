package com.medinet.integration.rest;

import com.medinet.api.dto.PatientDto;
import com.medinet.api.dto.RegistrationFormDto;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.repository.jpa.PatientJpaRepository;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import com.medinet.integration.configuration.RestAssuredIntegrationTestBase;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.medinet.api.controller.rest.PatientRestController.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class PatientIT extends RestAssuredIntegrationTestBase {
    @Autowired
    private PatientJpaRepository patientJpaRepository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setup(){
        UserEntity userForTests = UserEntity.builder()
                .email("medinet@test.pl")
                .password("password")
                .roles(Set.of())
                .verifyCode("verifyCode")
                .active(false)
                .build();
        UserEntity userForTests2 = UserEntity.builder()
                .email("medinet@test2.pl")
                .password("password")
                .roles(Set.of())
                .verifyCode("verifyCode")
                .active(true)
                .build();
        UserEntity userForTests3 = UserEntity.builder()
                .email("medinet@test3.pl")
                .password("password")
                .roles(Set.of())
                .verifyCode("verifyCode")
                .active(false)
                .build();
        userRepository.saveAndFlush(userForTests);
        userRepository.saveAndFlush(userForTests2);
        userRepository.saveAndFlush(userForTests3);
    }
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
        assertFalse(allAfter.get(1).getUser().getActive());
        patientJpaRepository.deleteById(allAfter.get(1).getPatientId());
    }

    @Test
    public void testCreateNewPatientWithExistsEmail() {
        List<PatientEntity> all = patientJpaRepository.findAll();
        assertEquals(1, all.size());
        RegistrationFormDto registrationFormDto = getRegistrationFormDto();
        registrationFormDto.setEmail("admin@admin.pl");
        given().spec(requestSpecification())
                .contentType(ContentType.JSON)
                .body(registrationFormDto)
                .when()
                .post("http://localhost:" + port + basePath + API_PATIENT +
                        API_PATIENT_NEW)
                .then()
                .log().everything()
                .statusCode(409);
        List<PatientEntity> allAfter = patientJpaRepository.findAll();
        assertEquals(1, allAfter.size());
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

    @Test
    public void testActivatePatientEmailNotFound() {
        String email = "nonexistentSecond@example.com";
        String activationCode = "123456";
        given()
                .param("email", email)
                .param("activationCode", activationCode)
                .when()
                .post("http://localhost:" + port + basePath + API_PATIENT +
                        API_PATIENT_ACTIVE)
                .then()
                .statusCode(400)
                .body(equalTo("Ten email nie istnieje w bazie danych"));
    }

    @Test
    public void testActivatePatientAccountAlreadyActive() {
        String email = "medinet@test2.pl";
        String activationCode = "verifyCode";
        given()
                .param("email", email)
                .param("activationCode", activationCode)
                .when()
                .post("http://localhost:" + port + basePath + API_PATIENT +
                        API_PATIENT_ACTIVE)
                .then()
                .statusCode(400)
                .body(equalTo("To konto jest już aktywne"));
        UserEntity userByEmail = userRepository.findByEmail(email);
        Assertions.assertTrue(userByEmail.getActive());

    }

    @Test
    public void testActivatePatientAccountNotRequiresActivation() {
        String email = "admin@admin.pl";
        String activationCode = "123456";
        given()
                .param("email", email)
                .param("activationCode", activationCode)
                .when()
                .post("http://localhost:" + port + basePath + API_PATIENT +
                        API_PATIENT_ACTIVE)
                .then()
                .statusCode(400)
                .body(equalTo("To konto nie wymaga aktywacji"));
    }

    @Test
    public void testActivatePatientInvalidActivationCode() {
        String email = "medinet@test3.pl";

        given()
                .param("email", email)
                .param("activationCode", "invalidCode")
                .when()
                .post("http://localhost:" + port + basePath + API_PATIENT +
                        API_PATIENT_ACTIVE)
                .then()
                .statusCode(400)
                .body(equalTo("Niepoprawny kod aktywacyjny"));

        UserEntity userByEmailAfter = userRepository.findByEmail(email);
        assertFalse(userByEmailAfter.getActive());
    }


    @Test
    public void testActivatePatientSuccess() {
        given()
                .param("email", "medinet@test.pl")
                .param("activationCode", "verifyCode")
                .when()
                .post("http://localhost:" + port + basePath + API_PATIENT +
                        API_PATIENT_ACTIVE)
                .then()
                .statusCode(200);
        UserEntity userByEmailAfter = userRepository.findByEmail("medinet@test.pl");
        Assertions.assertTrue(userByEmailAfter.getActive());
    }

    private static RegistrationFormDto getRegistrationFormDto() {
        return RegistrationFormDto
                .builder()
                .name("Test")
                .surname("Testowy")
                .email("medinet.rezerwacje@gmail.com")
                .password("test123A@")
                .city("Test")
                .street("Testowa 23")
                .postalCode("33-314")
                .phoneNumber("+48 511 111 111")
                .build();
    }
}
