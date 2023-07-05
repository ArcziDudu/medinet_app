package com.medinet.business.services;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.AppointmentDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentDao appointmentDao;
    private final DoctorService doctorService;
    private final CalendarService calendarService;
    private final AppointmentMapper appointmentMapper;

    public String getVisitNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }


    @Transactional
    public void processAppointment(AppointmentEntity appointment) {


        doctorService.findDoctorById(appointment.getDoctor().getDoctorId())
                .getCalendars()
                .forEach(calendar -> calendar.getHours()
                        .removeIf(value -> value.equals(appointment.getTimeOfVisit())
                                && calendar.getDate().equals(appointment.getDateOfAppointment())));
        appointment.setStatus("upcoming");

        appointmentDao.saveAppointment(appointment);
    }


    public OffsetDateTime issueInvoice() {
        return OffsetDateTime.now();
    }

    public List<AppointmentDto> findCompletedAppointments(PatientDto currentPatient) {
        LocalDateTime now = LocalDateTime.now();

       return currentPatient.getAppointments().stream()
                .filter(a -> {
                    LocalDate appointmentDate = a.getDateOfAppointment();
                    LocalTime appointmentTime = LocalTime.parse(a.getTimeOfVisit());
                    LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
                    return appointmentDateTime.isBefore(now);
                }).map(appointmentMapper::mapFromEntity).collect(Collectors.toList());

    }

    public List<AppointmentDto> findUpcomingAppointments(PatientDto currentPatient) {
       return currentPatient.getAppointments().stream().filter(a->a.getStatus().equals("upcoming"))
                .map(appointmentMapper::mapFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processRemovingAppointment(Integer appointmentID,
                                           String calendarHour,
                                           Integer calendarId) {
        Optional<CalendarEntity> calendar = calendarService.findById(calendarId);
        List<String> hours = calendar.get().getHours();
        hours.add(calendarHour);
        hours.sort(new Comparator<String>() {
            @Override
            public int compare(String hour1, String hour2) {
                return hour1.compareTo(hour2);
            }
        });


        appointmentDao.removeAppointment(appointmentID);
    }

    public List<AppointmentDto> findAllCompletedAppointments(String status) {
        return appointmentDao.findAllByStatus(status);
    }

    @Transactional
    public void approveAppointment(Integer appointmentID, String message) {
        Optional<AppointmentEntity> optionalAppointment = appointmentDao.findById(appointmentID);
        if (optionalAppointment.isPresent()) {
            AppointmentEntity appointment = optionalAppointment.get();
            appointment.setStatus("done");
            appointment.setNoteOfAppointment(message);
        } else {
            throw new NotFoundException("not found");
        }
    }

    public Optional<AppointmentEntity> findById(Integer appointmentId) {
        return appointmentDao.findById(appointmentId);
    }

    public void save(AppointmentEntity appointment) {
        appointmentDao.saveAppointment(appointment);
    }
}
