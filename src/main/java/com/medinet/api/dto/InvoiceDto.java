package com.medinet.api.dto;

import lombok.*;

@Data
@Builder
@ToString(of = "uuid")
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private Integer invoiceId;

    private String uuid;
    private byte[] pdfData;
}
