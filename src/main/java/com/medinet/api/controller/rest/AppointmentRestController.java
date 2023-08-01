package com.medinet.api.controller.rest;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.RequestDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.CalendarService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(AppointmentRestController.API_APPOINTMENT)
public class AppointmentRestController {
    public static final String API_APPOINTMENT = "/api/appointment";
    public static final String API_APPOINTMENT_NEW = "/new";
    public static final String API_APPOINTMENT_FIND_BY_ID = "/find/{appointmentId}";
    public static final String API_APPOINTMENT_DELETE = "/{appointmentId}";
    public static final String API_APPOINTMENT_FIND_ALL_BY_STATUS = "/all/{status}";
    public static final String API_APPOINTMENT_FIND_ALL_BY_STATUS_AND_DOCTOR_ID = "/all/{status}/{doctorId}";
    public static final String API_APPOINTMENT_UPDATE_MESSAGE = "/{appointmentId}";

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final CalendarService calendarService;


    @PostMapping(value = API_APPOINTMENT_NEW, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create an appointment",
            description = "Create an appointment based on the provided details")
    public ResponseEntity<?> createAppointment(@RequestBody
                                               @Schema(description = "Appointment request")
                                               RequestDto requestDto) {
        LocalTime visitTime = requestDto.getTimeOfVisit();

        LocalTime minAllowedTime = LocalTime.of(8, 0);
        LocalTime maxAllowedTime = LocalTime.of(16, 0);
        LocalDate currentDate = LocalDate.now();
        LocalDate appointmentDate = requestDto.getDateOfAppointment();
        AppointmentEntity existAppointment = appointmentService.findByDateOfAppointmentAndTimeOfVisit(
                requestDto.getDateOfAppointment(),
                requestDto.getTimeOfVisit());
        if (currentDate.plusWeeks(2).isBefore(appointmentDate)) {
            return ResponseEntity.badRequest()
                    .body("Invalid appointment date - you cannot schedule an appointment more than two weeks from today!");
        }
        if (currentDate.plusDays(1).isAfter(appointmentDate)) {
            return ResponseEntity.badRequest()
                    .body("Invalid appointment date - you cannot schedule an appointment earlier than tomorrow!");
        }
        if (appointmentDate.getDayOfWeek() == DayOfWeek.SATURDAY || appointmentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return ResponseEntity.badRequest()
                    .body("Invalid appointment date - you cannot schedule an appointment on the weekend!");
        }
        if (visitTime.isBefore(minAllowedTime) || visitTime.isAfter(maxAllowedTime)) {
            return ResponseEntity.badRequest()
                    .body("Invalid appointment time!");
        }
        if (existAppointment != null) {
            return ResponseEntity.badRequest()
                    .body("The selected time slot is already booked!");
        } else {

            DoctorEntity doctorEntity = doctorMapper.mapFromDto(doctorService.findDoctorById(requestDto.getDoctorId()));
            CalendarEntity calendar = calendarService
                    .findByDoctorIdAndDateOfAppointment(doctorEntity, requestDto.getDateOfAppointment());

            AppointmentDto appointment = createAppointmentDto(requestDto, calendar);

            appointmentService.processAppointment(appointmentMapper.mapFromDto(appointment));
            return ResponseEntity.ok().build();
        }
    }

    private AppointmentDto createAppointmentDto(RequestDto requestDto, CalendarEntity calendar) {
        return AppointmentDto.builder()
                .dateOfAppointment(requestDto.getDateOfAppointment())
                .timeOfVisit(requestDto.getTimeOfVisit())
                .patient(patientMapper.mapFromDto(patientService.findByEmail(requestDto.getEmail())))
                .doctor(doctorMapper.mapFromDto(doctorService.findDoctorById(requestDto.getDoctorId())))
                .issueInvoice(OffsetDateTime.now())
                .calendarId(calendar.getCalendarId())
                .status("pending")
                .UUID(appointmentService.getVisitNumber())
                .build();
    }

