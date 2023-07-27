package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.OpinionDto;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-27T22:37:03+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 18.0.2 (Oracle Corporation)"
)
@Component
public class OpinionMapperImpl implements OpinionMapper {

    @Override
    public OpinionDto mapFromEntity(OpinionEntity entity) {
        if ( entity == null ) {
            return null;
        }

        OpinionDto.OpinionDtoBuilder opinionDto = OpinionDto.builder();

        opinionDto.opinionId( entity.getOpinionId() );
        opinionDto.dateOfCreateOpinion( entity.getDateOfCreateOpinion() );
        opinionDto.note( entity.getNote() );
        opinionDto.patient( patientEntityToPatientEntity( entity.getPatient() ) );
        opinionDto.doctor( doctorEntityToDoctorEntity( entity.getDoctor() ) );

        return opinionDto.build();
    }

    @Override
    public OpinionEntity mapFromDto(OpinionDto dtos) {
        if ( dtos == null ) {
            return null;
        }

        OpinionEntity.OpinionEntityBuilder opinionEntity = OpinionEntity.builder();

        opinionEntity.opinionId( dtos.getOpinionId() );
        opinionEntity.dateOfCreateOpinion( dtos.getDateOfCreateOpinion() );
        opinionEntity.note( dtos.getNote() );
        opinionEntity.patient( dtos.getPatient() );
        opinionEntity.doctor( dtos.getDoctor() );

        return opinionEntity.build();
    }

    protected PatientEntity patientEntityToPatientEntity(PatientEntity patientEntity) {
        if ( patientEntity == null ) {
            return null;
        }

        PatientEntity.PatientEntityBuilder patientEntity1 = PatientEntity.builder();

        patientEntity1.patientId( patientEntity.getPatientId() );
        patientEntity1.name( patientEntity.getName() );
        patientEntity1.email( patientEntity.getEmail() );
        patientEntity1.surname( patientEntity.getSurname() );
        patientEntity1.phoneNumber( patientEntity.getPhoneNumber() );

        return patientEntity1.build();
    }

    protected DoctorEntity doctorEntityToDoctorEntity(DoctorEntity doctorEntity) {
        if ( doctorEntity == null ) {
            return null;
        }

        DoctorEntity.DoctorEntityBuilder doctorEntity1 = DoctorEntity.builder();

        doctorEntity1.doctorId( doctorEntity.getDoctorId() );
        doctorEntity1.name( doctorEntity.getName() );
        doctorEntity1.surname( doctorEntity.getSurname() );
        doctorEntity1.email( doctorEntity.getEmail() );
        doctorEntity1.priceForVisit( doctorEntity.getPriceForVisit() );
        doctorEntity1.specialization( doctorEntity.getSpecialization() );

        return doctorEntity1.build();
    }
}
