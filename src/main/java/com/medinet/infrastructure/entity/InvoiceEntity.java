package com.medinet.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"invoiceId", "uuid"})
@Entity
@Table(name = "invoice_table")
public class InvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer invoiceId;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "pdf_data", columnDefinition = "BYTEA")
    private byte[] pdfData;
}
