package com.medinet.api.controller;

import com.medinet.infrastructure.entity.InvoiceEntity;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class PdfDownloadController {

    @Autowired
    private InvoiceJpaRepository invoiceJpaRepository;

    @GetMapping("/invoice/download/{uuid}")
    public ResponseEntity<ByteArrayResource> downloadInvoice(@PathVariable String uuid) {
        Optional<InvoiceEntity> invoice = invoiceJpaRepository.findByUuid(uuid);
        if (invoice.isPresent() && invoice.get().getPdfData() != null) {
            byte[] pdfData = invoice.get().getPdfData();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faktura_medinet "+uuid+".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfData.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new ByteArrayResource(pdfData));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
