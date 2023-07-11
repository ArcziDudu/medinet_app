package com.medinet.infrastructure.repository;

import com.medinet.api.dto.OpinionDto;
import com.medinet.business.dao.OpinionDao;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.repository.jpa.OpinionJpaRepository;
import com.medinet.infrastructure.repository.mapper.OpinionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class OpinionRepository implements OpinionDao {
    private final OpinionJpaRepository opinionJpaRepository;
    private final OpinionMapper opinionMapper;

    @Override
    public OpinionDto saveOpinion(OpinionEntity opinion) {

        return opinionMapper.mapFromEntity(opinionJpaRepository.save(opinion));
    }

    @Override
    public List<OpinionDto> findAll() {
        return opinionJpaRepository.findAll()
                .stream()
                .map(opinionMapper::mapFromEntity)
                .toList();
    }
}
