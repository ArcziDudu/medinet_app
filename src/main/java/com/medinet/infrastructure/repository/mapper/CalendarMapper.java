package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.CalendarDto;
import com.medinet.infrastructure.entity.CalendarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarMapper {

    CalendarDto mapFromEntity(CalendarEntity entity);

    CalendarEntity mapFromDto(CalendarDto calendar);
}
