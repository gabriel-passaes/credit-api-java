package com.creditapi.infrastructure.auth.provider.social.strategy;

import com.creditapi.application.auth.usecase.login.social.SocialLoginStrategy;
import com.creditapi.domain.auth.exception.SocialLoginException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.social.SocialProvider;
import com.creditapi.infrastructure.auth.provider.social.verifier.GoogleTokenVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GoogleLoginStrategy implements SocialLoginStrategy {

  private static final Logger log = LoggerFactory.getLogger(GoogleLoginStrategy.class);

  private final GoogleTokenVerifier googleVerifier;
  private final UserRepository userRepository;

  public GoogleLoginStrategy(GoogleTokenVerifier googleVerifier, UserRepository userRepository) {
    this.googleVerifier = googleVerifier;
    this.userRepository = userRepository;
  }

  @Override
  public SocialProvider getProvider() {
    return SocialProvider.GOOGLE;
  }

  @Override
  public User authenticate(String socialToken) {
    var payload =
        googleVerifier
            .verifyAndGetPayload(socialToken)
            .orElseThrow(() -> new SocialLoginException("Token Google inválido"));

    String email = payload.getEmail();

    return userRepository
        .findByEmail(email)
        .orElseGet(
            () -> {
              User newUser = User.builder().name(payload.getName()).email(email).build();
              log.info("Criando novo usuário via Google login: {}", email);
              return userRepository.save(newUser);
            });
  }
}
