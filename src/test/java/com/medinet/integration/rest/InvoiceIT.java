package com.medinet.integration.rest;

import com.medinet.business.services.InvoiceService;
import com.medinet.infrastructure.entity.InvoiceEntity;
import com.medinet.integration.configuration.RestAssuredIntegrationTestBase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvoiceIT extends RestAssuredIntegrationTestBase
        implements WiremockTestSupport {
    @Autowired
    InvoiceService invoiceService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    void setup() {
        String uuid = "some_unique_uuid";
        String test = "74657374";
        byte[] pdfData = test.getBytes();
        invoiceService.save(InvoiceEntity.builder()
                .uuid(uuid)
                .pdfData(pdfData)
                .build());
    }

    @Test
    public void testGeneratePdfWiremock() throws Exception {
        //given
        String htmlContent = testHtml();
        String uuid = "3abcc6b0-8c55-4ee6-a0ea-4b1cffe881da";
        byte[] pdfBytes = htmlContent.getBytes();

        stubForInvoice(wireMockServer, htmlContent, pdfBytes);

        given()
                .body(htmlContent)
                .contentType("application/json")
                .when()
                .post("http://localhost:" + 9999 + "/pdf")
                .then()
                .statusCode(200);
        byte[] responseBytes = given()
                .contentType("application/json")
                .body(htmlContent)
                .when()
                .post("http://localhost:" + 9999 + "/pdf")
                .then()
                .statusCode(200)
                .extract().asByteArray();

        Assertions.assertEquals(Arrays.toString(responseBytes), Arrays.toString(pdfBytes));
        Assertions.assertEquals(new String(responseBytes), htmlContent);

        String extractedUUID = extractUUIDFromResponse(new String(responseBytes));
        Assertions.assertEquals(uuid, extractedUUID);
    }

    private String extractUUIDFromResponse(String html) {
        String regex = "\\b[A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{12}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Test
    public void givenNonExistingUuidwhenDownloadInvoicethenReturnNotFound() {

        String uuid = "non_existing_uuid";
        given().spec(requestSpecification())
                .pathParam("uuid", uuid)
                .when()
                .get("http://localhost:" + port + basePath + "/api/invoice/download/{uuid}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void givenExistingUuidWhenDownloadInvoiceThenReturnPdfFile() {
        String uuid = "some_unique_uuid";
        String test = "74657374";
        byte[] pdfData = test.getBytes();

        given().spec(requestSpecification())
                .pathParam("uuid", uuid)
                .when()
                .get("http://localhost:" + port + basePath + "/api/invoice/download/{uuid}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType("application/pdf")
                .header("Content-Disposition", "attachment; filename=faktura_medinet " + uuid + ".pdf")
                .body(Matchers.equalToIgnoringCase(new String(pdfData)));
    }
}