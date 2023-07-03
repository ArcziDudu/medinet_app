package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.OpinionDto;
import com.medinet.infrastructure.entity.OpinionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OpinionMapper {
    OpinionDto mapFromEntity(OpinionEntity entity);
    OpinionEntity mapFromDto(OpinionDto dtos);
}
