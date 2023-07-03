package com.medinet.infrastructure.repository;

import com.medinet.business.dao.AppointmentDao;
import com.medinet.infrastructure.repository.jpa.AppointmentJpaRepository;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AppointmentRepository implements AppointmentDao {
    private final AppointmentJpaRepository appointmentJpaRepository;
    private final AppointmentMapper appointmentMapper;
}
