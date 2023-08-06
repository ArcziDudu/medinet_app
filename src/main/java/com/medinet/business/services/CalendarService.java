package com.medinet.business.services;

import com.medinet.api.dto.CalendarDto;
import com.medinet.business.dao.CalendarDao;
import com.medinet.domain.exception.NotFoundException;
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
    public CalendarDto findById(Integer calendarId) {
        Optional<CalendarDto> calendar = calendarDao.findById(calendarId);
        if (calendar.isEmpty()) {
            throw new NotFoundException("Could not find calendar by id: [%s]".formatted(calendarId));
        }
        return calendar.get();
    }


    public void save(CalendarEntity calendar) {
        calendarDao.save(calendar);
    }

    @Transactional
    public CalendarDto findByDoctorIdAndDateOfAppointment(DoctorEntity doctor, LocalDate dateOfAppointment) {
        Optional<CalendarDto> calendar = calendarDao
                .findByDoctorIdAndDateOfAppointment(doctor, dateOfAppointment);
        if (calendar.isEmpty()) {
            throw new NotFoundException("Could not find calendar by doctorId and dateOfAppointment: [%s] [%s]"
                    .formatted(doctor.getDoctorId(), dateOfAppointment));
        }
        return calendar.get();

    }
}
