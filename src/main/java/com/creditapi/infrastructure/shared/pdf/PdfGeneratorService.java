package com.creditapi.infrastructure.shared.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PdfGeneratorService {

  private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorService.class);

  public byte[] generateFromHtml(String html) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.withHtmlContent(html, null);
      builder.toStream(outputStream);
      builder.run();

      logger.info("✅ PDF gerado com sucesso ({} bytes)", outputStream.size());
      return outputStream.toByteArray();
    } catch (Exception e) {
      logger.error("❌ Erro ao gerar PDF a partir de HTML", e);
      throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
    }
  }
}
