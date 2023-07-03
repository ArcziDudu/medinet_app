package com.medinet.infrastructure.repository.jpa;

import com.medinet.api.dto.CalendarDto;
import com.medinet.infrastructure.entity.CalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CalendarJpaRepository extends JpaRepository<CalendarEntity, Integer> {
    List<CalendarEntity> findAllByOrderByDate();


}

