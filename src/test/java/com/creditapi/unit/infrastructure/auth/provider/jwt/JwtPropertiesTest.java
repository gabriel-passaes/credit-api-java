package com.creditapi.unit.infrastructure.auth.provider.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.creditapi.infrastructure.auth.provider.jwt.JwtProperties;

class JwtPropertiesTest {

  @Test
  @DisplayName("deve configurar propriedades corretamente")
  void shouldSetPropertiesCorrectly() {
    JwtProperties props = new JwtProperties();
    props.setSecret("abc123");
    props.setAccessTokenTtl(Duration.ofMinutes(15));
    props.setRefreshTokenTtl(Duration.ofMinutes(45));

    assertThat(props.getSecret()).isEqualTo("abc123");
    assertThat(props.getAccessTokenTtl()).isEqualTo(Duration.ofMinutes(15));
    assertThat(props.getRefreshTokenTtl()).isEqualTo(Duration.ofMinutes(45));
  }
}
