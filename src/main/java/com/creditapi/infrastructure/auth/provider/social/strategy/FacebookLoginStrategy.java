package com.creditapi.infrastructure.auth.provider.social.strategy;

import com.creditapi.application.auth.usecase.login.social.SocialLoginStrategy;
import com.creditapi.domain.auth.exception.SocialLoginException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.social.SocialProvider;
import com.creditapi.infrastructure.auth.provider.social.verifier.FacebookTokenVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FacebookLoginStrategy implements SocialLoginStrategy {

  private static final Logger log = LoggerFactory.getLogger(FacebookLoginStrategy.class);

  private final FacebookTokenVerifier facebookVerifier;
  private final UserRepository userRepository;

  public FacebookLoginStrategy(
      FacebookTokenVerifier facebookVerifier, UserRepository userRepository) {
    this.facebookVerifier = facebookVerifier;
    this.userRepository = userRepository;
  }

  @Override
  public SocialProvider getProvider() {
    return SocialProvider.FACEBOOK;
  }

  @Override
  public User authenticate(String socialToken) {
    var profile =
        facebookVerifier
            .verifyAndGetProfile(socialToken)
            .orElseThrow(() -> new SocialLoginException("Token Facebook inválido"));

    return userRepository
        .findByEmail(profile.getEmail())
        .orElseGet(
            () -> {
              User newUser =
                  User.builder().name(profile.getName()).email(profile.getEmail()).build();
              log.info("Criando novo usuário via Facebook login: {}", profile.getEmail());
              return userRepository.save(newUser);
            });
  }
}
