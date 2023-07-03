package com.medinet.infrastructure.repository;

import com.medinet.infrastructure.repository.jpa.OpinionJpaRepository;
import com.medinet.infrastructure.repository.mapper.OpinionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class OpinionRepository {
    private final OpinionJpaRepository opinionJpaRepository;
    private final OpinionMapper opinionMapper;
}
