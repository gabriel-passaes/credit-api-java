package com.creditapi.infrastructure.invoice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalInvoiceStatusDTO {

  @JsonProperty("numero_nota")
  private String invoiceNumber;

  @JsonProperty("status")
  private String status;

  @JsonProperty("mensagem")
  private String message;

  @JsonProperty("data_emissao")
  private String issueDate;

  @JsonProperty("cnpj_emitente")
  private String issuerCnpj;

  @JsonProperty("ultima_atualizacao")
  private LocalDateTime lastUpdated;
}
