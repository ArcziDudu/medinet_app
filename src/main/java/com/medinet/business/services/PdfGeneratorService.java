package com.medinet.business.services;

import com.medinet.infrastructure.entity.InvoiceEntity;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor

@Slf4j
public class PdfGeneratorService {

    private final WebClient webClient;

    @Autowired
    private InvoiceJpaRepository invoiceJpaRepository;

    public PdfGeneratorService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://htmlpdfapi.com/api/v1/pdf")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authentication", "Token co4o4OpK6M_WpBgMYInX2ybs6NmWwQ8e")
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString())
                .build();
    }


    public void generatePdf(String htmlContent, String uuid) {
        webClient.post()
                .uri("https://htmlpdfapi.com/api/v1/pdf")
                .body(BodyInserters.fromValue("{\"html\": \"" + htmlContent + "\"}"))
                .retrieve()
                .bodyToMono(byte[].class)
                .subscribe(
                        pdfBytes -> {
                            InvoiceEntity pdfDocument = new InvoiceEntity();
                            pdfDocument.setUuid(uuid);
                            pdfDocument.setPdfData(pdfBytes);
                            invoiceJpaRepository.save(pdfDocument);
                            log.info("Plik PDF został wygenerowany i zapisany w bazie danych.");
                            log.info("Numer UUID faktury: " + uuid);
                        },
                        error -> log.error("Błąd podczas generowania pliku PDF: " + error.getMessage())
                );
    }

}
