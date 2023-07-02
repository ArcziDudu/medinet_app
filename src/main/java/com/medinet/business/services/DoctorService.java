package com.medinet.business.services;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.dao.DoctorDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorDao doctorDao;


    public List<DoctorDto> findAllDoctors() {
        List<DoctorDto> doctors = doctorDao.findAllDoctors();
        log.info("Available doctors: [{}]", doctors.size());

        return doctors;
    }


    public List<DoctorDto> findAllDoctorsBySpecializationAndCity(String doctorSpecialization, String doctorCity) {
        List<DoctorDto> doctors = doctorDao.findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity);
        log.info("Available doctors: [{}]", doctors.size());
        return doctors;
    }

    public Set<String> findAllAvailableSpecialization(){
        Set<String> specializations = doctorDao.findAllAvailableSpecialization();
        log.info("Available specializations: [{}]", specializations.size());
        return specializations;
    }

    public Set<String> findAllAvailableCities(){
        Set<String> cities = doctorDao.findAllAvailableCities();
        log.info("Available cities: [{}]", cities.size());
        return cities;
    }
}
