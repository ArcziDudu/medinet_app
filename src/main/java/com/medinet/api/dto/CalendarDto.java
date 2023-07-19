package com.medinet.api.dto;

import com.medinet.infrastructure.entity.DoctorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDto {
    Integer calendarId;
    LocalDate date;
    List<LocalTime> hours;
    DoctorEntity doctor;
}
