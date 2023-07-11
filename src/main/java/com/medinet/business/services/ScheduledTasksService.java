package com.medinet.business.services;

import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ScheduledTasksService {
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;


    //zasada działania systemu - okno wizytowe trwa godzine, wiec program co pół godziny sprawdza czy wizyta się odbyła,
    //jeśli tak to przekazywana jest lekarzowi, który ma 24h
    // na dodanie  notatki z wizyty, jeśli tego nie zrobi, "wizyta" jest oznaczana jako zakończona
    @Scheduled(fixedRate = 20000)
    public void myMethod() {
        List<AppointmentEntity> upcoming = appointmentService
                .findAllAppointmentsByStatus("upcoming")
                .stream().map(appointmentMapper::mapFromDto).toList();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (AppointmentEntity upcomingAppointment : upcoming) {
            if (upcomingAppointment.getDateOfAppointment().equals(today)
                    && now.isAfter(LocalTime.parse(upcomingAppointment.getTimeOfVisit(), formatter))) {

                AppointmentEntity appointment = appointmentService.findById(upcomingAppointment.getAppointmentId())
                        .orElseThrow(() -> {
                            log.error("Problem with appointment service. Appointment with id [%s] not found!"
                                    .formatted(upcomingAppointment.getAppointmentId()));
                            return new RuntimeException("Problem with appointment service. Check logs!");
                        });
                appointment.setStatus("pending");
                appointmentService.save(appointment);

            }
        }
    }
}

