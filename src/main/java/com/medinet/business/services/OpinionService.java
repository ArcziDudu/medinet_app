package com.medinet.business.services;

import com.medinet.business.dao.OpinionDao;
import com.medinet.infrastructure.entity.OpinionEntity;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class OpinionService {
    private final OpinionDao opinionDao;

    @Transactional
    public void processOpinion(OpinionEntity opinion) {
        opinionDao.saveOpinion(opinion);
    }
}
