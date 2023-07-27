package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.PatientDto;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-27T22:37:03+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 18.0.2 (Oracle Corporation)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public PatientDto mapFromEntity(PatientEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PatientDto.PatientDtoBuilder patientDto = PatientDto.builder();

        patientDto.patientId( entity.getPatientId() );
        patientDto.name( entity.getName() );
        patientDto.email( entity.getEmail() );
        patientDto.surname( entity.getSurname() );
        patientDto.phoneNumber( entity.getPhoneNumber() );
        patientDto.address( entity.getAddress() );
        Set<OpinionEntity> set = entity.getOpinions();
        if ( set != null ) {
            patientDto.opinions( new LinkedHashSet<OpinionEntity>( set ) );
        }
        Set<AppointmentEntity> set1 = entity.getAppointments();
        if ( set1 != null ) {
            patientDto.appointments( new LinkedHashSet<AppointmentEntity>( set1 ) );
        }

        return patientDto.build();
    }

    @Override
    public PatientEntity mapFromDto(PatientDto patientDto) {
        if ( patientDto == null ) {
            return null;
        }

        PatientEntity.PatientEntityBuilder patientEntity = PatientEntity.builder();

        patientEntity.patientId( patientDto.getPatientId() );
        patientEntity.name( patientDto.getName() );
        patientEntity.email( patientDto.getEmail() );
        patientEntity.surname( patientDto.getSurname() );
        patientEntity.phoneNumber( patientDto.getPhoneNumber() );
        patientEntity.address( patientDto.getAddress() );
        Set<AppointmentEntity> set = patientDto.getAppointments();
        if ( set != null ) {
            patientEntity.appointments( new LinkedHashSet<AppointmentEntity>( set ) );
        }
        Set<OpinionEntity> set1 = patientDto.getOpinions();
        if ( set1 != null ) {
            patientEntity.opinions( new LinkedHashSet<OpinionEntity>( set1 ) );
        }

        return patientEntity.build();
    }
}
