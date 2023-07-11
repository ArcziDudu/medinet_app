package com.medinet.api.controller.rest;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.RequestDto;
import com.medinet.business.services.AppointmentService;
import com.medinet.business.services.CalendarService;
import com.medinet.business.services.DoctorService;
import com.medinet.business.services.PatientService;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@RestController
@AllArgsConstructor
@RequestMapping(AppointmentRestController.API_APPOINTMENT)
public class AppointmentRestController {
    public static final String API_APPOINTMENT = "/api/appointment";
    public static final String API_APPOINTMENT_CREATE = "/create";

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
            if(existAppointment!= null){
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

            return ResponseEntity
                    .ok()
                    .build();
        }
    }
}
