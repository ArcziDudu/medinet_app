package com.medinet.business.dao;

import com.medinet.api.dto.CalendarDto;
import com.medinet.infrastructure.entity.CalendarEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarDao {

    List<CalendarDto> findAllCalendar();


    Optional<CalendarEntity> findById(Integer calendarId);

    void save(CalendarEntity calendar);

}
