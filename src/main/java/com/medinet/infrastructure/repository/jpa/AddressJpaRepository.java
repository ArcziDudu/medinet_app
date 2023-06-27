package com.medinet.infrastructure.repository.jpa;

import com.medinet.infrastructure.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressJpaRepository extends JpaRepository<AddressEntity, Integer> {
}
