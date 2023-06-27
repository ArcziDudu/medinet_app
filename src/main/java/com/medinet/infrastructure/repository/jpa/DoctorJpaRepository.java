package com.medinet.infrastructure.repository.jpa;

import com.medinet.infrastructure.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Integer> {
}
