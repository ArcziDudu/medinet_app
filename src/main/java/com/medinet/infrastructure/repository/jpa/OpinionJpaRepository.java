package com.medinet.infrastructure.repository.jpa;

import com.medinet.infrastructure.entity.OpinionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpinionJpaRepository extends JpaRepository<OpinionEntity, Integer> {

}
