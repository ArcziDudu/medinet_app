package com.medinet.api.dto;

import com.medinet.infrastructure.entity.AddressEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private Integer doctorId;
    String name;
    String surname;
    String email;
    BigDecimal priceForVisit;
    String specialization;
    AddressEntity address;

}
