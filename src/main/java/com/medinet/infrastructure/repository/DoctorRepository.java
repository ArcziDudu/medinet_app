package com.medinet.infrastructure.repository;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.dao.DoctorDao;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.repository.jpa.DoctorJpaRepository;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class DoctorRepository implements DoctorDao {
    private final DoctorJpaRepository doctorJpaRepository;
    private final DoctorMapper doctorMapper;
    @Override
    public List<DoctorDto> findAllDoctors() {
        return doctorJpaRepository.findAll().stream()
                .map(doctorMapper::mapFromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DoctorDto> findAll(Pageable pageable) {
        Page<DoctorEntity> page = doctorJpaRepository.findAll(pageable);
        List<DoctorDto> doctorDtos = page.getContent().stream()
                .map(doctorMapper::mapFromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(doctorDtos, pageable, page.getTotalElements());
    }


    @Override
    public Optional<DoctorDto> findDoctorById(Integer doctorId) {
        return doctorJpaRepository.findById(doctorId).map(doctorMapper::mapFromEntity);
    }


    @Override
    public List<DoctorDto> findAllDoctorsBySpecializationAndCity(String doctorSpecialization, String doctorCity) {
        return doctorJpaRepository.findAllDoctorsBySpecializationAndCity(doctorSpecialization, doctorCity)
                .stream()
                .map(doctorMapper::mapFromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> findAllAvailableSpecialization() {
        return doctorJpaRepository.findAll()
                .stream()
                .map(DoctorEntity::getSpecialization)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> findAllAvailableCities() {
        return doctorJpaRepository.findAll()
                .stream()
                .map(a->a.getAddress().getCity())
                .collect(Collectors.toSet());
    }
}
