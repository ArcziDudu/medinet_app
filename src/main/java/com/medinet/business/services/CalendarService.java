package com.medinet.business.services;

import com.medinet.api.dto.CalendarDto;
import com.medinet.business.dao.CalendarDao;
import com.medinet.infrastructure.entity.CalendarEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarService {
    private final CalendarDao calendarDao;

    public List<CalendarDto> findAllCalendar() {
        List<CalendarDto> calendars = calendarDao.findAllCalendar();
        log.info("Available calendars: [{}]", calendars.size());
        return calendars;
    }


    public Optional<CalendarEntity> findById(Integer calendarId) {
        Optional<CalendarEntity> calendar = calendarDao.findById(calendarId);
        if(calendar.isEmpty()){
                log.error("Problem with calendar service. Calendar with id [%s] not found!"
                        .formatted(calendarId));
        }
        return calendar;
    }
}
