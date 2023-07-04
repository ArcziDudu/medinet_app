package com.medinet.business.services;

import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.PatientDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.OpinionEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PatientService {
    private final PatientDao patientDao;

    public void sendOpinion(OpinionEntity opinion) {

    }

    public PatientDto findById(Integer patientId) {
        Optional<PatientDto> patientById = patientDao.findById(patientId);
        if(patientById.isEmpty()){
            throw new NotFoundException("Could not find patient by id: [%s]".formatted(patientId));
        }
        return patientById.get();
    }

    public List<PatientDto> findAllPatients() {
        List<PatientDto> patients = patientDao.findAll();
        log.info("Available patients: [{}]", patients.size());
        return patients;
    }
}
