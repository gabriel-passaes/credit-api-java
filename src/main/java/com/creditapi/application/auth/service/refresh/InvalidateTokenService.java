package com.creditapi.application.auth.service.refresh;

import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;
import com.creditapi.application.auth.usecase.refresh.InvalidateTokenUseCase;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InvalidateTokenService implements InvalidateTokenUseCase {

  private static final Logger log = LoggerFactory.getLogger(InvalidateTokenService.class);

  private final AuthTokenRepository tokenRepo;

  public InvalidateTokenService(AuthTokenRepository tokenRepo) {
    this.tokenRepo = tokenRepo;
  }

  @Override
  @RateLimiter(name = "authService")
  public void execute(RefreshTokenRequestDTO dto) {
    log.info("Invalidando refresh token: {}", dto.getRefreshToken());
    tokenRepo.invalidate(dto.getRefreshToken());
  }

  @Override
  @RateLimiter(name = "authService")
  public void executeAll(Long userId) {
    log.info("Invalidando todos os refresh tokens do userId={}", userId);
    tokenRepo.invalidateByUserId(userId);
  }
}
