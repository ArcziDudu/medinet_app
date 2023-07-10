package com.medinet.business.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
@AllArgsConstructor
public class PdfGeneratorService {

  private final WebClient webClient;

  public PdfGeneratorService() {
    this.webClient = WebClient.builder()
            .baseUrl("https://htmlpdfapi.com/api/v1/pdf")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authentication","Token Ti6QZHzQNWNqAgku0bKJaxLWabFkbhiM")
            .build();
  }

  public void generatePdf(String htmlContent, String uuid) {
    webClient.post()
            .uri("https://htmlpdfapi.com/api/v1/pdf")
            .body(BodyInserters.fromValue("{\"html\": \"" + htmlContent + "\"}"))
            .retrieve()
            .bodyToMono(byte[].class)
            .subscribe(pdfBytes -> {
              try (FileOutputStream outputStream = new FileOutputStream("src/main/resources/invoices/faktura_medinet"+uuid+".pdf")) {
                outputStream.write(pdfBytes);
                System.out.println("Plik PDF został zapisany.");
              } catch (IOException e) {
                System.out.println("Błąd podczas zapisywania pliku PDF: " + e.getMessage());
              }
            }, error -> {
              System.out.println("Błąd podczas generowania pliku PDF: " + error.getMessage());
            });
  }


}
