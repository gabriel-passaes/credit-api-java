package com.creditapi.unit.application.auth.service.recover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.dto.request.RecoverPasswordRequestDTO;
import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;
import com.creditapi.application.auth.service.recover.RecoverPasswordService;
import com.creditapi.application.auth.usecase.email.SendRecoverEmailUseCase;
import com.creditapi.domain.auth.exception.AuthUserNotFoundException;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecoverPasswordServiceTest {

  private UserRepository userRepository;
  private AuthTokenRepository tokenRepository;
  private JwtTokenProvider jwtProvider;
  private SendRecoverEmailUseCase sendRecover;
  private RecoverPasswordService service;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    tokenRepository = mock(AuthTokenRepository.class);
    jwtProvider = mock(JwtTokenProvider.class);
    sendRecover = mock(SendRecoverEmailUseCase.class);
    service = new RecoverPasswordService(userRepository, tokenRepository, jwtProvider, sendRecover);
  }

  @Test
  @DisplayName("Deve retornar mensagem de recuperação ao encontrar usuário")
  void givenValidEmail_whenExecute_thenSendRecoveryEmail() {
    var user = User.builder().id(1L).name("Ana").email("ana@email.com").build();
    var dto = RecoverPasswordRequestDTO.builder().email("ana@email.com").build();
    when(userRepository.findByEmail("ana@email.com")).thenReturn(java.util.Optional.of(user));
    when(jwtProvider.generateRefreshTokenValue()).thenReturn("token123");
    when(sendRecover.execute("ana@email.com", "Ana", "token123"))
        .thenReturn(PasswordRecoveryResponseDTO.builder().message("ok").build());

    var result = service.execute(dto);

    assertEquals("ok", result.getMessage());
    verify(tokenRepository).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção se o e-mail não for encontrado")
  void givenUnknownEmail_whenExecute_thenThrowException() {
    var dto = RecoverPasswordRequestDTO.builder().email("naoexiste@email.com").build();
    when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.empty());

    assertThrows(AuthUserNotFoundException.class, () -> service.execute(dto));
    verify(tokenRepository, never()).save(any());
  }
}
