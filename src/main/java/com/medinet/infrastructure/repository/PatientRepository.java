package com.medinet.infrastructure.repository;

import com.medinet.infrastructure.repository.jpa.PatientJpaRepository;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class PatientRepository {
    private final PatientJpaRepository patientJpaRepository;
    private final PatientMapper patientMapper;
}
