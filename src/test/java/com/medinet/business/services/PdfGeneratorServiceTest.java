package com.medinet.business.services;

import com.medinet.infrastructure.entity.InvoiceEntity;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import com.medinet.integration.configuration.PersistenceContainerTestConfiguration;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
public class PdfGeneratorServiceTest {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private InvoiceJpaRepository invoiceJpaRepository;

    @Test
    public void testGeneratePdfAndSaveInvoice() {
        String testUuid = "test-uuid";
        String testHtmlContent = "<!DOCTYPE html><html><head></head><body><h1>Hello World!</h1></body></html>";

        pdfGeneratorService.generatePdf(testHtmlContent, testUuid);

        InvoiceEntity savedInvoice = invoiceJpaRepository.findByUuid(testUuid).orElse(null);

    }


}