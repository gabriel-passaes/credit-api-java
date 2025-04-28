package com.creditapi.application.invoice.usecase.download;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;

public interface DownloadInvoiceUseCase {
  byte[] execute(DownloadInvoiceRequestDTO request);
}
