package com.medinet.business.dao;

import com.medinet.api.dto.OpinionDto;
import com.medinet.infrastructure.entity.OpinionEntity;

import java.util.List;

public interface OpinionDao {
    OpinionDto saveOpinion(OpinionEntity opinion);

    List<OpinionDto> findAll();

}
