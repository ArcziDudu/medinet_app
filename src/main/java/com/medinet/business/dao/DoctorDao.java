package com.medinet.business.dao;

import com.medinet.api.dto.DoctorDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DoctorDao {
    List<DoctorDto> findAllDoctors();
    Optional<DoctorDto> findDoctorById(Integer doctorId);

    List<DoctorDto> findAllDoctorsBySpecializationAndCity(String doctorSpecialization, String doctorCity);
    Set<String> findAllAvailableSpecialization();

    Set<String> findAllAvailableCities();
}
