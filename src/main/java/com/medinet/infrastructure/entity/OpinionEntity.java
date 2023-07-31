package com.medinet.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"opinionId"})
@Entity
@Table(name = "opinion")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opinionId")
public class OpinionEntity implements Comparable<OpinionEntity>{
    @Override
    public int compareTo(OpinionEntity other) {
        return other.getDateOfCreateOpinion().compareTo(this.dateOfCreateOpinion);
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opinion_id")
    private Integer opinionId;

    @Column(name = "date")
    private OffsetDateTime dateOfCreateOpinion;

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private DoctorEntity doctor;


}
