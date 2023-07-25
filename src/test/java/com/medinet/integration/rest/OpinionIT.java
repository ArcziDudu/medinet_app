package com.medinet.integration.rest;

import com.medinet.api.dto.OpinionDto;
import com.medinet.business.services.OpinionService;
import com.medinet.infrastructure.repository.jpa.DoctorJpaRepository;
import com.medinet.infrastructure.repository.jpa.PatientJpaRepository;
import com.medinet.integration.configuration.RestAssuredIntegrationTestBase;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.medinet.api.controller.rest.OpinionRestController.*;
import static com.medinet.util.EntityFixtures.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class OpinionIT
        extends RestAssuredIntegrationTestBase{
    @Autowired
    private OpinionService opinionService;

    @BeforeAll
    void setUp() {
        opinionService.processOpinion(opinion());
        opinionService.processOpinion(opinion2());
        opinionService.processOpinion(opinion3());
    }

    @Test
    public void testAllOpinions() {
        given()
                .spec(requestSpecification())
                .when()
                .get("http://localhost:" + port + basePath + API_OPINION
                        + API_OPINION_ALL)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(4))
                .body("[0].opinionId", equalTo(opinion().getOpinionId()))
                .body("[1].opinionId", equalTo(opinion2().getOpinionId()))
                .body("[2].opinionId", equalTo(opinion3().getOpinionId()));
    }
    @Test
    public void testCreateOpinion(){
        List<OpinionDto> all = opinionService.findAll();
        assertEquals(3, all.size());
        String opinion = "this doctor is amazing!";
        given()
                .spec(requestSpecification())
                .when()
                .pathParam("patientId", 1)
                .pathParam("doctorId", 2)
                .body(opinion)
                .post("http://localhost:" + port + basePath + API_OPINION
                        + API_OPINION_CREATE)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
        List<OpinionDto> allAfter = opinionService.findAll();
        assertEquals(4, allAfter.size());
    }
    @Test
    public void testCreateOpinionWithIncorrectIds(){
        List<OpinionDto> all = opinionService.findAll();
        assertEquals(4, all.size());
        String opinion = "this doctor is amazing!";
        given()
                .spec(requestSpecification())
                .when()
                .pathParam("patientId", 100)
                .pathParam("doctorId", 200)
                .body(opinion)
                .post("http://localhost:" + port + basePath + API_OPINION
                        + API_OPINION_CREATE)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON);
        List<OpinionDto> allAfter = opinionService.findAll();
        assertEquals(4, allAfter.size());
    }
    @Test
    public void testGetOpinionByPatientId(){
        given()
                .spec(requestSpecification())
                .when()
                .pathParam("patientId", 1)
                .get("http://localhost:" + port + basePath + API_OPINION
                        + API_OPINION_BY_PATIENT)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(4));
    }
    @Test
    public void testGetOpinionByDoctorId(){
        given()
                .spec(requestSpecification())
                .when()
                .pathParam("doctorId", 2)
                .get("http://localhost:" + port + basePath + API_OPINION
                        + API_OPINION_BY_DOCTOR)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(4));
    }
}
