package com.medinet.business.dao;

import com.medinet.api.dto.PatientDto;

import java.util.Optional;

public interface PatientDao {
    Optional<PatientDto> findById(Integer patientId);

}
