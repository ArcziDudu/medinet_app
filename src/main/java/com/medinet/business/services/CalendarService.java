package com.medinet.business.services;

import com.medinet.api.dto.CalendarDto;
import com.medinet.business.dao.CalendarDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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


}
