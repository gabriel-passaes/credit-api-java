package com.creditapi.infrastructure.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {

  private static final Logger logger = LoggerFactory.getLogger(ObservabilityConfig.class);

  private final MeterRegistry meterRegistry;

  @Value("${spring.profiles.active:default}")
  private String activeProfile;

  public ObservabilityConfig(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @PostConstruct
  public void configure() {
    logger.info("🔍 Inicializando configuração de observabilidade com Micrometer...");

    meterRegistry
        .config()
        .commonTags(
            List.of(Tag.of("application", "credit-api"), Tag.of("environment", activeProfile)));

    meterRegistry
        .config()
        .meterFilter(
            MeterFilter.maximumAllowableTags(
                "http.server.requests", "uri", 100, MeterFilter.deny()));

    meterRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("tomcat.sessions.rejected"));
    meterRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("jvm.threads.states"));
    meterRegistry
        .config()
        .meterFilter(MeterFilter.deny(id -> id.getName().contains("logback.events")));

    logger.info(
        "✅ ObservabilityConfig finalizada com tags: [application=credit-api, environment={}]",
        activeProfile);
  }
}
