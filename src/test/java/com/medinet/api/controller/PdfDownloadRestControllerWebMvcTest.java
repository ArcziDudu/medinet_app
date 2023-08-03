package com.medinet.api.controller;

import com.medinet.api.controller.rest.PdfDownloadRestController;
import com.medinet.infrastructure.entity.InvoiceEntity;
import com.medinet.infrastructure.repository.jpa.AppointmentJpaRepository;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = PdfDownloadRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PdfDownloadRestControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InvoiceJpaRepository invoiceJpaRepository;
    @MockBean
    private AppointmentJpaRepository appointmentJpaRepository;

    @Test
    public void testDownloadInvoice_ExistingInvoice_ShouldReturnPdfFile() throws Exception {

        String uuid = "some-uuid";
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setUuid(uuid);
        byte[] pdfData = "Mock PDF Data".getBytes();
        invoiceEntity.setPdfData(pdfData);

        when(invoiceJpaRepository.findByUuid(uuid)).thenReturn(Optional.of(invoiceEntity));


        mockMvc.perform(get("/api/invoice/download/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faktura_medinet " + uuid + ".pdf"))
                .andExpect(content().bytes(pdfData));
    }

    @Test
    public void testDownloadInvoice_NonExistingInvoice_ShouldReturnNotFound() throws Exception {

        String uuid = "non-existing-uuid";
        when(invoiceJpaRepository.findByUuid(uuid)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/invoice/download/{uuid}", uuid))
                .andExpect(status().isNotFound());
    }
}
