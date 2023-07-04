package com.medinet.business.services;

import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.PatientDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PatientService {
    private final PatientDao patientDao;

    public PatientDto findById(Integer patientId) {
        Optional<PatientDto> patientById = patientDao.findById(patientId);
        if(patientById.isEmpty()){
            throw new NotFoundException("Could not find patient by id: [%s]".formatted(patientId));
        }
        return patientById.get();
    }

}
