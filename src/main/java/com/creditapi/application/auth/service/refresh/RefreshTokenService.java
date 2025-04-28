package com.creditapi.application.auth.service.refresh;

import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.usecase.refresh.RefreshTokenUseCase;
import com.creditapi.domain.auth.exception.RefreshTokenExpiredException;
import com.creditapi.domain.auth.exception.RefreshTokenInvalidException;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

  private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

  private final AuthTokenRepository tokenRepo;
  private final JwtTokenProvider jwtProvider;
  private final UserRepository userRepo;
  private final AuthMapper mapper;

  public RefreshTokenService(
      AuthTokenRepository tokenRepo,
      JwtTokenProvider jwtProvider,
      UserRepository userRepo,
      AuthMapper mapper) {
    this.tokenRepo = tokenRepo;
    this.jwtProvider = jwtProvider;
    this.userRepo = userRepo;
    this.mapper = mapper;
  }

  @Override
  @RateLimiter(name = "authService")
  public RefreshResponseDTO execute(RefreshTokenRequestDTO dto) {
    log.info("Refresh de token solicitado: {}", dto.getRefreshToken());

    AuthToken oldToken =
        tokenRepo
            .findByValue(dto.getRefreshToken())
            .orElseThrow(() -> new RefreshTokenInvalidException("Refresh token inválido"));

    if (oldToken.isExpired()) {
      throw new RefreshTokenExpiredException("Refresh token expirado");
    }

    var user =
        userRepo
            .findById(oldToken.getUserId())
            .orElseThrow(
                () ->
                    new com.creditapi.domain.auth.exception.AuthUserNotFoundException(
                        "Usuário não encontrado"));

    tokenRepo.invalidate(dto.getRefreshToken());

    String newAccess = jwtProvider.generateAccessToken(user);
    String newRefresh = jwtProvider.generateRefreshTokenValue();

    tokenRepo.save(
        AuthToken.builder()
            .userId(user.getId())
            .value(newRefresh)
            .authSource("REFRESH")
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(jwtProvider.getRefreshTtl()))
            .build());

    log.info("Tokens renovados para userId={}", user.getId());
    return mapper.toRefreshResponse(newAccess, newRefresh);
  }
}
