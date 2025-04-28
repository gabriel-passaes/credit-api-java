package com.creditapi.infrastructure.auth.persistence.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.infrastructure.auth.persistence.entity.AuthTokenEntity;

@Repository
public class AuthTokenRepositoryImpl implements AuthTokenRepository {

  private final AuthTokenJpaRepository jpa;

  public AuthTokenRepositoryImpl(AuthTokenJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  @Transactional
  public AuthToken save(AuthToken token) {
    AuthTokenEntity entity = toEntity(token);
    AuthTokenEntity saved = jpa.save(entity);
    return toDomain(saved);
  }

  @Override
  public Optional<AuthToken> findByValue(String tokenValue) {
    return jpa.findByValue(tokenValue).map(this::toDomain);
  }

  @Override
  @Transactional
  public void invalidate(String tokenValue) {
    jpa.invalidateByValue(tokenValue);
  }

  @Override
  @Transactional
  public void invalidateByUserId(Long userId) {
    jpa.invalidateByUserId(userId);
  }

  private AuthToken toDomain(AuthTokenEntity entity) {
    return AuthToken.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .value(entity.getValue())
        .createdAt(entity.getCreatedAt().toInstant(ZoneOffset.UTC)) // Conversão correta LocalDateTime -> Instant
        .expiresAt(entity.getExpiresAt().toInstant(ZoneOffset.UTC)) // Conversão correta LocalDateTime -> Instant
        .invalidated(entity.isInvalidated())
        .authSource(entity.getAuthSource())
        .build();
  }

  private AuthTokenEntity toEntity(AuthToken token) {
    AuthTokenEntity entity = new AuthTokenEntity();
    entity.setId(token.getId());
    entity.setUserId(token.getUserId());
    entity.setValue(token.getValue());
    entity.setCreatedAt(LocalDateTime.ofInstant(token.getCreatedAt(), ZoneOffset.UTC)); // Conversão correta Instant -> LocalDateTime
    entity.setExpiresAt(LocalDateTime.ofInstant(token.getExpiresAt(), ZoneOffset.UTC)); // Conversão correta Instant -> LocalDateTime
    entity.setInvalidated(token.isInvalidated());
    entity.setAuthSource(token.getAuthSource());
    return entity;
  }
}
