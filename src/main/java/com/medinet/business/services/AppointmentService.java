package com.medinet.business.services;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.dao.AppointmentDao;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentDao appointmentDao;
    private final DoctorService doctorService;

    public String getVisitNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }


    @Transactional
    public void processAppointment(AppointmentEntity appointment) {


        doctorService.findDoctorById(appointment.getDoctor().getDoctorId())
                .getCalendars()
                .forEach(calendar -> calendar.getHours()
                        .removeIf(value -> value.equals(appointment.getTimeOfVisit())));

        appointmentDao.saveAppointment(appointment);
    }


    public OffsetDateTime issueInvoice() {
        return OffsetDateTime.now();
    }
}
