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
    public static final String API_APPOINTMENT_CREATE = "/create";
    public static final String API_APPOINTMENT_FIND_BY_ID = "/{appointmentId}";
    public static final String API_APPOINTMENT_DELETE = "/{appointmentId}";
    public static final String API_APPOINTMENT_FIND_ALL_BY_STATUS= "/find/all/{status}";
    public static final String API_APPOINTMENT_FIND_ALL_BY_STATUS_AND_DOCTOR_ID = "/find/all/{status}/{doctorId}";
    public static final String API_APPOINTMENT_UPDATE_MESSAGE = "/{appointmentId}";

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final CalendarService calendarService;

    @PostMapping(value = API_APPOINTMENT_CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAppointment(@RequestBody RequestDto requestDto) {
        LocalTime visitTime = LocalTime.parse(requestDto.getTimeOfVisit());

        LocalTime minAllowedTime = LocalTime.of(8, 0);
        LocalTime maxAllowedTime = LocalTime.of(16, 0);
        LocalDate currentDate = LocalDate.now();
        LocalDate appointmentDate = requestDto.getDateOfAppointment();

        if (currentDate.plusWeeks(2).isBefore(appointmentDate)) {
            return ResponseEntity.badRequest()
                    .body("Nieprawidłowa data wizyty - nie możesz umówić się na termin późniejszy niż dwa tygodnie od dzisiaj!");
        }
        if (currentDate.plusDays(1).isAfter(appointmentDate)) {
            return ResponseEntity.badRequest()
                    .body("Nieprawidłowa data wizyty - nie możesz umówić się na termin wcześniejszy niż jutro!");
        }
        if (appointmentDate.getDayOfWeek() == DayOfWeek.SATURDAY || appointmentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return ResponseEntity.badRequest()
                    .body("Nieprawidłowa data wizyty - nie możesz umówić się na weekend!");
        }
        if (visitTime.isBefore(minAllowedTime) || visitTime.isAfter(maxAllowedTime)) {
            return ResponseEntity.badRequest()
                    .body("Nieprawidłowa godzina wizyty!");

        } else {

            AppointmentEntity existAppointment = appointmentService.findByDateOfAppointmentAndTimeOfVisit(
                    requestDto.getDateOfAppointment(),
                    requestDto.getTimeOfVisit());
            if (existAppointment != null) {
                return ResponseEntity.badRequest()
                        .body("Podany termin jest zarezerwowany!");
            }

            DoctorEntity doctorEntity = doctorMapper.mapFromDto(doctorService.findDoctorById(requestDto.getDoctorId()));
            CalendarEntity calendar = calendarService
                    .findByDoctorIdAndDateOfAppointment(doctorEntity, requestDto.getDateOfAppointment());

            AppointmentDto appointment = AppointmentDto.builder()
                    .dateOfAppointment(requestDto.getDateOfAppointment())
                    .timeOfVisit(requestDto.getTimeOfVisit())
                    .patient(patientMapper.mapFromDto(patientService.findByEmail(requestDto.getEmail())))
                    .doctor(doctorMapper.mapFromDto(doctorService.findDoctorById(requestDto.getDoctorId())))
                    .issueInvoice(OffsetDateTime.now())
                    .calendarId(calendar.getCalendarId())
                    .status("pending")
                    .UUID(appointmentService.getVisitNumber())
                    .build();

            appointmentService.processAppointment(appointmentMapper.mapFromDto(appointment));
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(value = API_APPOINTMENT_FIND_BY_ID)
    public ResponseEntity<AppointmentDto> AppointmentById(@PathVariable Integer appointmentId) {

        if (Objects.isNull(appointmentId)) {
            return ResponseEntity.notFound().build();
        }
        Optional<AppointmentEntity> appointment = appointmentService.findById(appointmentId);
        if (appointment.isEmpty()){
            throw new NotFoundException("Wizyta o id [%s] nie istniejte".formatted(appointment));
        }
      return ResponseEntity.ok(appointmentMapper.mapFromEntity(appointment.get()));
    }

    @GetMapping(value = API_APPOINTMENT_FIND_ALL_BY_STATUS)
    public ResponseEntity<?> AppointmentsByStatus(@PathVariable String status) {
        if (!status.equals("done") && !status.equals("pending") && !status.equals("upcoming")) {
           return ResponseEntity.badRequest().body("nieprawidłowy status wizyty");
        }

        return ResponseEntity
                .ok(appointmentService.findAllAppointmentsByStatus(status));
    }
    @GetMapping(value = API_APPOINTMENT_FIND_ALL_BY_STATUS_AND_DOCTOR_ID)
    public ResponseEntity<?> AppointmentsByStatusAndDoctorId(@PathVariable String status, @PathVariable Integer doctorId) {
        if (!status.equals("done") && !status.equals("pending") && !status.equals("upcoming")) {
            return ResponseEntity.badRequest().body("nieprawidłowy status wizyty");
        }

        return ResponseEntity
                .ok(appointmentService.findAllAppointmentsByStatusAndDoctorID(status, doctorId));
    }
    @PatchMapping(API_APPOINTMENT_UPDATE_MESSAGE)
    public ResponseEntity<?> updateAppointmentMessage(@PathVariable Integer appointmentId, @RequestBody String message) {
        Optional<AppointmentEntity> appointment = appointmentService.findById(appointmentId);
        if(appointment.isEmpty()){
            return ResponseEntity.badRequest()
                    .body("Wizyta o podanym id nie istnieje");
        } else if (!Objects.equals(appointment.get().getStatus(), "pending")) {
            return ResponseEntity.badRequest()
                    .body("Nie można modyfikować wizyty, która się nie zakoćzyła");
        }
        appointmentService.approveAppointment(appointmentId, message);
            return ResponseEntity.ok().build();
    }
    @DeleteMapping(API_APPOINTMENT_DELETE)
    public ResponseEntity<?> deleteAppointment(@PathVariable Integer appointmentId) {
        Optional<AppointmentEntity> appointment = appointmentService.findById(appointmentId);
        if(appointment.isEmpty()){
            return ResponseEntity.badRequest()
                    .body("Wizyta o podanym id nie istnieje");
        } else if (Objects.equals(appointment.get().getStatus(), "pending")) {
            return ResponseEntity.badRequest()
                    .body("Nie można anulować wizyty, która się odbyła");
        }
        else if (Objects.equals(appointment.get().getStatus(), "done")) {
            return ResponseEntity.badRequest()
                    .body("Nie można anulować wizyty, która się odbyła");
        }
        Integer calendarId = appointment.get().getCalendarId();
        String timeOfVisit = appointment.get().getTimeOfVisit();
        appointmentService.processRemovingAppointment(appointmentId, timeOfVisit, calendarId);
        return ResponseEntity.ok().build();
    }
}