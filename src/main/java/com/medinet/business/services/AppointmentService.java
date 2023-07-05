package com.medinet.business.services;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.AppointmentDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
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

        appointmentDao.saveAppointment(appointment);
    }


    public OffsetDateTime issueInvoice() {
        return OffsetDateTime.now();
    }

    public List<AppointmentEntity> findCompletedAppointments(PatientDto currentPatient) {
        LocalDateTime now = LocalDateTime.now();

        List<AppointmentEntity> completedAppointments = currentPatient.getAppointments().stream()
                .filter(a -> {
                    LocalDate appointmentDate = a.getDateOfAppointment();
                    LocalTime appointmentTime = LocalTime.parse(a.getTimeOfVisit());
                    LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
                    return appointmentDateTime.isBefore(now);
                })
                .collect(Collectors.toList());

        return completedAppointments;
    }

    public List<AppointmentEntity> findUpcomingAppointments(PatientDto currentPatient) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        return currentPatient.getAppointments().stream()
                .filter(a -> {
                    LocalDate appointmentDate = a.getDateOfAppointment();
                    LocalTime appointmentTime = LocalTime.parse(a.getTimeOfVisit());
                    LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
                    return (appointmentDate.isEqual(today) && appointmentDateTime.isAfter(now)) || appointmentDate.isAfter(today);
                })
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
    public void approveAppointment(Integer appointmentID) {
        Optional<AppointmentEntity> optionalAppointment = appointmentDao.findById(appointmentID);
        if (optionalAppointment.isPresent()) {
            AppointmentEntity appointment = optionalAppointment.get();
            appointment.setStatus("done");
        } else {
            throw new NotFoundException("not foung");
        }
    }
}
