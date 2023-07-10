package com.medinet.business.dao;

import com.medinet.api.dto.PatientDto;
import com.medinet.infrastructure.entity.PatientEntity;

import java.util.List;
import java.util.Optional;

public interface PatientDao {
    Optional<PatientDto> findById(Integer patientId);

    List<PatientDto> findAll();

    void save(PatientEntity newPatient);
}
