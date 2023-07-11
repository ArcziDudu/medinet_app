package com.medinet.business.services;

import com.medinet.api.dto.OpinionDto;
import com.medinet.business.dao.OpinionDao;
import com.medinet.infrastructure.entity.OpinionEntity;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class OpinionService {
    private final OpinionDao opinionDao;

    @Transactional
    public OpinionDto processOpinion(OpinionEntity opinion) {
        return opinionDao.saveOpinion(opinion);
    }

    public List<OpinionDto> findAll() {
       return opinionDao.findAll();
    }
}
