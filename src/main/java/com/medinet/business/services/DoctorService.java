package com.medinet.business.services;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.dao.DoctorDao;
import com.medinet.domain.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorDao doctorDao;

    public List<DoctorDto> findAllDoctors() {
        return doctorDao.findAllDoctors();
    }

    public Page<DoctorDto> findAllDoctors(Integer pageNumber) {
        int pageSize = 15;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return doctorDao.findAll(pageable);
    }


    public Page<DoctorDto> findAllDoctorsBySpecializationAndCity(String doctorSpecialization, String doctorCity, Integer pageNumber) {
        int pageSize = 15;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return doctorDao.findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity, pageable);
    }

    public Set<String> findAllAvailableSpecialization() {
        Set<String> specializations = doctorDao.findAllAvailableSpecialization();
        log.info("Available specializations: [{}]", specializations.size());
        return specializations;
    }

    public Set<String> findAllAvailableCities() {
        Set<String> cities = doctorDao.findAllAvailableCities();
        log.info("Available cities: [{}]", cities.size());
        return cities;
    }

    public DoctorDto findDoctorById(Integer doctorId) {
        Optional<DoctorDto> doctorById = doctorDao.findDoctorById(doctorId);
        if (doctorById.isEmpty()) {
            throw new NotFoundException("Could not find car by doctorId: [%s]".formatted(doctorId));
        }
        return doctorById.get();
    }

}
