package com.creditapi.application.auth.service.recover;

import com.creditapi.application.auth.dto.request.ResetPasswordRequestDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.usecase.recover.ResetPasswordUseCase;
import com.creditapi.domain.auth.exception.AuthUserNotFoundException;
import com.creditapi.domain.auth.exception.RefreshTokenExpiredException;
import com.creditapi.domain.auth.exception.RefreshTokenInvalidException;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService implements ResetPasswordUseCase {

  private static final Logger log = LoggerFactory.getLogger(ResetPasswordService.class);

  private final AuthTokenRepository tokenRepo;
  private final UserRepository userRepo;
  private final PasswordEncoder encoder;
  private final JwtTokenProvider jwtProvider;
  private final AuthMapper mapper;

  public ResetPasswordService(
      AuthTokenRepository tokenRepo,
      UserRepository userRepo,
      PasswordEncoder encoder,
      JwtTokenProvider jwtProvider,
      AuthMapper mapper) {
    this.tokenRepo = tokenRepo;
    this.userRepo = userRepo;
    this.encoder = encoder;
    this.jwtProvider = jwtProvider;
    this.mapper = mapper;
  }

  @Override
  @RateLimiter(name = "authService")
  public RefreshResponseDTO execute(ResetPasswordRequestDTO dto) {
    log.info("Reset de senha solicitado com token={}", dto.getToken());

    AuthToken authToken =
        tokenRepo
            .findByValue(dto.getToken())
            .orElseThrow(() -> new RefreshTokenInvalidException("Token inválido"));

    if (authToken.isExpired()) {
      throw new RefreshTokenExpiredException("Token expirado");
    }

    User user =
        userRepo
            .findById(authToken.getUserId())
            .orElseThrow(() -> new AuthUserNotFoundException("Usuário não encontrado"));

    user.setPassword(encoder.encode(dto.getNewPassword()));
    userRepo.save(user);
    tokenRepo.invalidate(dto.getToken());

    String newAccess = jwtProvider.generateAccessToken(user);
    String newRefresh = jwtProvider.generateRefreshTokenValue();
    AuthToken freshToken =
        AuthToken.builder()
            .userId(user.getId())
            .value(newRefresh)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(jwtProvider.getRefreshTtl()))
            .authSource("RESET")
            .build();

    tokenRepo.save(freshToken);

    log.info("Senha redefinida para userId={}", user.getId());
    return mapper.toRefreshResponse(newAccess, newRefresh);
  }
}