    @GetMapping(value = API_APPOINTMENT_FIND_BY_ID)
    @Operation(summary = "Get appointment by ID",
            description = "Retrieve information about an appointment based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentDto> AppointmentById(@PathVariable Integer appointmentId) {

        if (Objects.isNull(appointmentId)) {
            return ResponseEntity.notFound().build();
        }
        AppointmentEntity appointment = appointmentService.findById(appointmentId);

        return ResponseEntity.ok(appointmentMapper.mapFromEntity(appointment));
    }


    @GetMapping(value = API_APPOINTMENT_FIND_ALL_BY_STATUS)
    @Operation(summary = "Get appointments by status",
            description = "Retrieve appointments based on the specified status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments found"),
            @ApiResponse(responseCode = "400", description = "Invalid status provided")
    })
    public ResponseEntity<?> AppointmentsByStatus(@PathVariable String status) {
        if (!status.equals("done") && !status.equals("pending") && !status.equals("upcoming")) {
            return ResponseEntity.badRequest().body("Invalid appointment status");
        }

        return ResponseEntity.ok(appointmentService.findAllAppointmentsByStatus(status));
    }


    @GetMapping(value = API_APPOINTMENT_FIND_ALL_BY_STATUS_AND_DOCTOR_ID)
    @Operation(summary = "Get appointments by status and doctor ID",
            description = "Retrieve appointments based on the specified status and doctor ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments found"),
            @ApiResponse(responseCode = "400", description = "Invalid status provided")
    })
    public ResponseEntity<?> AppointmentsByStatusAndDoctorId(
            @PathVariable @Parameter(description = "Appointment status") String status,
            @PathVariable @Parameter(description = "Doctor ID") Integer doctorId
    ) {
        if (!status.equals("done") && !status.equals("pending") && !status.equals("upcoming")) {
            return ResponseEntity.badRequest().body("Invalid appointment status");
        }

        return ResponseEntity.ok(appointmentService.findAllAppointmentsByStatusAndDoctorID(status, doctorId));
    }

    @PatchMapping(API_APPOINTMENT_UPDATE_MESSAGE)
    @Operation(summary = "Update appointment message", description = "Update the message for an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment message updated"),
            @ApiResponse(responseCode = "400", description = "Invalid appointment ID or message provided")
    })
    public ResponseEntity<?> updateAppointmentMessage(
            @PathVariable @Parameter(description = "Appointment ID") Integer appointmentId,
            @RequestBody @Schema(description = "New appointment message") String message
    ) {
      AppointmentEntity appointmentCheck = appointmentService.findById(appointmentId);
        if (appointmentCheck.getStatus().equals("done")) {
            return ResponseEntity.badRequest()
                    .body("You can not modify appointment with status done!");
        }
        if (appointmentCheck.getStatus().equals("upcoming")) {
            return ResponseEntity.badRequest()
                    .body("You can not modify appointment with status upcoming!");
        }
        try {
            AppointmentEntity appointment = appointmentService.findById(appointmentId);
        } catch (NotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        appointmentService.approveAppointment(appointmentId, message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(API_APPOINTMENT_DELETE)
    @Operation(summary = "Delete appointment", description = "Delete an appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid appointment ID or cannot delete the appointment")
    })
    public ResponseEntity<?> deleteAppointment(
            @PathVariable @Parameter(description = "Appointment ID") Integer appointmentId
    ) {
       AppointmentEntity appointment = appointmentService.findById(appointmentId);
        if (Objects.isNull(appointment)) {
            return ResponseEntity.badRequest().body("Appointment with the provided ID does not exist");
        } else if (Objects.equals(appointment.getStatus(), "pending")) {
            return ResponseEntity.badRequest().body("Cannot cancel an appointment that has already taken place");
        } else if (Objects.equals(appointment.getStatus(), "done")) {
            return ResponseEntity.badRequest().body("Cannot cancel an appointment that has already taken place");
        }
        Integer calendarId = appointment.getCalendarId();
        LocalTime timeOfVisit = appointment.getTimeOfVisit();
        appointmentService.processRemovingAppointment(appointmentId, timeOfVisit, calendarId);
        return ResponseEntity.ok().build();
    }
}