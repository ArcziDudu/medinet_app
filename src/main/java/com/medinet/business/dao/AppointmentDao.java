package com.medinet.business.dao;

import com.medinet.infrastructure.entity.AppointmentEntity;

public interface AppointmentDao {
    void saveAppointment(AppointmentEntity appointment);
}
