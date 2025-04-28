package com.creditapi.application.credit.service.download;

import com.creditapi.application.credit.usecase.download.DownloadCreditInvoiceUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.infrastructure.shared.pdf.PdfGeneratorService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DownloadCreditInvoiceService implements DownloadCreditInvoiceUseCase {

  private static final Logger logger = LoggerFactory.getLogger(DownloadCreditInvoiceService.class);

  private final CreditRepository creditRepository;
  private final PdfGeneratorService pdfGenerator;

  public DownloadCreditInvoiceService(
      CreditRepository creditRepository, PdfGeneratorService pdfGenerator) {
    this.creditRepository = creditRepository;
    this.pdfGenerator = pdfGenerator;
  }

  @Override
  @Observed(name = "credit.download-invoice")
  public byte[] execute(String creditNumber) {
    logger.info("📎 Iniciando geração de PDF da nota fiscal para crédito {}", creditNumber);

    Credit credit =
        creditRepository
            .findByCreditNumber(creditNumber)
            .orElseThrow(
                () -> {
                  logger.warn("Crédito não encontrado: {}", creditNumber);
                  return new CreditNotFoundException("Crédito não encontrado: " + creditNumber);
                });

    String html =
        com.creditapi.infrastructure.credit.pdf.template.CreditHtmlTemplateBuilder.buildHtml(
            credit);
    byte[] pdfBytes = pdfGenerator.generateFromHtml(html);

    logger.info("✅ PDF da nota fiscal gerado com sucesso para crédito {}", creditNumber);
    return pdfBytes;
  }
}
