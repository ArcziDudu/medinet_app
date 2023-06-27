package com.medinet.infrastructure.repository;

import com.medinet.api.dto.DoctorDto;
import com.medinet.business.dao.DoctorDao;
import com.medinet.infrastructure.repository.jpa.DoctorJpaRepository;
import com.medinet.infrastructure.repository.mapper.DoctorMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
}
