package com.creditapi.application.invoice.service.download;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.usecase.download.DownloadInvoiceUseCase;
import com.creditapi.domain.invoice.provider.InvoiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DownloadInvoiceService implements DownloadInvoiceUseCase {

  private static final Logger logger = LoggerFactory.getLogger(DownloadInvoiceService.class);
  private final InvoiceProvider invoiceProvider;

  public DownloadInvoiceService(InvoiceProvider invoiceProvider) {
    this.invoiceProvider = invoiceProvider;
  }

  @Override
  public byte[] execute(DownloadInvoiceRequestDTO request) {
    logger.info("Executando download da nota: {}", request.invoiceNumber());
    return invoiceProvider.downloadInvoice(request);
  }
}
