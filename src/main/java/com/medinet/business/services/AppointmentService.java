package com.medinet.business.services;

import com.medinet.business.dao.AppointmentDao;
import com.medinet.infrastructure.entity.AppointmentEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentDao appointmentDao;

    public OffsetDateTime getIssueDate() {
        return OffsetDateTime.now();
    }

    public String getVisitNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public OffsetDateTime convertStringToOffsetDateTime(String selectedHour) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return OffsetDateTime.parse(selectedHour, formatter);
    }

    @Transactional
    public void processAppointment(AppointmentEntity appointment) {
        appointmentDao.saveAppointment(appointment);
    }


    public OffsetDateTime issueInvoice() {
        return OffsetDateTime.now();
    }
}
