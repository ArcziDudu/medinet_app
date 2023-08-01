package com.medinet.business.services;

import com.medinet.infrastructure.entity.AppointmentEntity;
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


    //zasada działania systemu - okno wizytowe trwa godzine, wiec program co godzinę sprawdza czy wizyta się odbyła,
    //jeśli tak to przekazywana jest lekarzowi, który ma 24h
    // na dodanie  notatki z wizyty, jeśli tego nie zrobi, "wizyta" jest oznaczana jako zakończona
    @Scheduled(fixedRate = 20000)
    @Transactional
    public void myMethod() {
        List<AppointmentEntity> upcoming = appointmentService
                .findAllAppointmentsByStatus("upcoming")
                .stream().map(appointmentMapper::mapFromDto).toList();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        for (AppointmentEntity upcomingAppointment : upcoming) {
            if (upcomingAppointment.getDateOfAppointment().equals(today)
                    && now.isAfter(upcomingAppointment.getTimeOfVisit())) {

                AppointmentEntity appointment = appointmentService.findById(upcomingAppointment.getAppointmentId());
                appointment.setStatus("pending");
                appointmentService.save(appointment);

            }
        }
    }
}

