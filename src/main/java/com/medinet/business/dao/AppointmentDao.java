package com.medinet.business.dao;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.infrastructure.entity.AppointmentEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {
    void saveAppointment(AppointmentEntity appointment);

    void removeAppointment(Integer appointmentID);

    List<AppointmentDto> findAllByStatus(String status);

    Optional<AppointmentDto> findById(Integer appointmentID);

    List<AppointmentDto> findAll();

    boolean existByDateAndTimeOfVisit(LocalDate dateOfAppointment, LocalTime timeOfVisit);

}
