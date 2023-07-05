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

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "patient", cascade = CascadeType.REMOVE)
    private Set<AppointmentEntity> appointments;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "patient", cascade = CascadeType.ALL)
    private Set<OpinionEntity> opinions;
}
