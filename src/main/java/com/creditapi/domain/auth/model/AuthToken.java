package com.creditapi.domain.auth.model;

import java.time.Instant;
import java.util.Objects;

public class AuthToken {

  private final Long id;
  private final Long userId;
  private final String token_value;
  private final Instant expiresAt;
  private final Instant createdAt;
  private final boolean invalidated;
  private final String authSource;

  private AuthToken(Builder builder) {
    this.id = builder.id;
    this.userId = Objects.requireNonNull(builder.userId, "userId é obrigatório");
    this.token_value = Objects.requireNonNull(builder.value, "token_value é obrigatório");
    this.expiresAt = Objects.requireNonNull(builder.expiresAt, "expiresAt é obrigatório");
    this.createdAt = Objects.requireNonNull(builder.createdAt, "createdAt é obrigatório");
    this.invalidated = builder.invalidated;
    this.authSource = builder.authSource;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public String getValue() {
    return token_value;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public boolean isInvalidated() {
    return invalidated;
  }

  public String getAuthSource() {
    return authSource;
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiresAt);
  }

  public AuthToken invalidate() {
    return new Builder(this).invalidated(true).build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private Long id;
    private Long userId;
    private String value;
    private Instant expiresAt;
    private Instant createdAt;
    private boolean invalidated = false;
    private String authSource;

    private Builder() {}

    private Builder(AuthToken copy) {
      this.id = copy.id;
      this.userId = copy.userId;
      this.value = copy.token_value;
      this.expiresAt = copy.expiresAt;
      this.createdAt = copy.createdAt;
      this.invalidated = copy.invalidated;
      this.authSource = copy.authSource;
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public Builder value(String value) {
      this.value = value;
      return this;
    }

    public Builder expiresAt(Instant expiresAt) {
      this.expiresAt = expiresAt;
      return this;
    }

    public Builder createdAt(Instant createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder invalidated(boolean invalid) {
      this.invalidated = invalid;
      return this;
    }

    public Builder authSource(String authSource) {
      this.authSource = authSource;
      return this;
    }

    public AuthToken build() {
      return new AuthToken(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AuthToken that)) return false;
    return Objects.equals(token_value, that.token_value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token_value);
  }
}
