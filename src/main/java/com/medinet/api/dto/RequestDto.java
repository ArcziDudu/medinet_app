package com.medinet.api.dto;

import com.medinet.infrastructure.entity.PatientEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    String timeOfVisit;
    LocalDate dateOfAppointment;
    String email;
    Integer doctorId;

}
