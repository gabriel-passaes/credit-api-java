package com.creditapi.infrastructure.invoice.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

  @Id private String invoiceNumber;

  private String issuerCnpj;

  private String status;

  private LocalDateTime issuedAt;

  @Lob private byte[] pdf;
}
