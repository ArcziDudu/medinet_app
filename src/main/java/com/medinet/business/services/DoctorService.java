package com.medinet.business.services;

import com.medinet.api.dto.DoctorDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.business.dao.DoctorDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.configuration.BootstrapApplicationComponent;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;

import com.medinet.infrastructure.security.RoleEntity;
import com.medinet.infrastructure.security.RoleRepository;
import com.medinet.infrastructure.security.UserEntity;
import com.medinet.infrastructure.security.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorDao doctorDao;
    private final CalendarService calendarService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public List<DoctorDto> findAllDoctors() {
        return doctorDao.findAllDoctors();
    }

    @Transactional
    public Page<DoctorDto> findAllDoctors(Integer pageNumber) {
        int pageSize = 15;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return doctorDao.findAll(pageable);
    }

    @Transactional
    public Page<DoctorDto> findAllDoctorsBySpecializationAndCity(
            String doctorSpecialization,
            String doctorCity,
            Integer pageNumber) {
        int pageSize = 15;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return doctorDao.findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity, pageable);
    }

    @Transactional
    public Set<String> findAllAvailableSpecialization() {
        Set<String> specializations = doctorDao.findAllAvailableSpecialization();
        log.info("Available specializations: [{}]", specializations.size());
        return specializations;
    }

    @Transactional
    public Set<String> findAllAvailableCities() {
        Set<String> cities = doctorDao.findAllAvailableCities();
        log.info("Available cities: [{}]", cities.size());
        return cities;
    }

    @Transactional
    public DoctorDto findDoctorById(Integer doctorId) {
        Optional<DoctorDto> doctorById = doctorDao.findDoctorById(doctorId);
        if (doctorById.isEmpty()) {
            throw new NotFoundException("Could not find doctor by doctorId: [%s]".formatted(doctorId));
        }
        return doctorById.get();
    }

    @Transactional
    public DoctorEntity create(DoctorEntity newDoctor) {

        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity roleDoctor = roleRepository.findByRole("DOCTOR");
        roles.add(roleDoctor);
        UserEntity user = UserEntity.builder()
                .email(newDoctor.getEmail())
                .password(passwordEncoder.encode("test"))
                .active(true)
                .roles(roles)
                .build();
        userRepository.save(user);
        newDoctor.setUser(user);
        DoctorEntity doctor = doctorDao.save(newDoctor);

        for (LocalDate date : BootstrapApplicationComponent.generateDateList()) {
            CalendarEntity calendar = new CalendarEntity();
            calendar.setDoctor(doctor);
            calendar.setDate(date);
            calendar.setHours(BootstrapApplicationComponent.hoursArrayGenerator());
            calendarService.save(calendar);
        }
        return doctorDao.save(newDoctor);
    }

    @Transactional
    public void deleteById(Integer doctorId) {

        Optional<DoctorDto> doctorById = doctorDao.findDoctorById(doctorId);
        if(doctorById.isEmpty()){
            throw new NotFoundException("Could not find doctor by id: [%s]".formatted(doctorId));
        }

        doctorDao.deleteDoctor(doctorId);
        userRepository.deleteByEmail(doctorById.get().getEmail());
    }

    public DoctorDto findByEmail(String email) {
        Optional<DoctorDto> DoctorByEmail =  doctorDao.findByEmail(email);
        if(DoctorByEmail.isEmpty()){
            throw new NotFoundException("Could not find doctor by email: [%s]".formatted(email));
        }
        return DoctorByEmail.get();

    }}


