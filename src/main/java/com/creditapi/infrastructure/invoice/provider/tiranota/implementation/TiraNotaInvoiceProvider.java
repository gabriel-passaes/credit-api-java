package com.creditapi.infrastructure.invoice.provider.tiranota.implementation;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;
import com.creditapi.domain.invoice.exception.InvoiceNotFoundException;
import com.creditapi.domain.invoice.provider.InvoiceProvider;
import com.creditapi.infrastructure.invoice.provider.dto.ExternalInvoiceStatusDTO;
import com.creditapi.infrastructure.invoice.provider.tiranota.config.TiraNotaProperties;
import com.creditapi.infrastructure.shared.external.ApiClient;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class TiraNotaInvoiceProvider implements InvoiceProvider {

  private static final Logger logger = LoggerFactory.getLogger(TiraNotaInvoiceProvider.class);

  private final ApiClient apiClient;
  private final TiraNotaProperties properties;

  public TiraNotaInvoiceProvider(ApiClient apiClient, TiraNotaProperties properties) {
    this.apiClient = apiClient;
    this.properties = properties;
  }

  @Override
  public InvoiceStatusResponseDTO consultInvoiceStatus(DownloadInvoiceRequestDTO request) {
    try {
      logger.info("Consultando status da nota para CNPJ {}", request.cnpj());

      String url = properties.getBaseUrl() + "/status/" + request.cnpj();

      ResponseEntity<ExternalInvoiceStatusDTO> response =
          apiClient.exchange(
              url, HttpMethod.GET, defaultHeaders(), null, ExternalInvoiceStatusDTO.class);

      ExternalInvoiceStatusDTO body = response.getBody();
      if (body == null) {
        throw new InvoiceNotFoundException("Resposta da TiraNota veio vazia");
      }

      return new InvoiceStatusResponseDTO(
          body.getStatus(), body.getMessage(), body.getLastUpdated());

    } catch (Exception e) {
      logger.error("Erro ao consultar status da nota fiscal via TiraNota", e);
      throw new InvoiceNotFoundException("Falha ao consultar nota fiscal: " + e.getMessage(), e);
    }
  }

  @Override
  public byte[] downloadInvoice(DownloadInvoiceRequestDTO request) {
    try {
      logger.info("Fazendo download da nota fiscal {} via TiraNota", request.invoiceNumber());

      String url = properties.getBaseUrl() + "/invoice/" + request.invoiceNumber() + "/download";

      ResponseEntity<byte[]> response =
          apiClient.exchange(url, HttpMethod.GET, defaultHeaders(), null, byte[].class);

      byte[] pdf = response.getBody();
      if (pdf == null || pdf.length == 0) {
        throw new InvoiceNotFoundException("PDF da nota fiscal não encontrado.");
      }

      return pdf;

    } catch (Exception e) {
      logger.error("Erro ao realizar download da nota fiscal via TiraNota", e);
      throw new InvoiceNotFoundException("Falha ao baixar nota fiscal: " + e.getMessage(), e);
    }
  }

  private Map<String, String> defaultHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
    headers.put("x-api-key", properties.getApiKey());
    return headers;
  }
}
