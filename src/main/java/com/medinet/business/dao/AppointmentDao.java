package com.medinet.business.dao;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.infrastructure.entity.AppointmentEntity;

import java.util.List;

public interface AppointmentDao {
    void saveAppointment(AppointmentEntity appointment);

    void removeAppointment(Integer appointmentID);

    List<AppointmentDto> findAllByStatus(String status);
}
