package com.creditapi.unit.application.auth.service.refresh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.service.refresh.RefreshTokenService;
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

class RefreshTokenServiceTest {

  private AuthTokenRepository tokenRepo;
  private JwtTokenProvider jwtProvider;
  private UserRepository userRepo;
  private AuthMapper mapper;
  private RefreshTokenService service;

  @BeforeEach
  void setUp() {
    tokenRepo = mock(AuthTokenRepository.class);
    jwtProvider = mock(JwtTokenProvider.class);
    userRepo = mock(UserRepository.class);
    mapper = mock(AuthMapper.class);
    service = new RefreshTokenService(tokenRepo, jwtProvider, userRepo, mapper);
  }

  @Test
  @DisplayName("Deve renovar os tokens com refresh válido")
  void givenValidRefreshToken_whenExecute_thenReturnNewTokens() {
    var user = User.builder().id(10L).email("test@test.com").name("test").build();
    var token =
        AuthToken.builder()
            .userId(10L)
            .value("refresh")
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
    var dto = RefreshTokenRequestDTO.builder().refreshToken("refresh").build();

    when(tokenRepo.findByValue("refresh")).thenReturn(Optional.of(token));
    when(userRepo.findById(10L)).thenReturn(Optional.of(user));
    when(jwtProvider.generateAccessToken(user)).thenReturn("access");
    when(jwtProvider.generateRefreshTokenValue()).thenReturn("newRefresh");
    when(mapper.toRefreshResponse("access", "newRefresh"))
        .thenReturn(
            RefreshResponseDTO.builder().accessToken("access").refreshToken("newRefresh").build());

    var result = service.execute(dto);

    assertEquals("access", result.getAccessToken());
    assertEquals("newRefresh", result.getRefreshToken());
    verify(tokenRepo).invalidate("refresh");
    verify(tokenRepo).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção se token não for encontrado")
  void givenInvalidToken_whenExecute_thenThrow() {
    when(tokenRepo.findByValue("invalido")).thenReturn(Optional.empty());
    var dto = RefreshTokenRequestDTO.builder().refreshToken("invalido").build();

    assertThrows(RefreshTokenInvalidException.class, () -> service.execute(dto));
  }

  @Test
  @DisplayName("Deve lançar exceção se token estiver expirado")
  void givenExpiredToken_whenExecute_thenThrow() {
    var expired =
        AuthToken.builder()
            .userId(5L)
            .value("expired")
            .expiresAt(Instant.now().minusSeconds(60))
            .build();
    var dto = RefreshTokenRequestDTO.builder().refreshToken("expired").build();

    when(tokenRepo.findByValue("expired")).thenReturn(Optional.of(expired));

    assertThrows(RefreshTokenExpiredException.class, () -> service.execute(dto));
  }

  @Test
  @DisplayName("Deve lançar exceção se usuário não for encontrado")
  void givenValidToken_whenUserNotFound_thenThrow() {
    var token =
        AuthToken.builder()
            .userId(7L)
            .value("valid")
            .expiresAt(Instant.now().plusSeconds(600))
            .build();
    var dto = RefreshTokenRequestDTO.builder().refreshToken("valid").build();

    when(tokenRepo.findByValue("valid")).thenReturn(Optional.of(token));
    when(userRepo.findById(7L)).thenReturn(Optional.empty());

    assertThrows(AuthUserNotFoundException.class, () -> service.execute(dto));
  }
}
