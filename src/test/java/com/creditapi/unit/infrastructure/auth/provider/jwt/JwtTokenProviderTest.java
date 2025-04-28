package com.creditapi.unit.infrastructure.auth.provider.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtProperties;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;

import io.jsonwebtoken.JwtException;

class JwtTokenProviderTest {

  @Test
  @DisplayName("deve gerar e validar token JWT com sucesso")
  void shouldGenerateAndParseJwtSuccessfully() {
    JwtTokenProvider provider = createProvider();

    User user = User.builder()
        .id(1L)
        .email("test@example.com")
        .name("Tester")
        .password("123456")
        .build();

    String jwt = provider.generateAccessToken(user);

    var parsed = provider.parse(jwt);

    assertThat(parsed.getBody().getSubject()).isEqualTo("1");
    assertThat(parsed.getBody().get("email")).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("deve lançar exceção ao tentar parsear token inválido")
  void shouldThrowExceptionForInvalidJwt() {
    JwtTokenProvider provider = createProvider();
    assertThrows(JwtException.class, () -> provider.parse("invalid.token.here"));
  }

  @Test
  @DisplayName("deve gerar token de refresh com valor seguro")
  void shouldGenerateSecureRefreshToken() {
    JwtTokenProvider provider = createProvider();

    String refresh = provider.generateRefreshTokenValue();

    assertThat(refresh).isNotNull().hasSizeGreaterThan(30);
  }

  private JwtTokenProvider createProvider() {
    JwtProperties props = new JwtProperties();
    props.setRefreshTokenTtl(Duration.ofMinutes(30));
    props.setSecret("mysupersecretkey1234567890mysupersecretkey1234567890");
    props.setAccessTokenTtl(Duration.ofMinutes(60));
    return new JwtTokenProvider(props);
  }
}
