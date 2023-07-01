package com.medinet.business.dao;

import com.medinet.api.dto.CalendarDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CalendarDao {

    List<CalendarDto> findAllCalendar();
}
