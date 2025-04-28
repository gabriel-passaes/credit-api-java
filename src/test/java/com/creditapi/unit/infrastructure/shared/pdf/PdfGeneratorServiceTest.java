package com.creditapi.unit.infrastructure.shared.pdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.creditapi.infrastructure.shared.pdf.PdfGeneratorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PdfGeneratorServiceTest {

  private final PdfGeneratorService service = new PdfGeneratorService();

  @Test
  @DisplayName("Deve gerar PDF a partir de HTML válido")
  void shouldGeneratePdfFromValidHtml() {
    String html = "<html><body><h1>PDF de Teste</h1></body></html>";

    byte[] pdf = service.generateFromHtml(html);

    assertThat(pdf).isNotNull();
    assertThat(pdf.length).isGreaterThan(0);
  }

  @Test
  @DisplayName("Deve lançar exceção se HTML for nulo")
  void shouldThrowExceptionWhenHtmlIsNull() {
    assertThrows(RuntimeException.class, () -> service.generateFromHtml(null));
  }
}
