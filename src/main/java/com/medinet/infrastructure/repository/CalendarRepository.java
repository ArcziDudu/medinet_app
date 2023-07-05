package com.medinet.infrastructure.repository;

import com.medinet.api.dto.CalendarDto;
import com.medinet.business.dao.CalendarDao;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.repository.jpa.CalendarJpaRepository;
import com.medinet.infrastructure.repository.mapper.CalendarMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CalendarRepository implements CalendarDao {
    private CalendarJpaRepository calendarJpaRepository;
    private CalendarMapper calendarMapper;
    @Override
    public List<CalendarDto> findAllCalendar() {
        return calendarJpaRepository.findAllByOrderByDate().stream()
                .map(calendarMapper::mapFromEntity).collect(Collectors.toList());
    }

    @Override
    public Optional<CalendarEntity> findById(Integer calendarId) {
        return calendarJpaRepository.findById(calendarId);
    }


}
