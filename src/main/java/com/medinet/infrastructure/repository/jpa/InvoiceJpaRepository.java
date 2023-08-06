package com.medinet.infrastructure.repository.jpa;

import com.medinet.infrastructure.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, Integer> {
    Optional<InvoiceEntity> findByUuid(String uuid);

}
