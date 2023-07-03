package com.medinet.api.dto;

import com.medinet.infrastructure.entity.AddressEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    private Integer patientId;
    private String name;
    private String email;
    private String surname;
    private String phoneNumber;
    private AddressEntity address;
}
