package com.medinet.infrastructure.repository;

import com.medinet.api.dto.InvoiceDto;
import com.medinet.business.dao.InvoiceDao;
import com.medinet.infrastructure.entity.InvoiceEntity;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import com.medinet.infrastructure.repository.mapper.InvoiceMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class InvoiceRepository implements InvoiceDao {
    private InvoiceJpaRepository invoiceJpaRepository;
    private InvoiceMapper invoiceMapper;

    @Override
    public Optional<InvoiceDto> findByUuid(String uuid) {
        return invoiceJpaRepository.findByUuid(uuid)
                .map(invoiceMapper::mapFromEntity);
    }

    @Override
    public void save(InvoiceEntity pdfDocument) {
        invoiceJpaRepository.save(pdfDocument);
    }
}
