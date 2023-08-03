package com.medinet.business.services;

import com.medinet.api.dto.CalendarDto;
import com.medinet.business.dao.CalendarDao;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarService {
    private final CalendarDao calendarDao;

    public List<CalendarDto> findAllCalendar() {
        return calendarDao.findAllCalendar();
    }

    @Transactional
    public Optional<CalendarEntity> findById(Integer calendarId) {
        Optional<CalendarEntity> calendar = calendarDao.findById(calendarId);
        if (calendar.isEmpty()) {
            log.error("Problem with calendar service. Calendar with id [%s] not found!"
                    .formatted(calendarId));
        }
        return calendar;
    }


    public void save(CalendarEntity calendar) {
        calendarDao.save(calendar);
    }

    @Transactional
    public CalendarEntity findByDoctorIdAndDateOfAppointment(DoctorEntity doctor, LocalDate dateOfAppointment) {
        Optional<CalendarEntity> calendar = calendarDao
                .findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);
        if (calendar.isEmpty()) {
            log.error("Problem with calendar service. Calendar  not found!");
        }
        return calendar.get();

    }
}
