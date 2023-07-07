package com.medinet.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"patientId", "email"})
@Entity
@Table(name = "patient")
public class PatientEntity {
    public Set<AppointmentEntity> getAppointments() {
        return appointments;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "surname")
    private String surname;

    @Column(name = "Phone")
    private String phoneNumber;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patient", cascade = CascadeType.REFRESH)
    private Set<AppointmentEntity> appointments;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patient")
    private Set<OpinionEntity> opinions;
}
