package com.creditapi.unit.application.invoice.service.download;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.service.download.DownloadInvoiceService;
import com.creditapi.domain.invoice.exception.InvoiceNotFoundException;
import com.creditapi.domain.invoice.provider.InvoiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DownloadInvoiceServiceTest {

  @Mock private InvoiceProvider invoiceProvider;

  @InjectMocks private DownloadInvoiceService useCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve realizar download do PDF da nota com sucesso")
  void shouldDownloadInvoiceSuccessfully() {
    DownloadInvoiceRequestDTO request = new DownloadInvoiceRequestDTO("987654", "22334455000199");
    byte[] pdfMock = new byte[] {1, 2, 3};

    when(invoiceProvider.downloadInvoice(request)).thenReturn(pdfMock);

    byte[] result = useCase.execute(request);

    assertNotNull(result);
    assertEquals(3, result.length);
    verify(invoiceProvider, times(1)).downloadInvoice(request);
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar baixar nota inexistente")
  void shouldThrowWhenInvoiceDownloadFails() {
    DownloadInvoiceRequestDTO request = new DownloadInvoiceRequestDTO("000000", "00000000000000");

    when(invoiceProvider.downloadInvoice(request))
        .thenThrow(new InvoiceNotFoundException("Nota não encontrada"));

    assertThrows(InvoiceNotFoundException.class, () -> useCase.execute(request));
    verify(invoiceProvider, times(1)).downloadInvoice(request);
  }
}
