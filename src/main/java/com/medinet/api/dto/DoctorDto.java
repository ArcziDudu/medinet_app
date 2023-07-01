package com.medinet.api.dto;

import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

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
    private Set<CalendarEntity> calendars;
}
