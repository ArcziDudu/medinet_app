package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.infrastructure.entity.AppointmentEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-27T22:37:03+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.1.1.jar, environment: Java 18.0.2 (Oracle Corporation)"
)
@Component
public class AppointmentMapperImpl implements AppointmentMapper {

    @Override
    public AppointmentDto mapFromEntity(AppointmentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AppointmentDto.AppointmentDtoBuilder appointmentDto = AppointmentDto.builder();

        appointmentDto.appointmentId( entity.getAppointmentId() );
        appointmentDto.timeOfVisit( entity.getTimeOfVisit() );
        appointmentDto.status( entity.getStatus() );
        appointmentDto.noteOfAppointment( entity.getNoteOfAppointment() );
        appointmentDto.UUID( entity.getUUID() );
        appointmentDto.issueInvoice( entity.getIssueInvoice() );
        appointmentDto.dateOfAppointment( entity.getDateOfAppointment() );
        appointmentDto.patient( entity.getPatient() );
        appointmentDto.doctor( entity.getDoctor() );
        appointmentDto.calendarId( entity.getCalendarId() );

        return appointmentDto.build();
    }

    @Override
    public AppointmentEntity mapFromDto(AppointmentDto appointmentDto) {
        if ( appointmentDto == null ) {
            return null;
        }

        AppointmentEntity.AppointmentEntityBuilder appointmentEntity = AppointmentEntity.builder();

        appointmentEntity.appointmentId( appointmentDto.getAppointmentId() );
        appointmentEntity.timeOfVisit( appointmentDto.getTimeOfVisit() );
        appointmentEntity.status( appointmentDto.getStatus() );
        appointmentEntity.noteOfAppointment( appointmentDto.getNoteOfAppointment() );
        appointmentEntity.UUID( appointmentDto.getUUID() );
        appointmentEntity.issueInvoice( appointmentDto.getIssueInvoice() );
        appointmentEntity.dateOfAppointment( appointmentDto.getDateOfAppointment() );
        appointmentEntity.calendarId( appointmentDto.getCalendarId() );
        appointmentEntity.patient( appointmentDto.getPatient() );
        appointmentEntity.doctor( appointmentDto.getDoctor() );

        return appointmentEntity.build();
    }
}
