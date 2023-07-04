package com.medinet.api.dto;

import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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
    private Set<OpinionEntity> opinions;
}
