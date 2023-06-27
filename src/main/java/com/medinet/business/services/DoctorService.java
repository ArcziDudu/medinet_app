package com.medinet.business.services;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.dao.DoctorDao;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorDao doctorDao;
    private final DoctorMapper doctorMapper;
    public List<DoctorDto> findAllDoctors() {
        List<DoctorDto> doctors = doctorDao.findAllDoctors();
        log.info("Available doctors: [{}]", doctors.size());
        return doctors;
    }
}
