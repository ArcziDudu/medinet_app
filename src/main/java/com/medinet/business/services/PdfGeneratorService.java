package com.medinet.business.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class PdfGeneratorService {

    private final WebClient webClient;

    public PdfGeneratorService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://htmlpdfapi.com/api/v1/pdf")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authentication", "Token co4o4OpK6M_WpBgMYInX2ybs6NmWwQ8e")
                .build();
    }

    public void generatePdf(String htmlContent, String uuid) {
        String pdfDirectory = System.getenv("PDF_DIRECTORY");
        String filePath;
        if (pdfDirectory != null) {
            filePath = pdfDirectory + "/faktura_medinet" + uuid + ".pdf";
        } else {
            filePath = "src/main/resources/invoices/faktura_medinet" + uuid + ".pdf";
        }

        webClient.post()
                .uri("https://htmlpdfapi.com/api/v1/pdf")
                .body(BodyInserters.fromValue("{\"html\": \"" + htmlContent + "\"}"))
                .retrieve()
                .bodyToMono(byte[].class)
                .subscribe(pdfBytes -> {
                    try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                        outputStream.write(pdfBytes);
                        log.info("Plik PDF został zapisany.");
                        log.info("Numer UUID faktury: " + uuid);
                        log.info("Plik znajduję się pod ścieżką: " + filePath);
                    } catch (IOException e) {
                        log.error("Błąd podczas zapisywania pliku PDF: " + e.getMessage());

                    }
                }, error -> {
                    log.error("Błąd podczas generowania pliku PDF: " + error.getMessage());
                });
    }
}
