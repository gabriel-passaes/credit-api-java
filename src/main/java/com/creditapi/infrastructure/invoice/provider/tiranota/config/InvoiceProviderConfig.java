package com.creditapi.infrastructure.invoice.provider.tiranota.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.creditapi.domain.invoice.provider.InvoiceProvider;
import com.creditapi.infrastructure.invoice.provider.tiranota.implementation.TiraNotaInvoiceProvider;
import com.creditapi.infrastructure.shared.external.ApiClient;

@Configuration
@EnableConfigurationProperties(TiraNotaProperties.class)
public class InvoiceProviderConfig {

  @Bean
  public InvoiceProvider invoiceProvider(ApiClient apiClient, TiraNotaProperties tiraNotaProperties) {
    return new TiraNotaInvoiceProvider(apiClient, tiraNotaProperties);
  }
}
