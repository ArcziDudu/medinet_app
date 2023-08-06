package com.medinet.infrastructure.repository.mapper;

import com.medinet.api.dto.InvoiceDto;
import com.medinet.infrastructure.entity.InvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {
    InvoiceDto mapFromEntity(InvoiceEntity entity);

    InvoiceEntity mapFromDto(InvoiceDto patientDto);
}
