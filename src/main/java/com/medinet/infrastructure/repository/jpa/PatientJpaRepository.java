package com.medinet.infrastructure.repository.jpa;

import com.medinet.infrastructure.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientJpaRepository extends JpaRepository<PatientEntity, Integer> {


    boolean existsByPhoneNumber(String phoneNumber);

    Optional<PatientEntity> findByUserId(int id);

    Optional<PatientEntity> findByEmail(String email);
}
