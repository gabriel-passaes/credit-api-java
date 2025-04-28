package com.creditapi.domain.auth.gateway.repository;

import com.creditapi.domain.auth.model.AuthToken;
import java.util.Optional;

public interface AuthTokenRepository {

  AuthToken save(AuthToken token);

  Optional<AuthToken> findByValue(String tokenValue);

  void invalidate(String tokenValue);

  void invalidateByUserId(Long userId);
}
