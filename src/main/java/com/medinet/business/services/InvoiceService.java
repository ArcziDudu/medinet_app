package com.medinet.business.services;

import com.medinet.api.dto.InvoiceDto;
import com.medinet.business.dao.InvoiceDao;
import com.medinet.domain.exception.NotFoundException;
import com.medinet.infrastructure.entity.InvoiceEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class InvoiceService {
    private InvoiceDao invoiceDao;

    @Transactional
    public void save(InvoiceEntity pdfDocument) {
        invoiceDao.save(pdfDocument);
    }

    public InvoiceDto findByUuid(String uuid) {
        Optional<InvoiceDto> invoiceByUuid = invoiceDao.findByUuid(uuid);
        if (invoiceByUuid.isEmpty()) {
            throw new NotFoundException("Could not find invoice by uuid: [%s]".formatted(uuid));
        }
        return invoiceByUuid.get();
    }
}
