package com.medinet.api.dto;

import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.AppointmentEntity;
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
    private String name;
    private String surname;
    private String email;
    private BigDecimal priceForVisit;
    private String specialization;
    private AddressEntity address;
    private Set<CalendarEntity> calendars;
    Set<AppointmentEntity> appointments;
}
