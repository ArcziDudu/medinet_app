package com.medinet.infrastructure.repository.jpa;

import com.medinet.api.dto.DoctorDto;
import com.medinet.infrastructure.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Integer> {

    @Query("""
            SELECT d
            FROM DoctorEntity d
            INNER JOIN d.address address
            where d.specialization = :doctorSpecialization
            AND d.address.city  = :doctorCity
            """)
    List<DoctorEntity> findAllDoctorsBySpecializationAndCity(String doctorSpecialization, String doctorCity);
}
