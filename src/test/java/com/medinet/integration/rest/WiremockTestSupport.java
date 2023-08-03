package com.medinet.integration.rest;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public interface WiremockTestSupport {
    default void stubForInvoice(WireMockServer wireMockServer, String htmlContent, byte[] pdfBytes) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/pdf"))
                .withRequestBody(equalTo(htmlContent))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(pdfBytes)));
    }

    default String testHtml() {
        return "<!DOCTYPE html>" +
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
                "<h4>Numer wizyty: 3abcc6b0-8c55-4ee6-a0ea-4b1cffe881da</h4>" +
                "<p>Data wizyty: 2023-08-03</p>" +
                "<p>Godzina: 08:00</p>" +
                "<p>Imie i nazwisko pacjenta: Admin Admin</p>" +
                "<p>Adres przychodni: Wrocław adminowa 32</p>" +
                "<p>Lekarz: Krystian Wieczorek</p>" +
                "<p>Wizyta u specjalisty - Kardiolog</p>" +
                "<p>Informacje od lekarza: Testowa faktura</p>" +
                "<p>Koszt wizyty: 291.00 zł</p>" +
                "</body>" +
                "</html>";
    }
}
