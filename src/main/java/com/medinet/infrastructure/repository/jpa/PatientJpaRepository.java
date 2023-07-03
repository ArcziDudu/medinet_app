package com.medinet.infrastructure.repository.jpa;

import com.medinet.infrastructure.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientJpaRepository extends JpaRepository<PatientEntity, Integer> {
}
