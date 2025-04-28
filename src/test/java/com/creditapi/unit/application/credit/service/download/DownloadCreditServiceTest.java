package com.creditapi.unit.application.credit.service.download;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.service.download.DownloadCreditInvoiceService;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.shared.pdf.PdfGeneratorService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes para DownloadCreditInvoiceService")
class DownloadCreditServiceTest {

  private CreditRepository creditRepository;
  private PdfGeneratorService pdfGenerator;
  private DownloadCreditInvoiceService downloadCreditInvoiceService;

  @BeforeEach
  void setup() {
    creditRepository = mock(CreditRepository.class);
    pdfGenerator = mock(PdfGeneratorService.class);
    downloadCreditInvoiceService = new DownloadCreditInvoiceService(creditRepository, pdfGenerator);
  }

  private Credit buildMockCredit(Long id, String creditNumber, String nfseNumber) {
    return Credit.builder()
        .id(id)
        .creditNumber(creditNumber)
        .nfseNumber(nfseNumber)
        .constitutionDate(LocalDate.now())
        .issqnAmount(BigDecimal.TEN)
        .creditType("ISSQN")
        .simpleNational(false)
        .rate(BigDecimal.ONE)
        .billedAmount(BigDecimal.valueOf(1000))
        .deductionAmount(BigDecimal.ZERO)
        .calculationBase(BigDecimal.valueOf(1000))
        .user(User.builder().id(99L).name("Test User").email("test@email.com").build())
        .build();
  }

  @Test
  @DisplayName("Deve gerar PDF com sucesso para crédito existente")
  void shouldGeneratePdfSuccessfully_whenCreditExists() {
    String creditNumber = "CREDIT001";
    Credit credit = buildMockCredit(1L, creditNumber, "NF001");

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(pdfGenerator.generateFromHtml(anyString())).thenReturn("mock-pdf".getBytes());

    byte[] result = downloadCreditInvoiceService.execute(creditNumber);

    assertNotNull(result);
    assertEquals("mock-pdf", new String(result));
  }

  @Test
  @DisplayName("Deve lançar CreditNotFoundException se crédito não for encontrado")
  void shouldThrowCreditNotFoundException_whenCreditDoesNotExist() {
    when(creditRepository.findByCreditNumber("INVALID")).thenReturn(Optional.empty());

    assertThrows(
        CreditNotFoundException.class, () -> downloadCreditInvoiceService.execute("INVALID"));
  }

  @Test
  @DisplayName("Deve lançar RuntimeException se PDF Generator falhar")
  void shouldThrowException_whenPdfGeneratorFails() {
    String creditNumber = "CREDIT002";
    Credit credit = buildMockCredit(2L, creditNumber, "NF002");

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(pdfGenerator.generateFromHtml(anyString()))
        .thenThrow(new RuntimeException("Erro ao gerar PDF"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> downloadCreditInvoiceService.execute(creditNumber));

    assertEquals("Erro ao gerar PDF", exception.getMessage());
  }

  @Test
  @DisplayName("Deve retornar array vazio se geração de PDF não produzir conteúdo")
  void shouldReturnEmptyArray_whenPdfGeneratorReturnsEmpty() {
    String creditNumber = "CREDIT003";
    Credit credit = buildMockCredit(3L, creditNumber, "NF003");

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(pdfGenerator.generateFromHtml(anyString())).thenReturn(new byte[0]);

    byte[] result = downloadCreditInvoiceService.execute(creditNumber);

    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  @DisplayName("Deve evitar chamada ao PDF Generator se crédito não existir")
  void shouldNotCallPdfGenerator_whenCreditNotFound() {
    when(creditRepository.findByCreditNumber("MISSING")).thenReturn(Optional.empty());

    assertThrows(
        CreditNotFoundException.class, () -> downloadCreditInvoiceService.execute("MISSING"));

    verify(pdfGenerator, never()).generateFromHtml(anyString());
  }
}
