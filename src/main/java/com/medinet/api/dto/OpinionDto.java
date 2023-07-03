package com.medinet.api.dto;

import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpinionDto {
    private Integer opinionId;
    private OffsetDateTime dateOfCreateOpion;
    private String note;
    private PatientEntity patient;
    private DoctorEntity doctor;
}
