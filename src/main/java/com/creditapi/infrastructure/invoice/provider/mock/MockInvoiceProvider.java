package com.creditapi.infrastructure.invoice.provider.mock;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;
import com.creditapi.domain.invoice.provider.InvoiceProvider;
import java.time.LocalDateTime;

public class MockInvoiceProvider implements InvoiceProvider {

  @Override
  public InvoiceStatusResponseDTO consultInvoiceStatus(DownloadInvoiceRequestDTO request) {
    return new InvoiceStatusResponseDTO(
        "MOCK-STATUS", "Nota simulada com CNPJ: " + request.cnpj(), LocalDateTime.now());
  }

  @Override
  public byte[] downloadInvoice(DownloadInvoiceRequestDTO request) {
    return "PDF_MOCK".getBytes();
  }
}
