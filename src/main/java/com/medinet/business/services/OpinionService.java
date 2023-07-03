package com.medinet.business.services;

import com.medinet.business.dao.OpinionDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class OpinionService {
    private final OpinionDao opinionDao;
}
