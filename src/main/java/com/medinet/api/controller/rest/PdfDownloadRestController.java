package com.medinet.api.controller.rest;

import com.medinet.api.dto.InvoiceDto;
import com.medinet.business.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;

@Controller
@AllArgsConstructor
public class PdfDownloadRestController {

    @Autowired
    private final InvoiceService invoiceService;

    @GetMapping("/api/invoice/download/{uuid}")
    @Operation(summary = "Download invoice by uuid",
            description = "Download invoice if it exists in the database by uuid number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice Found"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public ResponseEntity<?> downloadInvoice(@PathVariable String uuid) {
        InvoiceDto invoice = invoiceService.findByUuid(uuid);
        if (Objects.isNull(invoice)) {
            String errorMessage = "Invoice with UUID " + uuid + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage);
        }

        byte[] pdfData = invoice.getPdfData();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faktura_medinet " + uuid + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfData.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdfData));

    }
}
