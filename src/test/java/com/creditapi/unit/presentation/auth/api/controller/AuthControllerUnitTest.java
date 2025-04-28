package com.creditapi.unit.presentation.auth.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.dto.request.LoginRequestDTO;
import com.creditapi.application.auth.dto.request.RecoverPasswordRequestDTO;
import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;
import com.creditapi.application.auth.dto.request.RegisterRequestDTO;
import com.creditapi.application.auth.dto.request.ResetPasswordRequestDTO;
import com.creditapi.application.auth.dto.request.SocialLoginRequestDTO;
import com.creditapi.application.auth.dto.response.LoginResponseDTO;
import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;
import com.creditapi.application.auth.dto.response.RegisterResponseDTO;
import com.creditapi.application.auth.usecase.login.LoginUserUseCase;
import com.creditapi.application.auth.usecase.login.social.LoginWithSocialUseCase;
import com.creditapi.application.auth.usecase.recover.RecoverPasswordUseCase;
import com.creditapi.application.auth.usecase.recover.ResetPasswordUseCase;
import com.creditapi.application.auth.usecase.refresh.InvalidateTokenUseCase;
import com.creditapi.application.auth.usecase.refresh.RefreshTokenUseCase;
import com.creditapi.application.auth.usecase.register.RegisterUserUseCase;
import com.creditapi.domain.auth.exception.InvalidCredentialsException;
import com.creditapi.domain.auth.exception.RefreshTokenInvalidException;
import com.creditapi.infrastructure.auth.provider.social.SocialProvider;
import com.creditapi.presentation.auth.api.controller.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

public class AuthControllerUnitTest {

  private LoginUserUseCase loginUseCase;
  private LoginWithSocialUseCase socialUseCase;
  private RegisterUserUseCase registerUseCase;
  private RecoverPasswordUseCase recoverUseCase;
  private ResetPasswordUseCase resetUseCase;
  private RefreshTokenUseCase refreshUseCase;
  private InvalidateTokenUseCase invalidateUseCase;
  private AuthController controller;

  @BeforeEach
  void setup() {
    loginUseCase = mock(LoginUserUseCase.class);
    socialUseCase = mock(LoginWithSocialUseCase.class);
    registerUseCase = mock(RegisterUserUseCase.class);
    recoverUseCase = mock(RecoverPasswordUseCase.class);
    resetUseCase = mock(ResetPasswordUseCase.class);
    refreshUseCase = mock(RefreshTokenUseCase.class);
    invalidateUseCase = mock(InvalidateTokenUseCase.class);
    controller =
        new AuthController(
            loginUseCase,
            socialUseCase,
            registerUseCase,
            recoverUseCase,
            resetUseCase,
            refreshUseCase,
            invalidateUseCase);
  }

  @Test
  @DisplayName("deve realizar login com sucesso")
  void shouldLoginSuccessfully() {
    var request = LoginRequestDTO.builder().email("user@email.com").password("123").build();

    var response = LoginResponseDTO.builder().userId(1L).email("user@email.com").build();

    when(loginUseCase.execute(request)).thenReturn(response);

    var result = controller.login(request);

    assertThat(result.getBody().getEmail()).isEqualTo("user@email.com");
  }

  @Test
  @DisplayName("deve lançar exceção para login com credenciais inválidas")
  void shouldThrowExceptionForInvalidLogin() {
    var request = LoginRequestDTO.builder().email("user@email.com").password("wrong").build();

    when(loginUseCase.execute(request))
        .thenThrow(new InvalidCredentialsException("Credenciais inválidas"));

    assertThatThrownBy(() -> controller.login(request))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessageContaining("Credenciais inválidas");
  }

  @Test
  @DisplayName("deve realizar login social com sucesso")
  void shouldLoginSocialSuccessfully() {
    var request =
        SocialLoginRequestDTO.builder().provider(SocialProvider.GOOGLE).token("valid").build();

    var response = LoginResponseDTO.builder().userId(1L).email("social@email.com").build();

    when(socialUseCase.execute(request)).thenReturn(response);

    var result = controller.socialLogin(request);

    assertThat(result.getBody().getEmail()).isEqualTo("social@email.com");
  }

