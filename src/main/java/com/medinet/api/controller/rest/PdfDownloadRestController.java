package com.medinet.api.controller.rest;

import com.medinet.infrastructure.entity.AppointmentEntity;
import com.medinet.infrastructure.entity.InvoiceEntity;
import com.medinet.infrastructure.repository.jpa.AppointmentJpaRepository;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.result.view.RedirectView;

import java.util.Optional;
import java.util.UUID;

@Controller
public class PdfDownloadRestController {

    @Autowired
    private InvoiceJpaRepository invoiceJpaRepository;
    @Autowired
    private AppointmentJpaRepository appointmentJpaRepository;

    @GetMapping("/api/invoice/download/{uuid}")
    @Operation(summary = "Download invoice by uuid",
            description = "Download invoice if it exists in the database by uuid number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice Found"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public ResponseEntity<?> downloadInvoice(@PathVariable String uuid) {
        Optional<InvoiceEntity> invoice = invoiceJpaRepository.findByUuid(uuid);
        if(invoice.isEmpty()){
            String errorMessage = "Invoice with UUID " + uuid + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage);
        }

            byte[] pdfData = invoice.orElseThrow().getPdfData();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faktura_medinet " + uuid + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfData.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new ByteArrayResource(pdfData));

    }
}
