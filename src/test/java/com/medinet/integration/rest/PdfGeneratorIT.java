package com.medinet.integration.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.medinet.business.services.PdfGeneratorService;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class PdfGeneratorIT {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);
    @Mock
    private InvoiceJpaRepository invoiceJpaRepository;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @BeforeEach
    public void setUp() {
        String baseUrl = "http://localhost:8089/api/v1/pdf";
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authentication", "Token co4o4OpK6M_WpBgMYInX2ybs6NmWwQ8e")
                .build();

        pdfGeneratorService = new PdfGeneratorService(webClient, invoiceJpaRepository);
    }

    @Test
    public void testGeneratePdf() throws Exception {

        String uuid = "51b6266c-5444-47b0-87b3-b41b0227188f";
        String htmlContent = testHtml();

        wireMockRule.stubFor(post(urlEqualTo("/api/v1/pdf"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));
        pdfGeneratorService.generatePdf(htmlContent, uuid);

        File pdfFile = new File("src/main/resources/invoices/faktura_medinet51b6266c-5444-47b0-87b3-b41b0227188f.pdf");


        assertTimeout(Duration.ofSeconds(10), () -> {
            while (!pdfFile.exists()) {
                Thread.sleep(1000);
            }
        });

        Assertions.assertTrue(Files.exists(pdfFile.toPath()), "Plik PDF nie został zapisany.");
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String contents = stripper.getText(document);

            Assertions.assertTrue(contents.contains("Faktura: Wizyta w przychodni medinet"), "Nie znaleziono tekstu 'Faktura: Wizyta w przychodni medinet' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Numer wizyty: 51b6266c-5444-47b0-87b3-b41b0227188f"), "Nie znaleziono tekstu 'Numer wizyty: 51b6266c-5444-47b0-87b3-b41b0227188f' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Data wizyty: 2023-07-13"), "Nie znaleziono tekstu 'Data wizyty: 2023-07-13' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Godzina: 09:00"), "Nie znaleziono tekstu 'Godzina: 09:00' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Imie i nazwisko pacjenta: Test test"), "Nie znaleziono tekstu 'Imie i nazwisko pacjenta: Test test' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Adres przychodni: Dukla 18 Lotheville Point"), "Nie znaleziono tekstu 'Adres przychodni: Dukla 18 Lotheville Point' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Lekarz: Tester Tester"), "Nie znaleziono tekstu 'Lekarz: Tester Tester' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Wizyta u specjalisty - Kardiolog"), "Nie znaleziono tekstu 'Wizyta u specjalisty - Kardiolog' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Informacje od lekarza: Zalecam tak nie robic"), "Nie znaleziono tekstu 'Informacje od lekarza: Zalecam tak nie robic' w pliku PDF.");
            Assertions.assertTrue(contents.contains("Koszt wizyty: 291.00"), "Nie znaleziono tekstu 'Koszt wizyty: 291.00' w pliku PDF.");
        } finally {

            if (pdfFile.exists()) {
                pdfFile.delete();
            }
        }
    }


    private String testHtml() {
        return "<!doctype html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "   body {" +
                "position: relative;" +
                "font-family: Arial, sans-serif;" +
                "display: flex;" +
                "margin-top: 50px;" +
                "justify-content: center;" +
                "align-items: flex-start;" +
                "text-align: start;" +
                "font-size: 19px; " +
                "font-weight: bold; " +
                "}" +
                "h6{" +
                "margin-top:30px" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h4>Faktura: Wizyta w przychodni medinet</h4>" +
                "<h4>Numer wizyty: 51b6266c-5444-47b0-87b3-b41b0227188f</h4>" +
                "<p>Data wizyty: 2023-07-13 </p>" +
                "<p>Godzina: 09:00 </p>" +
                "<p>Imie i nazwisko pacjenta: Test test </p>" +
                "<p>Adres przychodni: Dukla 18 Lotheville Point</p>" +
                "<p>Lekarz: Tester Tester</p>" +
                "<p>Wizyta u specjalisty - Kardiolog</p>" +
                "<p>Informacje od lekarza: Zalecam tak nie robic</p>" +
                "<p>Koszt wizyty: 291.00</p>" +
                "</body>" +
                "</html>";
    }

}

