package com.creditapi.application.invoice.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InvoiceStatusResponseDTO(String status, String message, LocalDateTime lastUpdated) {}
