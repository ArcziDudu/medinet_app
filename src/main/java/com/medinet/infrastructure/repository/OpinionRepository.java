package com.medinet.infrastructure.repository;

import com.medinet.business.dao.OpinionDao;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.repository.jpa.OpinionJpaRepository;
import com.medinet.infrastructure.repository.mapper.OpinionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class OpinionRepository implements OpinionDao {
    private final OpinionJpaRepository opinionJpaRepository;

    @Override
    public void saveOpinion(OpinionEntity opinion) {
        opinionJpaRepository.save(opinion);
    }
}
