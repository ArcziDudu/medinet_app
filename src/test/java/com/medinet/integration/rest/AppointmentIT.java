package com.medinet.integration.rest;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.RequestDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.integration.configuration.RestAssuredIntegrationTestBase;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Stream;

import static com.medinet.api.controller.rest.AppointmentRestController.*;
import static com.medinet.util.EntityFixtures.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppointmentIT
        extends RestAssuredIntegrationTestBase {
    @Autowired
    private AppointmentService appointmentService;

    @BeforeAll
    void setup() {
        appointmentService.save(appointment());
        appointmentService.save(appointment2());
        appointmentService.save(appointment3());
    }

    @Test
    @Transactional
    public void testCreateAppointment() {
        List<AppointmentDto> all = appointmentService.findAll();
        assertEquals(2, all.size());
        given().spec(requestSpecification())
                .body(requestDto())
                .when()
                .post("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_NEW)
                .then()
                .statusCode(200);
        List<AppointmentDto> allAfter = appointmentService.findAll();
        assertEquals(3, allAfter.size());

    }

    @Test
    public void testCreateAppointmentWith2Weeks() {
        RequestDto requestDto = requestDto();
        requestDto.setDateOfAppointment(LocalDate.now().plusWeeks(3));
        List<AppointmentDto> all = appointmentService.findAll();
        assertEquals(3, all.size());
        given().spec(requestSpecification())
                .body(requestDto)
                .when()
                .post("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_NEW)
                .then()
                .statusCode(400)
                .body(is("Invalid appointment date - you cannot schedule an appointment more than two weeks from today!"));
        List<AppointmentDto> allAfter = appointmentService.findAll();
        assertEquals(3, allAfter.size());
    }

    @Test
    public void testCreateAppointmentEarlierThanTomorrow() {
        RequestDto requestDto = requestDto();
        requestDto.setDateOfAppointment(LocalDate.now());
        List<AppointmentDto> all = appointmentService.findAll();
        assertEquals(3, all.size());
        given().spec(requestSpecification())
                .body(requestDto)
                .when()
                .post("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_NEW)
                .then()
                .statusCode(400)
                .body(is("Invalid appointment date - you cannot schedule an appointment earlier than tomorrow!"));
        ;
        List<AppointmentDto> allAfter = appointmentService.findAll();
        assertEquals(3, allAfter.size());

    }

    @ParameterizedTest
    @MethodSource("provideWeekendDays")
    public void testCreateAppointmentOnWeekend(DayOfWeek day) {
        RequestDto requestDto = requestDto();
        requestDto
                .setDateOfAppointment(LocalDate.now().with(TemporalAdjusters.next(day)));
        List<AppointmentDto> all = appointmentService.findAll();
        assertEquals(3, all.size());
        given().spec(requestSpecification())
                .body(requestDto)
                .when()
                .post("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_NEW)
                .then()
                .statusCode(400)
                .body(is("Invalid appointment date - you cannot schedule an appointment on the weekend!"));
        ;
        List<AppointmentDto> allAfter = appointmentService.findAll();
        assertEquals(3, allAfter.size());

    }

    private static Stream<DayOfWeek> provideWeekendDays() {
        return Stream.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"done", "pending"})
    public void testGetExistingAppointmentByStatusParameterized(String status) {
        given()
                .spec(requestSpecification())
                .pathParam("status", status)
                .when()
                .get("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_FIND_ALL_BY_STATUS)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1));
    }


    @Test
    public void testGetExistingAppointmentByCorrectStatus() {
        given()
                .spec(requestSpecification())
                .pathParam("status", "done")
                .when()
                .get("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_FIND_ALL_BY_STATUS)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].appointmentId", equalTo(appointment().getAppointmentId()))
                .body("[0].status", equalTo(appointment().getStatus()));
    }

    @Test
    public void testGetAppointmentsByInvalidStatus() {
        given()
                .spec(requestSpecification())
                .pathParam("status", "invalid_status")
                .when()
                .get("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_FIND_ALL_BY_STATUS)
                .then()
                .statusCode(400)
                .body(is("Invalid appointment status"));
    }

    @Test
    public void testGetAppointmentsById() {
        given()
                .spec(requestSpecification())
                .pathParam("appointmentId", 2)
                .when()
                .get("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_FIND_BY_ID)
                .then()
                .statusCode(200)
                .body("appointmentId", equalTo(appointment2().getAppointmentId()))
                .body("status", equalTo(appointment2().getStatus()));
    }

    @Test
    public void testGetAppointmentsByInvalidId() {

        given()
                .spec(requestSpecification())
                .pathParam("appointmentId", 999)
                .when()
                .get("http://localhost:" + port + basePath + API_APPOINTMENT
                        + API_APPOINTMENT_FIND_BY_ID)
                .then()
                .statusCode(404)
                .body(is("Could not find appointment by Id: [999]"));
    }

    @Test
    @Transactional
    public void testDeleteAppointment() {
        List<AppointmentDto> list = appointmentService.findAll();
        assertEquals(3, list.size());
        given()
                .spec(requestSpecification())
                .pathParam("appointmentId", 2)
                .delete("http://localhost:" + port + basePath + API_APPOINTMENT + API_APPOINTMENT_DELETE)
                .then()
                .statusCode(200);
        List<AppointmentDto> listAfter = appointmentService.findAll();
        assertEquals(2, listAfter.size());
    }

    @Test
    @Transactional
    public void testDeleteAppointmentDoneStatus() {
        List<AppointmentDto> list = appointmentService.findAll();
        assertEquals(3, list.size());
        given()
                .spec(requestSpecification())
                .pathParam("appointmentId", 1)
                .delete("http://localhost:" + port + basePath + API_APPOINTMENT + API_APPOINTMENT_DELETE)
                .then()
                .statusCode(400)
                .body(is("Cannot cancel an appointment that has already taken place"));
        assertEquals(3, list.size());
    }

    @Test
    @Transactional
    public void testDeleteAppointmentPendingStatus() {
        List<AppointmentDto> list = appointmentService.findAll();
        assertEquals(3, list.size());
        given()
                .spec(requestSpecification())
                .pathParam("appointmentId", 3)
                .delete("http://localhost:" + port + basePath + API_APPOINTMENT + API_APPOINTMENT_DELETE)
                .then()
                .statusCode(400)
                .body(is("Cannot cancel an appointment that has already taken place"));
        List<AppointmentDto> listAfter = appointmentService.findAll();
        assertEquals(3, listAfter.size());
    }


}
