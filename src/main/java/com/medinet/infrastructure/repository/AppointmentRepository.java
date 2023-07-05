package com.medinet.infrastructure.repository;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.business.dao.AppointmentDao;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.repository.jpa.AppointmentJpaRepository;
import com.medinet.infrastructure.repository.mapper.AppointmentMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class AppointmentRepository implements AppointmentDao {
    private final AppointmentJpaRepository appointmentJpaRepository;
    private final AppointmentMapper appointmentMapper;
    @Override
    public void saveAppointment(AppointmentEntity appointment) {
        appointmentJpaRepository.save(appointment);
    }

    @Override
    public void removeAppointment(Integer appointmentID) {
        appointmentJpaRepository.deleteByAppointmentId(appointmentID);
    }

    @Override
    public List<AppointmentDto> findAllByStatus(String status) {
        return appointmentJpaRepository.findAllByStatus(status).stream()
                .map(appointmentMapper::mapFromEntity).collect(Collectors.toList());
    }
}
