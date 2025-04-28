package com.creditapi.unit.infrastructure.auth.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.infrastructure.auth.persistence.entity.AuthTokenEntity;
import com.creditapi.infrastructure.auth.persistence.repository.AuthTokenJpaRepository;
import com.creditapi.infrastructure.auth.persistence.repository.AuthTokenRepositoryImpl;

class AuthTokenRepositoryImplTest {

  private final AuthTokenJpaRepository jpa = mock(AuthTokenJpaRepository.class);
  private final AuthTokenRepositoryImpl repository = new AuthTokenRepositoryImpl(jpa);

  @Test
  @DisplayName("deve salvar e retornar token corretamente")
  void shouldSaveAndReturnToken() {
    AuthToken token =
        AuthToken.builder()
            .userId(1L)
            .value("token-abc")
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .authSource("TEST")
            .build();

    AuthTokenEntity savedEntity = new AuthTokenEntity();
    savedEntity.setId(100L);
    savedEntity.setUserId(token.getUserId());
    savedEntity.setValue(token.getValue());
    savedEntity.setCreatedAt(LocalDateTime.ofInstant(token.getCreatedAt(), ZoneOffset.UTC)); // Corrigido
    savedEntity.setExpiresAt(LocalDateTime.ofInstant(token.getExpiresAt(), ZoneOffset.UTC)); // Corrigido
    savedEntity.setInvalidated(false);
    savedEntity.setAuthSource(token.getAuthSource());

    when(jpa.save(any(AuthTokenEntity.class))).thenReturn(savedEntity);

    AuthToken result = repository.save(token);

    assertThat(result.getId()).isEqualTo(100L);
    assertThat(result.getUserId()).isEqualTo(token.getUserId());
    assertThat(result.getValue()).isEqualTo(token.getValue());
    assertThat(result.getAuthSource()).isEqualTo(token.getAuthSource());
  }

  @Test
  @DisplayName("deve retornar Optional.empty quando token não for encontrado")
  void shouldReturnEmptyIfTokenNotFound() {
    when(jpa.findByValue("abc")).thenReturn(Optional.empty());

    Optional<AuthToken> result = repository.findByValue("abc");

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("deve invalidar token por valor")
  void shouldInvalidateTokenByValue() {
    repository.invalidate("xyz");
    verify(jpa).invalidateByValue("xyz");
  }

  @Test
  @DisplayName("deve invalidar todos os tokens do usuário")
  void shouldInvalidateAllTokensByUserId() {
    repository.invalidateByUserId(33L);
    verify(jpa).invalidateByUserId(33L);
  }
}
