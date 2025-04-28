package com.creditapi.domain.invoice.provider;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;

public interface InvoiceProvider {

  InvoiceStatusResponseDTO consultInvoiceStatus(DownloadInvoiceRequestDTO request);

  byte[] downloadInvoice(DownloadInvoiceRequestDTO request);
}
