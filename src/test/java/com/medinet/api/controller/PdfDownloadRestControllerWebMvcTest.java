package com.medinet.api.controller;

import com.medinet.api.controller.rest.PdfDownloadRestController;
import com.medinet.api.dto.InvoiceDto;
import com.medinet.business.services.InvoiceService;
import com.medinet.infrastructure.repository.jpa.AppointmentJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = PdfDownloadRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PdfDownloadRestControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InvoiceService invoiceService;
    @MockBean
    private AppointmentJpaRepository appointmentJpaRepository;

    @Test
    public void testDownloadInvoice_ExistingInvoice_ShouldReturnPdfFile() throws Exception {

        String uuid = "some-uuid";
        InvoiceDto invoice = new InvoiceDto();
        invoice.setUuid(uuid);
        byte[] pdfData = "Mock PDF Data".getBytes();
        invoice.setPdfData(pdfData);

        when(invoiceService.findByUuid(uuid)).thenReturn(invoice);


        mockMvc.perform(get("/api/invoice/download/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faktura_medinet " + uuid + ".pdf"))
                .andExpect(content().bytes(pdfData));
    }

    @Test
    public void testDownloadInvoiceNonExistingInvoiceShouldReturnNotFound() throws Exception {

        String uuid = "non-existing-uuid";
        when(invoiceService.findByUuid(uuid)).thenReturn(null);


        mockMvc.perform(get("/api/invoice/download/{uuid}", uuid))
                .andExpect(status().isNotFound());
    }
}
