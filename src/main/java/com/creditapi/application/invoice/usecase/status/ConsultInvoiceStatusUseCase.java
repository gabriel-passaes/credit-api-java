package com.creditapi.application.invoice.usecase.status;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;

public interface ConsultInvoiceStatusUseCase {
  InvoiceStatusResponseDTO execute(DownloadInvoiceRequestDTO request);
}
