package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.OpinionDto;
import com.medinet.infrastructure.entity.OpinionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OpinionMapper {
    @Mapping(target = "patient.address", ignore = true)
    @Mapping(target = "patient.appointments", ignore = true)
    @Mapping(target = "patient.opinions", ignore = true)
    @Mapping(target = "patient.user", ignore = true)
    @Mapping(target = "doctor.address", ignore = true)
    @Mapping(target = "doctor.calendars", ignore = true)
    @Mapping(target = "doctor.opinions", ignore = true)
    @Mapping(target = "doctor.appointments", ignore = true)
    @Mapping(target = "doctor.user", ignore = true)
    OpinionDto mapFromEntity(OpinionEntity entity);
    OpinionEntity mapFromDto(OpinionDto dtos);
}
