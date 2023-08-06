package com.medinet.business.services;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ScheduledTasksService {
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @Scheduled(fixedRate = 20000)
    @Transactional
    public void statusConversionIfTheVisitTookPlace() {
        List<AppointmentDto> upcoming = appointmentService
                .findAllAppointmentsByStatus("upcoming");
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        for (AppointmentDto upcomingAppointment : upcoming) {
            if (upcomingAppointment.getDateOfAppointment().equals(today)
                    && now.isAfter(upcomingAppointment.getTimeOfVisit())) {

                AppointmentDto appointment = appointmentService.findById(upcomingAppointment.getAppointmentId());
                appointment.setStatus("pending");
                appointmentService.save(appointmentMapper.mapFromDto(appointment));

            }
        }
    }
}

