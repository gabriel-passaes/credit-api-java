package com.creditapi.infrastructure.invoice.provider.tiranota.client;

import com.creditapi.infrastructure.invoice.provider.dto.ExternalInvoiceStatusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1/invoices")
public interface TiraNotaClient {

  @GetExchange("/status")
  ResponseEntity<ExternalInvoiceStatusDTO> getInvoiceStatus(
      @RequestParam("document") String document,
      @RequestParam("number") String number,
      @RequestParam("series") String series,
      @RequestParam("year") String year);
}
