package com.creditapi.unit.infrastructure.invoice.provider.tiranota.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.domain.invoice.exception.InvoiceNotFoundException;
import com.creditapi.infrastructure.invoice.provider.dto.ExternalInvoiceStatusDTO;
import com.creditapi.infrastructure.invoice.provider.tiranota.config.TiraNotaProperties;
import com.creditapi.infrastructure.invoice.provider.tiranota.implementation.TiraNotaInvoiceProvider;
import com.creditapi.infrastructure.shared.external.ApiClient;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

class TiraNotaInvoiceProviderTest {

  private ApiClient apiClient;
  private TiraNotaProperties properties;
  private TiraNotaInvoiceProvider provider;

  @BeforeEach
  void setUp() {
    apiClient = Mockito.mock(ApiClient.class);
    properties = new TiraNotaProperties();
    properties.setBaseUrl("https://api.tiranota.com.br");
    properties.setApiKey("fake-key");
    provider = new TiraNotaInvoiceProvider(apiClient, properties);
  }

  @Test
  @DisplayName("Deve consultar status e mapear corretamente")
  void shouldMapInvoiceStatusCorrectly() {
    var dto = new ExternalInvoiceStatusDTO();
    dto.setStatus("APROVADA");
    dto.setMessage("Nota processada");
    dto.setLastUpdated(LocalDateTime.of(2024, 4, 20, 12, 0));

    when(apiClient.exchange(any(), any(), any(), any(), eq(ExternalInvoiceStatusDTO.class)))
        .thenReturn(ResponseEntity.ok(dto));

    var result = provider.consultInvoiceStatus(new DownloadInvoiceRequestDTO("123", "456"));

    assertEquals("APROVADA", result.status());
    assertEquals("Nota processada", result.message());
    assertEquals(LocalDateTime.of(2024, 4, 20, 12, 0), result.lastUpdated());
  }

  @Test
  @DisplayName("Deve lançar exceção se status vier vazio")
  void shouldThrowIfStatusResponseIsNull() {
    when(apiClient.exchange(any(), any(), any(), any(), eq(ExternalInvoiceStatusDTO.class)))
        .thenReturn(ResponseEntity.ok(null));

    var request = new DownloadInvoiceRequestDTO("123", "456");

    assertThrows(InvoiceNotFoundException.class, () -> provider.consultInvoiceStatus(request));
  }

  @Test
  @DisplayName("Deve fazer download do PDF com sucesso")
  void shouldDownloadPdfSuccessfully() {
    var pdf = new byte[] {1, 2, 3};

    when(apiClient.exchange(any(), eq(HttpMethod.GET), any(), any(), eq(byte[].class)))
        .thenReturn(ResponseEntity.ok(pdf));

    var result = provider.downloadInvoice(new DownloadInvoiceRequestDTO("123", "456"));

    assertNotNull(result);
    assertEquals(3, result.length);
  }

  @Test
  @DisplayName("Deve lançar exceção se o PDF estiver vazio")
  void shouldThrowIfPdfIsEmpty() {
    when(apiClient.exchange(any(), eq(HttpMethod.GET), any(), any(), eq(byte[].class)))
        .thenReturn(ResponseEntity.ok(new byte[0]));

    var request = new DownloadInvoiceRequestDTO("123", "456");

    assertThrows(InvoiceNotFoundException.class, () -> provider.downloadInvoice(request));
  }

  @Test
  @DisplayName("Deve lançar exceção se o PDF for nulo")
  void shouldThrowIfPdfIsNull() {
    when(apiClient.exchange(any(), eq(HttpMethod.GET), any(), any(), eq(byte[].class)))
        .thenReturn(ResponseEntity.ok(null));

    var request = new DownloadInvoiceRequestDTO("123", "456");

    assertThrows(InvoiceNotFoundException.class, () -> provider.downloadInvoice(request));
  }
}
