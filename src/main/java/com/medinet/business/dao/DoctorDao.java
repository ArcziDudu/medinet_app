package com.medinet.business.dao;

import com.medinet.api.dto.DoctorDto;

import java.util.List;
import java.util.Set;

public interface DoctorDao {
    List<DoctorDto> findAllDoctors();
    List<DoctorDto> findAllDoctorsByCity(String city);

    List<DoctorDto> findAllDoctorsBySpecialization(String doctorSpecialization);
    Set<String> findAllAvailableSpecialization();
}
