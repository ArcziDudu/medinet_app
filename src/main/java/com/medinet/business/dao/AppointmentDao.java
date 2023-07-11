package com.medinet.business.dao;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.infrastructure.entity.AppointmentEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {
    void saveAppointment(AppointmentEntity appointment);

    void removeAppointment(Integer appointmentID);

    List<AppointmentDto> findAllByStatus(String status);

    Optional<AppointmentEntity> findById(Integer appointmentID);

    Optional<AppointmentEntity> findByDateOfAppointmentAndTimeOfVisit(LocalDate dateOfAppointment, String timeOfVisit);

}
