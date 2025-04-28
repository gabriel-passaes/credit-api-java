package com.creditapi.infrastructure.invoice.provider.config;

import com.creditapi.domain.invoice.provider.InvoiceProvider;
import com.creditapi.infrastructure.invoice.provider.tiranota.config.TiraNotaProperties;
import com.creditapi.infrastructure.invoice.provider.tiranota.implementation.TiraNotaInvoiceProvider;
import com.creditapi.infrastructure.shared.external.ApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "invoice.provider", name = "type", havingValue = "tiranota")
@EnableConfigurationProperties(TiraNotaProperties.class)
public class InvoiceProviderConfig {

  @Bean
  @ConditionalOnProperty(prefix = "invoice.provider", name = "type", havingValue = "tiranota")
  public InvoiceProvider tiraNotaProvider(ApiClient apiClient, TiraNotaProperties properties) {
    return new TiraNotaInvoiceProvider(apiClient, properties);
  }
}
