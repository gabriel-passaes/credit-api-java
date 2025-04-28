package com.creditapi.unit.application.invoice.service.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;
import com.creditapi.application.invoice.service.status.ConsultInvoiceStatusService;
import com.creditapi.domain.invoice.exception.InvoiceNotFoundException;
import com.creditapi.domain.invoice.provider.InvoiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ConsultInvoiceStatusServiceTest {

  @Mock private InvoiceProvider invoiceProvider;

  @InjectMocks private ConsultInvoiceStatusService useCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve retornar status da nota com sucesso")
  void shouldReturnInvoiceStatusSuccessfully() {
    DownloadInvoiceRequestDTO request = new DownloadInvoiceRequestDTO("123456", "12345678000100");

    InvoiceStatusResponseDTO expected =
        new InvoiceStatusResponseDTO("APROVADA", "Nota aprovada com sucesso", null);

    when(invoiceProvider.consultInvoiceStatus(request)).thenReturn(expected);

    InvoiceStatusResponseDTO response = useCase.execute(request);

    assertEquals("APROVADA", response.status());
    assertEquals("Nota aprovada com sucesso", response.message());
    verify(invoiceProvider, times(1)).consultInvoiceStatus(request);
  }

  @Test
  @DisplayName("Deve lançar exceção ao consultar nota inexistente")
  void shouldThrowWhenInvoiceNotFound() {
    DownloadInvoiceRequestDTO request = new DownloadInvoiceRequestDTO("000000", "00000000000000");

    when(invoiceProvider.consultInvoiceStatus(request))
        .thenThrow(new InvoiceNotFoundException("Nota não encontrada"));

    assertThrows(InvoiceNotFoundException.class, () -> useCase.execute(request));
    verify(invoiceProvider, times(1)).consultInvoiceStatus(request);
  }
}
