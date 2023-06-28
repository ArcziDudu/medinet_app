package com.medinet.business.dao;

import com.medinet.api.dto.DoctorDto;

import java.util.List;
import java.util.Map;

public interface DoctorDao {
    List<DoctorDto> findAllDoctors();
    List<DoctorDto> findAllDoctorsByCity(String city);

}
