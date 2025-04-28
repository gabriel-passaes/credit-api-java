package com.creditapi.unit.domain.auth.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.creditapi.domain.auth.model.AuthToken;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthTokenTest {

  @Test
  @DisplayName("deve lançar NullPointerException se campos obrigatórios estiverem ausentes")
  void shouldThrowExceptionWhenRequiredFieldsAreMissing() {
    assertThatNullPointerException().isThrownBy(() -> AuthToken.builder().build());
  }

  @Test
  @DisplayName("deve retornar true quando token estiver expirado")
  void shouldReturnTrueIfTokenIsExpired() {
    Instant now = Instant.now();

    AuthToken expiredToken =
        AuthToken.builder()
            .userId(1L)
            .value("expired-session")
            .createdAt(now.minus(Duration.ofHours(1)))
            .expiresAt(now.minusSeconds(5))
            .authSource("EMAIL")
            .build();

    assertThat(expiredToken.isExpired()).isTrue();
  }

  @Test
  @DisplayName("deve retornar false quando token estiver válido")
  void shouldReturnFalseIfTokenIsStillValid() {
    Instant now = Instant.now();

    AuthToken validToken =
        AuthToken.builder()
            .userId(2L)
            .value("valid-session")
            .createdAt(now)
            .expiresAt(now.plus(Duration.ofMinutes(15)))
            .authSource("EMAIL")
            .build();

    assertThat(validToken.isExpired()).isFalse();
  }

  @Test
  @DisplayName(
      "invalidate deve retornar uma nova instância com invalidated=true mantendo o original intacto")
  void shouldReturnNewTokenWhenInvalidatedWithoutAffectingOriginal() {
    Instant now = Instant.now();

    AuthToken activeToken =
        AuthToken.builder()
            .userId(3L)
            .value("original-token")
            .createdAt(now)
            .expiresAt(now.plus(Duration.ofMinutes(30)))
            .authSource("EMAIL")
            .build();

    AuthToken invalidatedToken = activeToken.invalidate();

    assertThat(invalidatedToken.isInvalidated()).isTrue();
    assertThat(activeToken.isInvalidated()).isFalse();
    assertThat(invalidatedToken).isEqualTo(activeToken);
    assertThat(invalidatedToken).isNotSameAs(activeToken);
  }

  @Test
  @DisplayName("equals e hashCode devem considerar apenas o campo 'value'")
  void shouldUseOnlyValueForEqualityChecks() {
    Instant now = Instant.now();

    AuthToken firstToken =
        AuthToken.builder()
            .userId(100L)
            .value("refresh-token-shared")
            .createdAt(now)
            .expiresAt(now.plus(Duration.ofHours(1)))
            .authSource("SOCIAL")
            .build();

    AuthToken secondToken =
        AuthToken.builder()
            .userId(101L)
            .value("refresh-token-shared")
            .createdAt(now.minus(Duration.ofMinutes(10)))
            .expiresAt(now.plus(Duration.ofMinutes(40)))
            .authSource("EMAIL")
            .invalidated(true)
            .build();

    AuthToken uniqueToken =
        AuthToken.builder()
            .userId(102L)
            .value("unique-refresh-token")
            .createdAt(now)
            .expiresAt(now.plus(Duration.ofHours(1)))
            .authSource("REGISTER")
            .build();

    assertThat(firstToken).isEqualTo(secondToken);
    assertThat(firstToken.hashCode()).isEqualTo(secondToken.hashCode());

    assertThat(firstToken).isNotEqualTo(uniqueToken);
    assertThat(firstToken.hashCode()).isNotEqualTo(uniqueToken.hashCode());
  }

  @Test
  @DisplayName("authSource deve ser corretamente armazenado e recuperado")
  void shouldStoreAndRetrieveAuthSource() {
    AuthToken token =
        AuthToken.builder()
            .userId(500L)
            .value("test-token-authsource")
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(Duration.ofHours(1)))
            .authSource("REGISTER")
            .build();

    assertThat(token.getAuthSource()).isEqualTo("REGISTER");
  }
}
