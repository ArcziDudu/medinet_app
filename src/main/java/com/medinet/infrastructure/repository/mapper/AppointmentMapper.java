package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.AppointmentDto;
import com.medinet.api.dto.PatientDto;
import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.PatientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {
    AppointmentDto mapFromEntity(AppointmentEntity entity);
}
