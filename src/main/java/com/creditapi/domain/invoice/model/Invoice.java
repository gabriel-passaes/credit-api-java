package com.creditapi.domain.invoice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString(exclude = "pdf")
public class Invoice {
  private final String invoiceNumber;
  private final String issuerCnpj;
  private final String status;
  private final LocalDateTime issuedAt;
  private final byte[] pdf;
}
