package com.medinet.infrastructure.repository.jpa;

import com.medinet.api.dto.CalendarDto;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarJpaRepository extends JpaRepository<CalendarEntity, Integer> {
    List<CalendarEntity> findAllByOrderByDate();

    Optional<CalendarEntity> findByDoctorAndDate(DoctorEntity doctor, LocalDate date);
}

