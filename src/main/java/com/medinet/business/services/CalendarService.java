package com.medinet.business.services;

import com.medinet.api.dto.CalendarDto;
import com.medinet.api.dto.DoctorDto;
import com.medinet.business.dao.CalendarDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarService {
    private final CalendarDao calendarDao;

    public List<CalendarDto> findAllCalendar() {
        List<CalendarDto> calendars = calendarDao.findAllCalendar();
       log.info(String.valueOf(calendars.get(0).getDate().getDayOfWeek()));
        calendars.removeIf(a -> a.getDate().getDayOfWeek().equals(DayOfWeek.SUNDAY));
        log.info("Available calendars: [{}]", calendars.size());
        return calendars;
    }

}
