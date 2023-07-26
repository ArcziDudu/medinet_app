package com.medinet.integration.rest;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.services.DoctorService;
import com.medinet.integration.configuration.RestAssuredIntegrationTestBase;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.medinet.api.controller.rest.DoctorRestController.*;
import static com.medinet.util.EntityFixtures.someDoctorDto;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class DoctorIT
        extends RestAssuredIntegrationTestBase {

    @Autowired
    private DoctorService doctorService;

    @Test
    @Transactional
    public void testCreateDoctor() {
        List<DoctorDto> allDoctors = doctorService.findAllDoctors();
        assertEquals(98, allDoctors.size());
        DoctorDto doctorDto = someDoctorDto();
        given().spec(requestSpecification())
                .body(doctorDto)
                .when()
                .post("http://localhost:" + port + basePath + API_DOCTOR
                        + API_CREATE_DOCTOR)
                .then()
                .statusCode(201);
        List<DoctorDto> allDoctorsAfter = doctorService.findAllDoctors();
        assertEquals(99, allDoctorsAfter.size());
        DoctorDto doctorById = doctorService.findDoctorById(100);
        assertEquals(doctorDto.getName(), doctorById.getName());
        assertEquals(doctorDto.getEmail(), doctorById.getEmail());
        doctorService.deleteById(100);
    }

    @Test
    public void testGetDoctorById() {
        DoctorDto doctorById = doctorService.findDoctorById(1);
        given()
                .spec(requestSpecification())
                .pathParam("doctorId", 1)
                .when()
                .get("http://localhost:" + port + basePath + API_DOCTOR
                        + API_ONE_DOCTOR)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("doctorId", equalTo(doctorById.getDoctorId()))
                .body("name", equalTo(doctorById.getName()));
    }

    @Test
    public void testGetDoctorByNonExistingId() {
        int doctorId = 999;
        given()
                .spec(requestSpecification())
                .pathParam("doctorId", doctorId)
                .when()
                .get("http://localhost:" + port + basePath + API_DOCTOR
                        + API_ONE_DOCTOR)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllDoctors() {
        List<DoctorDto> allDoctors = doctorService.findAllDoctors();
        assertEquals(99, allDoctors.size());
        given()
                .spec(requestSpecification())
                .when()
                .get("http://localhost:" + port + basePath + API_DOCTOR
                        + API_ALL_DOCTOR)
                .then()
                .statusCode(200)
                .body("size()", is(99));
    }

    @Test
    public void testGetAllDoctorsByPage() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("page", 1)
                .when()
                .get("http://localhost:" + port + basePath + API_DOCTOR
                        + API_ALL_DOCTOR_PAGE)
                .then()
                .statusCode(200)
                .body("pageable.pageNumber", is(1));
    }

    @Test
    void deleteDoctorTest() {
        List<DoctorDto> allDoctors = doctorService.findAllDoctors();
        assertEquals(99, allDoctors.size());
        given()
                .contentType(ContentType.JSON)
                .pathParam("doctorId", 1)
                .when()
                .delete("http://localhost:" + port + basePath + API_DOCTOR
                        + API_ONE_DOCTOR)
                .then()
                .statusCode(200);
        List<DoctorDto> allDoctorsAfter = doctorService.findAllDoctors();
        assertEquals(98, allDoctorsAfter.size());
    }
}
