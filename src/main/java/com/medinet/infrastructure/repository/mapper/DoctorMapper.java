package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.DoctorDto;
import com.medinet.infrastructure.entity.DoctorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorMapper {

    DoctorDto mapFromEntity(DoctorEntity entity);

    DoctorEntity mapFromDto(DoctorDto doctorDto);
}
