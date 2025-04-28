package com.creditapi.application.auth.service.login;

import com.creditapi.application.auth.dto.request.SocialLoginRequestDTO;
import com.creditapi.application.auth.dto.response.LoginResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.usecase.login.social.LoginWithSocialUseCase;
import com.creditapi.application.auth.usecase.login.social.SocialLoginStrategyFactory;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class LoginWithSocialService implements LoginWithSocialUseCase {

  private static final Logger log = LoggerFactory.getLogger(LoginWithSocialService.class);

  private final SocialLoginStrategyFactory factory;
  private final JwtTokenProvider jwtProvider;
  private final AuthTokenRepository tokenRepo;
  private final AuthMapper mapper;

  public LoginWithSocialService(
      SocialLoginStrategyFactory factory,
      JwtTokenProvider jwtProvider,
      AuthTokenRepository tokenRepo,
      AuthMapper mapper) {
    this.factory = factory;
    this.jwtProvider = jwtProvider;
    this.tokenRepo = tokenRepo;
    this.mapper = mapper;
  }

  @Override
  @RateLimiter(name = "authService")
  @CachePut(value = "accessTokens", key = "#result.userId")
  public LoginResponseDTO execute(SocialLoginRequestDTO dto) {
    log.info("Login social solicitado via provider={}", dto.getProvider());

    var strategy = factory.get(dto.getProvider());
    var user = strategy.authenticate(dto.getToken());

    String access = jwtProvider.generateAccessToken(user);
    String refresh = jwtProvider.generateRefreshTokenValue();

    tokenRepo.save(
        AuthToken.builder()
            .userId(user.getId())
            .value(refresh)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(jwtProvider.getRefreshTtl()))
            .invalidated(false)
            .authSource(dto.getProvider().name())
            .build());

    log.info("Login social bem-sucedido para userId={}", user.getId());
    return mapper.toLoginResponse(user, access, refresh);
  }
}
