package com.creditapi.unit.infrastructure.auth.provider.social;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.domain.auth.exception.SocialLoginException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.social.model.FacebookProfile;
import com.creditapi.infrastructure.auth.provider.social.strategy.FacebookLoginStrategy;
import com.creditapi.infrastructure.auth.provider.social.verifier.FacebookTokenVerifier;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FacebookLoginStrategyTest {

  private FacebookTokenVerifier facebookVerifier;
  private UserRepository userRepository;
  private FacebookLoginStrategy strategy;

  @BeforeEach
  void setup() {
    facebookVerifier = mock(FacebookTokenVerifier.class);
    userRepository = mock(UserRepository.class);
    strategy = new FacebookLoginStrategy(facebookVerifier, userRepository);
  }

  @Test
  @DisplayName("deve retornar usuário existente ao autenticar com token válido do Facebook")
  void shouldReturnExistingUser_whenValidFacebookTokenProvided() {
    String validToken = "valid-facebook-token";
    FacebookProfile profile = new FacebookProfile("usuario@fb.com", "Usuário Facebook");

    when(facebookVerifier.verifyAndGetProfile(validToken)).thenReturn(Optional.of(profile));
    User existingUser = User.builder().id(1L).email(profile.getEmail()).build();
    when(userRepository.findByEmail(profile.getEmail())).thenReturn(Optional.of(existingUser));

    User result = strategy.authenticate(validToken);

    assertThat(result).isEqualTo(existingUser);
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("deve criar novo usuário se e-mail não existir no banco")
  void shouldCreateNewUser_whenFacebookUserNotFound() {
    String validToken = "new-facebook-token";
    FacebookProfile profile = new FacebookProfile("novo@fb.com", "Novo Usuário");

    when(facebookVerifier.verifyAndGetProfile(validToken)).thenReturn(Optional.of(profile));
    when(userRepository.findByEmail(profile.getEmail())).thenReturn(Optional.empty());

    User createdUser = User.builder().name(profile.getName()).email(profile.getEmail()).build();

    when(userRepository.save(any())).thenReturn(createdUser);

    User result = strategy.authenticate(validToken);

    assertThat(result.getEmail()).isEqualTo("novo@fb.com");
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("deve lançar exceção se token do Facebook for inválido")
  void shouldThrowException_whenFacebookTokenIsInvalid() {
    when(facebookVerifier.verifyAndGetProfile("invalid")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> strategy.authenticate("invalid"))
        .isInstanceOf(SocialLoginException.class)
        .hasMessageContaining("Token Facebook inválido");
  }
}
