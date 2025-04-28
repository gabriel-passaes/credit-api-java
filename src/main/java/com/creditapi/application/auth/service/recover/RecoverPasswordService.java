package com.creditapi.application.auth.service.recover;

import com.creditapi.application.auth.dto.request.RecoverPasswordRequestDTO;
import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;
import com.creditapi.application.auth.usecase.email.SendRecoverEmailUseCase;
import com.creditapi.application.auth.usecase.recover.RecoverPasswordUseCase;
import com.creditapi.domain.auth.exception.AuthUserNotFoundException;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RecoverPasswordService implements RecoverPasswordUseCase {

  private static final Logger log = LoggerFactory.getLogger(RecoverPasswordService.class);

  private final UserRepository userRepo;
  private final AuthTokenRepository tokenRepo;
  private final JwtTokenProvider jwtProvider;
  private final SendRecoverEmailUseCase sendRecover;

  public RecoverPasswordService(
      UserRepository userRepo,
      AuthTokenRepository tokenRepo,
      JwtTokenProvider jwtProvider,
      SendRecoverEmailUseCase sendRecover) {
    this.userRepo = userRepo;
    this.tokenRepo = tokenRepo;
    this.jwtProvider = jwtProvider;
    this.sendRecover = sendRecover;
  }

  @Override
  @RateLimiter(name = "authService")
  public PasswordRecoveryResponseDTO execute(RecoverPasswordRequestDTO dto) {
    log.info("Iniciando recuperação de senha para {}", dto.getEmail());

    User user =
        userRepo
            .findByEmail(dto.getEmail())
            .orElseThrow(() -> new AuthUserNotFoundException("Usuário não encontrado"));

    String recoveryToken = jwtProvider.generateRefreshTokenValue();
    AuthToken token =
        AuthToken.builder()
            .userId(user.getId())
            .value(recoveryToken)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(Duration.ofHours(1)))
            .authSource("RECOVER")
            .build();

    tokenRepo.save(token);

    PasswordRecoveryResponseDTO resp =
        sendRecover.execute(user.getEmail(), user.getName(), recoveryToken);

    log.info("Token de recuperação enviado para {}", dto.getEmail());
    return resp;
  }
}
