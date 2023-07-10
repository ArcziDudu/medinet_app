package com.medinet.infrastructure.repository.jpa;

import com.medinet.infrastructure.entity.PatientEntity;
import com.medinet.infrastructure.security.UserEntity;
import org.antlr.v4.runtime.misc.MultiMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientJpaRepository extends JpaRepository<PatientEntity, Integer> {


    boolean existsByPhoneNumber(String phoneNumber);

   Optional<PatientEntity> findByUserId(int id);
}
