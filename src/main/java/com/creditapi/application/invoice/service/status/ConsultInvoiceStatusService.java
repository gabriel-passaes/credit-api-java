package com.creditapi.application.invoice.service.status;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;
import com.creditapi.application.invoice.usecase.status.ConsultInvoiceStatusUseCase;
import com.creditapi.domain.invoice.provider.InvoiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsultInvoiceStatusService implements ConsultInvoiceStatusUseCase {

  private static final Logger logger = LoggerFactory.getLogger(ConsultInvoiceStatusService.class);
  private final InvoiceProvider invoiceProvider;

  public ConsultInvoiceStatusService(InvoiceProvider invoiceProvider) {
    this.invoiceProvider = invoiceProvider;
  }

  @Override
  public InvoiceStatusResponseDTO execute(DownloadInvoiceRequestDTO request) {
    logger.info("Consultando status da nota fiscal: {}", request.invoiceNumber());
    return invoiceProvider.consultInvoiceStatus(request);
  }
}
