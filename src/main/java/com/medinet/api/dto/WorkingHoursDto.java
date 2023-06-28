package com.medinet.api.dto;

import com.medinet.infrastructure.entity.DoctorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHoursDto {
    private Integer workingHoursId;
    private DoctorEntity doctor;
    private Date workingDate;
}
