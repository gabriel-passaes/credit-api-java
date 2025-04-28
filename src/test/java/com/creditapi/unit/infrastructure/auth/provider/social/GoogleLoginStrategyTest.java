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
import com.creditapi.infrastructure.auth.provider.social.model.GoogleProfile;
import com.creditapi.infrastructure.auth.provider.social.strategy.GoogleLoginStrategy;
import com.creditapi.infrastructure.auth.provider.social.verifier.GoogleTokenVerifier;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoogleLoginStrategyTest {

  private GoogleTokenVerifier googleVerifier;
  private UserRepository userRepository;
  private GoogleLoginStrategy strategy;

  @BeforeEach
  void setup() {
    googleVerifier = mock(GoogleTokenVerifier.class);
    userRepository = mock(UserRepository.class);
    strategy = new GoogleLoginStrategy(googleVerifier, userRepository);
  }

  @Test
  @DisplayName("deve retornar usuário existente ao autenticar com token válido do Google")
  void shouldReturnExistingUser_whenValidGoogleTokenProvided() {
    String validToken = "valid-google-token";
    GoogleProfile profile = new GoogleProfile("usuario@email.com", "Usuário Google");

    when(googleVerifier.verifyAndGetPayload(validToken)).thenReturn(Optional.of(profile));
    User existingUser = User.builder().id(1L).email(profile.getEmail()).build();
    when(userRepository.findByEmail(profile.getEmail())).thenReturn(Optional.of(existingUser));

    User result = strategy.authenticate(validToken);

    assertThat(result).isEqualTo(existingUser);
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("deve criar novo usuário se e-mail não existir no banco")
  void shouldCreateNewUser_whenGoogleUserNotFound() {
    String validToken = "new-user-google-token";
    GoogleProfile profile = new GoogleProfile("novo@email.com", "Novo Usuário");

    when(googleVerifier.verifyAndGetPayload(validToken)).thenReturn(Optional.of(profile));
    when(userRepository.findByEmail(profile.getEmail())).thenReturn(Optional.empty());

    User createdUser = User.builder().name(profile.getName()).email(profile.getEmail()).build();

    when(userRepository.save(any())).thenReturn(createdUser);

    User result = strategy.authenticate(validToken);

    assertThat(result.getEmail()).isEqualTo("novo@email.com");
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("deve lançar exceção se token do Google for inválido")
  void shouldThrowException_whenGoogleTokenIsInvalid() {
    when(googleVerifier.verifyAndGetPayload("invalid")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> strategy.authenticate("invalid"))
        .isInstanceOf(SocialLoginException.class)
        .hasMessageContaining("Token Google inválido");
  }
}
