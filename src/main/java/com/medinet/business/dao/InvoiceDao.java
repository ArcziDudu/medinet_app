package com.medinet.business.dao;

import com.medinet.api.dto.InvoiceDto;
import com.medinet.infrastructure.entity.InvoiceEntity;

import java.util.Optional;

public interface InvoiceDao {
    Optional<InvoiceDto> findByUuid(String uuid);

    void save(InvoiceEntity pdfDocument);
}
