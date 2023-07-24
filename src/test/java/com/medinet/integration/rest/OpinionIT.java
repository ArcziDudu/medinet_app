package com.medinet.integration.rest;

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
                .body("size()", is(3))
                .body("[0].opinionId", equalTo(opinion().getOpinionId()))
                .body("[1].opinionId", equalTo(opinion2().getOpinionId()))
                .body("[2].opinionId", equalTo(opinion3().getOpinionId()));
    }
}
