package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.DoctorDto;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.CalendarEntity;
import com.medinet.infrastructure.entity.DoctorEntity;
import com.medinet.infrastructure.entity.OpinionEntity;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-27T22:37:03+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 18.0.2 (Oracle Corporation)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public DoctorDto mapFromEntity(DoctorEntity entity) {
        if ( entity == null ) {
            return null;
        }

        DoctorDto.DoctorDtoBuilder doctorDto = DoctorDto.builder();

        doctorDto.doctorId( entity.getDoctorId() );
        doctorDto.name( entity.getName() );
        doctorDto.surname( entity.getSurname() );
        doctorDto.email( entity.getEmail() );
        doctorDto.priceForVisit( entity.getPriceForVisit() );
        doctorDto.specialization( entity.getSpecialization() );
        doctorDto.address( entity.getAddress() );
        Set<CalendarEntity> set = entity.getCalendars();
        if ( set != null ) {
            doctorDto.calendars( new TreeSet<CalendarEntity>( set ) );
        }
        Set<AppointmentEntity> set1 = entity.getAppointments();
        if ( set1 != null ) {
            doctorDto.appointments( new LinkedHashSet<AppointmentEntity>( set1 ) );
        }
        Set<OpinionEntity> set2 = entity.getOpinions();
        if ( set2 != null ) {
            doctorDto.opinions( new LinkedHashSet<OpinionEntity>( set2 ) );
        }
        doctorDto.user( entity.getUser() );

        return doctorDto.build();
    }

    @Override
    public DoctorEntity mapFromDto(DoctorDto doctorDto) {
        if ( doctorDto == null ) {
            return null;
        }

        DoctorEntity.DoctorEntityBuilder doctorEntity = DoctorEntity.builder();

        doctorEntity.doctorId( doctorDto.getDoctorId() );
        doctorEntity.name( doctorDto.getName() );
        doctorEntity.surname( doctorDto.getSurname() );
        doctorEntity.email( doctorDto.getEmail() );
        doctorEntity.priceForVisit( doctorDto.getPriceForVisit() );
        doctorEntity.specialization( doctorDto.getSpecialization() );
        doctorEntity.address( doctorDto.getAddress() );
        TreeSet<CalendarEntity> treeSet = doctorDto.getCalendars();
        if ( treeSet != null ) {
            doctorEntity.calendars( new LinkedHashSet<CalendarEntity>( treeSet ) );
        }
        Set<AppointmentEntity> set = doctorDto.getAppointments();
        if ( set != null ) {
            doctorEntity.appointments( new LinkedHashSet<AppointmentEntity>( set ) );
        }
        Set<OpinionEntity> set1 = doctorDto.getOpinions();
        if ( set1 != null ) {
            doctorEntity.opinions( new LinkedHashSet<OpinionEntity>( set1 ) );
        }
        doctorEntity.user( doctorDto.getUser() );

        return doctorEntity.build();
    }
}
