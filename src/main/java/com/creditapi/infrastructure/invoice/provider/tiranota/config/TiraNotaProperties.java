package com.creditapi.infrastructure.invoice.provider.tiranota.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "invoice.provider.tiranota")
public class TiraNotaProperties {

  private String baseUrl;
  private String apiKey;
  private int timeout;
}
