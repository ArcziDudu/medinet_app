package com.medinet.infrastructure.repository.jpa;

import com.medinet.api.dto.DoctorDto;
import com.medinet.infrastructure.entity.DoctorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Integer> {
    @Query("SELECT d FROM DoctorEntity d")
    Page<DoctorEntity> findAllDoctors(Pageable pageable);
    @Query("""
            SELECT d
            FROM DoctorEntity d
            INNER JOIN d.address address
            where d.specialization = :doctorSpecialization
            AND d.address.city  = :doctorCity
            """)
    Page<DoctorEntity> findAllDoctorsBySpecializationAndCity(
            String doctorSpecialization,
            String doctorCity,
            Pageable pageable);

    Optional<DoctorEntity> findByEmail(String email);
}
