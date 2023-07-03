package com.medinet.business.dao;

import com.medinet.api.dto.DoctorDto;
import com.medinet.infrastructure.entity.DoctorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DoctorDao {
    List<DoctorDto> findAllDoctors();
    Page<DoctorDto> findAll(Pageable pageable);
    Optional<DoctorDto> findDoctorById(Integer doctorId);

    List<DoctorDto> findAllDoctorsBySpecializationAndCity(String doctorSpecialization, String doctorCity);
    Set<String> findAllAvailableSpecialization();

    Set<String> findAllAvailableCities();

}
