package com.creditapi.unit.infrastructure.invoice.provider.tiranota.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.creditapi.infrastructure.invoice.provider.tiranota.config.TiraNotaProperties;

@SpringBootTest
@ActiveProfiles("test")
class TiraNotaIntegrationTest {

  @Autowired
  private TiraNotaProperties props;

  @Test
  void shouldLoadPropertiesFromApplicationYaml() {
    assertNotNull(props.getApiKey());
    assertNotNull(props.getBaseUrl());
  }
}
