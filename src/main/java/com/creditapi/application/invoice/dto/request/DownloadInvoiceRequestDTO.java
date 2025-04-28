package com.creditapi.application.invoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DownloadInvoiceRequestDTO(
    @NotBlank(message = "O número da nota fiscal é obrigatório.") String invoiceNumber,
    @NotBlank(message = "O CNPJ do emitente é obrigatório.") String cnpj) {}
