package com.medinet.infrastructure.repository;

import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.PatientDao;
import com.medinet.infrastructure.repository.jpa.PatientJpaRepository;
import com.medinet.infrastructure.repository.mapper.PatientMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class PatientRepository implements PatientDao {
    private final PatientJpaRepository patientJpaRepository;
    private final PatientMapper patientMapper;


    @Override
    public Optional<PatientDto> findById(Integer patientId) {
        return patientJpaRepository.findById(patientId).map(patientMapper::mapFromEntity);
    }

    @Override
    public List<PatientDto> findAll() {
        return patientJpaRepository.findAll()
                .stream()
                .map(patientMapper::mapFromEntity)
                .collect(Collectors.toList());
    }
}
