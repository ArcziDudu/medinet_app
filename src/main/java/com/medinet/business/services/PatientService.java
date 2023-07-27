package com.medinet.business.services;

import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.PatientDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.PatientEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PatientService {
    private final PatientDao patientDao;

    public PatientDto findById(Integer patientId) {
        Optional<PatientDto> patientById = patientDao.findByUserId(patientId);
        if (patientById.isEmpty()) {
            throw new NotFoundException("Could not find patient by id: [%s]".formatted(patientId));
        }
        return patientById.get();
    }


    public void createNewPatient(PatientEntity newPatient) {
        patientDao.save(newPatient);
    }

    public boolean findByPhoneNumber(String phoneNumber) {
        return patientDao.existsByPhoneNumber(phoneNumber);
    }

    public PatientDto findByUserId(int id) {
        Optional<PatientDto> patientById = patientDao.findByUserId(id);
        if (patientById.isEmpty()) {
            throw new NotFoundException("Could not find patient by id: [%s]".formatted(id));
        }
        return patientById.get();
    }

    public PatientDto findByEmail(String email) {
        Optional<PatientDto> patientByEmail = patientDao.findByEmail(email);
        if (patientByEmail.isEmpty()) {
            throw new NotFoundException("Could not find patient by email: [%s]".formatted(email));
        }
        return patientByEmail.get();
    }
}
