package com.creditapi.unit.application.auth.service.refresh;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;
import com.creditapi.application.auth.service.refresh.InvalidateTokenService;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvalidateTokenServiceTest {

  private AuthTokenRepository tokenRepo;
  private InvalidateTokenService service;

  @BeforeEach
  void setUp() {
    tokenRepo = mock(AuthTokenRepository.class);
    service = new InvalidateTokenService(tokenRepo);
  }

  @Test
  @DisplayName("Deve invalidar um único refresh token")
  void givenRefreshToken_whenExecute_thenInvalidateIt() {
    var dto = RefreshTokenRequestDTO.builder().refreshToken("token123").build();
    service.execute(dto);
    verify(tokenRepo).invalidate("token123");
  }

  @Test
  @DisplayName("Deve invalidar todos os tokens de um usuário")
  void givenUserId_whenExecuteAll_thenInvalidateAllTokens() {
    service.executeAll(42L);
    verify(tokenRepo).invalidateByUserId(42L);
  }
}
