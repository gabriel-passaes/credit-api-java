package com.creditapi.unit.application.auth.service.recover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.dto.request.ResetPasswordRequestDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.service.recover.ResetPasswordService;
import com.creditapi.domain.auth.exception.AuthUserNotFoundException;
import com.creditapi.domain.auth.exception.RefreshTokenExpiredException;
import com.creditapi.domain.auth.exception.RefreshTokenInvalidException;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class ResetPasswordServiceTest {

  private AuthTokenRepository tokenRepository;
  private UserRepository userRepository;
  private PasswordEncoder encoder;
  private JwtTokenProvider jwtProvider;
  private AuthMapper mapper;
  private ResetPasswordService service;

  @BeforeEach
  void setUp() {
    tokenRepository = mock(AuthTokenRepository.class);
    userRepository = mock(UserRepository.class);
    encoder = mock(PasswordEncoder.class);
    jwtProvider = mock(JwtTokenProvider.class);
    mapper = mock(AuthMapper.class);
    service =
        new ResetPasswordService(tokenRepository, userRepository, encoder, jwtProvider, mapper);
  }

  @Test
  @DisplayName("Deve resetar a senha com token válido")
  void givenValidToken_whenExecute_thenResetPassword() {
    var user = User.builder().id(1L).email("email").name("nome").build();
    var dto = ResetPasswordRequestDTO.builder().token("refresh").newPassword("nova").build();
    var token =
        AuthToken.builder()
            .userId(1L)
            .value("refresh")
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .invalidated(false)
            .build();

    when(tokenRepository.findByValue("refresh")).thenReturn(Optional.of(token));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(jwtProvider.generateAccessToken(user)).thenReturn("access");
    when(jwtProvider.generateRefreshTokenValue()).thenReturn("novoRefresh");
    when(mapper.toRefreshResponse("access", "novoRefresh"))
        .thenReturn(
            RefreshResponseDTO.builder().accessToken("access").refreshToken("novoRefresh").build());

    var result = service.execute(dto);

    assertEquals("access", result.getAccessToken());
    assertEquals("novoRefresh", result.getRefreshToken());
    verify(tokenRepository).invalidate("refresh");
  }

  @Test
  @DisplayName("Deve lançar exceção se token não for encontrado")
  void givenInvalidToken_whenExecute_thenThrow() {
    var dto = ResetPasswordRequestDTO.builder().token("404").newPassword("nova").build();
    when(tokenRepository.findByValue("404")).thenReturn(Optional.empty());

    assertThrows(RefreshTokenInvalidException.class, () -> service.execute(dto));
  }

  @Test
  @DisplayName("Deve lançar exceção se token estiver expirado")
  void givenExpiredToken_whenExecute_thenThrow() {
    var token =
        AuthToken.builder()
            .userId(1L)
            .value("expirado")
            .createdAt(Instant.now().minusSeconds(7200))
            .expiresAt(Instant.now().minusSeconds(3600))
            .build();

    var dto = ResetPasswordRequestDTO.builder().token("expirado").newPassword("nova").build();
    when(tokenRepository.findByValue("expirado")).thenReturn(Optional.of(token));

    assertThrows(RefreshTokenExpiredException.class, () -> service.execute(dto));
  }

  @Test
  @DisplayName("Deve lançar exceção se usuário não for encontrado")
  void givenValidToken_whenUserNotFound_thenThrow() {
    var token =
        AuthToken.builder()
            .userId(99L)
            .value("token")
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

    var dto = ResetPasswordRequestDTO.builder().token("token").newPassword("nova").build();
    when(tokenRepository.findByValue("token")).thenReturn(Optional.of(token));
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(AuthUserNotFoundException.class, () -> service.execute(dto));
  }
}
