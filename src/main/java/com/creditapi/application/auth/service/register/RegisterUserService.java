package com.creditapi.application.auth.service.register;

import com.creditapi.application.auth.dto.request.RegisterRequestDTO;
import com.creditapi.application.auth.dto.response.RegisterResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.usecase.email.SendWelcomeEmailUseCase;
import com.creditapi.application.auth.usecase.register.RegisterUserUseCase;
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
public class RegisterUserService implements RegisterUserUseCase {

  private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);

  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtProvider;
  private final AuthTokenRepository tokenRepo;
  private final SendWelcomeEmailUseCase welcomeEmail;
  private final AuthMapper mapper;

  public RegisterUserService(
      UserRepository userRepo,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtProvider,
      AuthTokenRepository tokenRepo,
      SendWelcomeEmailUseCase welcomeEmail,
      AuthMapper mapper) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
    this.jwtProvider = jwtProvider;
    this.tokenRepo = tokenRepo;
    this.welcomeEmail = welcomeEmail;
    this.mapper = mapper;
  }

  @Override
  @RateLimiter(name = "authService")
  @CachePut(value = "accessTokens", key = "#result.userId")
  public RegisterResponseDTO execute(RegisterRequestDTO req) {
    log.info("Iniciando registro para e-mail={}", req.getEmail());

    User user =
        userRepo.save(
            User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build());

    String access = jwtProvider.generateAccessToken(user);
    String refresh = jwtProvider.generateRefreshTokenValue();

    tokenRepo.save(
        AuthToken.builder()
            .userId(user.getId())
            .value(refresh)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(jwtProvider.getRefreshTtl()))
            .authSource("REGISTER")
            .build());

    welcomeEmail.execute(user.getEmail(), user.getName());
    log.info("Registro concluído para userId={}", user.getId());

    return mapper.toRegisterResponse(user, access, refresh);
  }
}
