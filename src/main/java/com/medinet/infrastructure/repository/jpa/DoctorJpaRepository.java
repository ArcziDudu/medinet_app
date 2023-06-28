package com.medinet.infrastructure.repository.jpa;

import com.medinet.api.dto.DoctorDto;
import com.medinet.infrastructure.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Integer> {
    @Query("""
            SELECT d FROM DoctorEntity d
            JOIN d.address a
            WHERE a.city = :city
            """)
    List<DoctorEntity> findAllDoctorsByCity(String city);
}
