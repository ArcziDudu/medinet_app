package com.medinet.infrastructure.repository.jpa;

import com.medinet.infrastructure.entity.AvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityJpaRepository extends JpaRepository<AvailabilityEntity, Integer> {
}
