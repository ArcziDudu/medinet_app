package com.medinet.api.dto;

import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    Integer appointmentId;
    String timeOfVisit;
    String status;
    String noteOfAppointment;
    String UUID;
    OffsetDateTime issueInvoice;
    LocalDate dateOfAppointment;
    PatientEntity patient;
    DoctorEntity doctor;
    Integer calendarId;
}
