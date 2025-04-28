package com.creditapi.application.auth.service.login;

import com.creditapi.application.auth.dto.request.LoginRequestDTO;
import com.creditapi.application.auth.dto.response.LoginResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.usecase.login.LoginUserUseCase;
import com.creditapi.domain.auth.exception.InvalidCredentialsException;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService implements LoginUserUseCase {

  private static final Logger log = LoggerFactory.getLogger(LoginUserService.class);

  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtProvider;
  private final AuthTokenRepository tokenRepo;
  private final AuthMapper mapper;

  public LoginUserService(
      UserRepository userRepo,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtProvider,
      AuthTokenRepository tokenRepo,
      AuthMapper mapper) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
    this.jwtProvider = jwtProvider;
    this.tokenRepo = tokenRepo;
    this.mapper = mapper;
  }

  @Override
  @RateLimiter(name = "authService")
  @CachePut(value = "accessTokens", key = "#result.userId")
  public LoginResponseDTO execute(LoginRequestDTO dto) {
    log.info("Login solicitado para e-mail={}", dto.getEmail());

    User user =
        userRepo
            .findByEmail(dto.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new InvalidCredentialsException("Credenciais inválidas");
    }

    String access = jwtProvider.generateAccessToken(user);
    String refresh = jwtProvider.generateRefreshTokenValue();

    tokenRepo.save(
        AuthToken.builder()
            .userId(user.getId())
            .value(refresh)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(jwtProvider.getRefreshTtl()))
            .invalidated(false)
            .authSource("EMAIL")
            .build());

    log.info("Login bem-sucedido para userId={}", user.getId());
    return mapper.toLoginResponse(user, access, refresh);
  }
}
