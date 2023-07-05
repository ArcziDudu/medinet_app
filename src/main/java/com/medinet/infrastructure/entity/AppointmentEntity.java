package com.medinet.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "appointmentId")
@Entity
@Table(name = "appointment")
public class AppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Column(name = "time_of_visit")
    private String timeOfVisit;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "note")
    private String noteOfAppointment;

    @Column(name = "uuid")
    private String UUID;

    @Column(name = "time_of_issue")
    private OffsetDateTime issueInvoice;

    @Column(name = "date_of_appointment")
    private LocalDate dateOfAppointment;

    @Column(name = "calendar_id")
    private Integer calendarId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private DoctorEntity doctor;


}
