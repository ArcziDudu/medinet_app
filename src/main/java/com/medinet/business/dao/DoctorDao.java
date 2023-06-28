package com.medinet.business.dao;

import com.medinet.api.dto.DoctorDto;

import java.util.List;

public interface DoctorDao {
    List<DoctorDto> findAllDoctors();
    List<DoctorDto> findAllDoctorsByCity(String city);
}