  @Test
  @DisplayName("deve registrar usuário com sucesso")
  void shouldRegisterSuccessfully() {
    var request =
        RegisterRequestDTO.builder()
            .name("Gabriel")
            .email("gabriel@email.com")
            .password("123")
            .build();

    var response =
        RegisterResponseDTO.builder()
            .userId(3L)
            .accessToken("token")
            .refreshToken("refresh")
            .build();

    when(registerUseCase.execute(request)).thenReturn(response);

    var result = controller.register(request);

    assertThat(result.getStatusCode().value()).isEqualTo(201);
    assertThat(result.getBody().getAccessToken()).isEqualTo("token");
  }

  @Test
  @DisplayName("deve enviar email de recuperação com sucesso")
  void shouldSendRecoverEmailSuccessfully() {
    var request = RecoverPasswordRequestDTO.builder().email("recover@email.com").build();

    var response = PasswordRecoveryResponseDTO.builder().message("Enviado").build();

    when(recoverUseCase.execute(request)).thenReturn(response);

    var result = controller.recover(request);

    assertThat(result.getBody().getMessage()).isEqualTo("Enviado");
  }

  @Test
  @DisplayName("deve lançar exceção ao recuperar senha de e-mail inexistente")
  void shouldThrowExceptionForInvalidRecoveryEmail() {
    var request = RecoverPasswordRequestDTO.builder().email("inexistente@email.com").build();

    when(recoverUseCase.execute(request))
        .thenThrow(
            new com.creditapi.domain.auth.exception.AuthUserNotFoundException(
                "Usuário não encontrado"));

    assertThatThrownBy(() -> controller.recover(request))
        .isInstanceOf(com.creditapi.domain.auth.exception.AuthUserNotFoundException.class);
  }

  @Test
  @DisplayName("deve resetar senha com sucesso")
  void shouldResetPasswordSuccessfully() {
    var request = ResetPasswordRequestDTO.builder().token("token").newPassword("novaSenha").build();

    var response = RefreshResponseDTO.builder().accessToken("acc").refreshToken("ref").build();

    when(resetUseCase.execute(request)).thenReturn(response);

    var result = controller.reset(request);

    assertThat(result.getBody().getAccessToken()).isEqualTo("acc");
  }

  @Test
  @DisplayName("deve renovar tokens com sucesso")
  void shouldRefreshTokenSuccessfully() {
    var request = RefreshTokenRequestDTO.builder().refreshToken("refresh123").build();

    var response = RefreshResponseDTO.builder().accessToken("acc").refreshToken("newRef").build();

    when(refreshUseCase.execute(request)).thenReturn(response);

    var result = controller.refresh(request);

    assertThat(result.getBody().getRefreshToken()).isEqualTo("newRef");
  }

  @Test
  @DisplayName("deve lançar exceção ao tentar renovar com refresh inválido")
  void shouldThrowForInvalidRefreshToken() {
    var request = RefreshTokenRequestDTO.builder().refreshToken("invalid").build();

    when(refreshUseCase.execute(request))
        .thenThrow(new RefreshTokenInvalidException("Refresh inválido"));

    assertThatThrownBy(() -> controller.refresh(request))
        .isInstanceOf(RefreshTokenInvalidException.class);
  }

  @Test
  @DisplayName("deve invalidar um refresh token com sucesso")
  void shouldInvalidateToken() {
    var request = RefreshTokenRequestDTO.builder().refreshToken("refresh").build();

    var result = controller.logout(request);

    verify(invalidateUseCase).execute(request);
    assertThat(result.getStatusCode().value()).isEqualTo(204);
  }

  @Test
  @DisplayName("deve invalidar todos os tokens do usuário logado")
  void shouldInvalidateAllTokensForUser() {
    var auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(99L);

    var result = controller.logoutAll(auth);

    verify(invalidateUseCase).executeAll(99L);
    assertThat(result.getStatusCode().value()).isEqualTo(204);
  }
}
