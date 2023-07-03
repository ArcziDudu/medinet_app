package com.medinet.business.services;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.dao.DoctorDao;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
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
    private final DoctorMapper doctorMapper;


    public List<DoctorDto> findAllDoctors() {
        List<DoctorDto> doctors = doctorDao.findAllDoctors();
        log.info("Available doctors: [{}]", doctors.size());

        return doctors;
    }

    public Page<DoctorDto> findAllDoctors(Integer pageNumber){
        int pageSize = 15;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return doctorDao.findAll(pageable);
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

    public DoctorDto findDoctorById(Integer doctorId) {
        Optional<DoctorDto> doctorById = doctorDao.findDoctorById(doctorId);
        if(doctorById.isEmpty()){
            throw new RuntimeException("Could not find car by doctorId: " + doctorId);
        }
        return doctorById.get();
    }


}
