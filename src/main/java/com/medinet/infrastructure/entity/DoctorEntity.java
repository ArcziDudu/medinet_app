package com.medinet.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.medinet.infrastructure.security.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"doctorId", "email"})
@Entity
@Table(name = "doctor")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "doctorId")
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "price_for_visit")
    private BigDecimal priceForVisit;

    @Column(name = "specialization")
    private String specialization;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "doctor", cascade = CascadeType.ALL)
    private Set<CalendarEntity> calendars;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "doctor", cascade = CascadeType.ALL)
    private Set<AppointmentEntity> appointments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "doctor", cascade = CascadeType.ALL)
    private Set<OpinionEntity> opinions;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}