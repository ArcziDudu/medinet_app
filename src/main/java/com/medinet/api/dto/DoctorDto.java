package com.medinet.api.dto;

import com.medinet.infrastructure.entity.AddressEntity;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.security.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

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
    private TreeSet<CalendarEntity> calendars;
    private Set<AppointmentEntity> appointments;
    private Set<OpinionEntity> opinions;
    private UserEntity user;
}
