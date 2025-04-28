package com.creditapi.unit.application.auth.service.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.dto.request.SocialLoginRequestDTO;
import com.creditapi.application.auth.dto.response.LoginResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.service.login.LoginWithSocialService;
import com.creditapi.application.auth.usecase.login.social.SocialLoginStrategy;
import com.creditapi.application.auth.usecase.login.social.SocialLoginStrategyFactory;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import com.creditapi.infrastructure.auth.provider.social.SocialProvider;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginWithSocialServiceTest {

  private SocialLoginStrategyFactory factory;
  private JwtTokenProvider jwtProvider;
  private AuthTokenRepository tokenRepo;
  private AuthMapper mapper;
  private LoginWithSocialService service;

  @BeforeEach
  void setUp() {
    factory = mock(SocialLoginStrategyFactory.class);
    jwtProvider = mock(JwtTokenProvider.class);
    tokenRepo = mock(AuthTokenRepository.class);
    mapper = mock(AuthMapper.class);
    service = new LoginWithSocialService(factory, jwtProvider, tokenRepo, mapper);
  }

  @Test
  @DisplayName("Deve realizar login social com sucesso")
  void givenValidSocialToken_whenLogin_thenReturnTokens() {
    SocialLoginRequestDTO dto =
        SocialLoginRequestDTO.builder()
            .provider(SocialProvider.GOOGLE)
            .token("valid-token")
            .build();

    User user = User.builder().id(10L).email("google@auth.com").build();

    SocialLoginStrategy strategy = mock(SocialLoginStrategy.class);
    when(factory.get(SocialProvider.GOOGLE)).thenReturn(strategy);
    when(strategy.authenticate(dto.getToken())).thenReturn(user);

    when(jwtProvider.generateAccessToken(user)).thenReturn("access-token");
    when(jwtProvider.generateRefreshTokenValue()).thenReturn("refresh-token");
    when(jwtProvider.getRefreshTtl()).thenReturn(Duration.ofHours(1));

    LoginResponseDTO expected =
        LoginResponseDTO.builder()
            .userId(10L)
            .name(null)
            .email("google@auth.com")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .authSource("GOOGLE")
            .build();

    when(mapper.toLoginResponse(user, "access-token", "refresh-token")).thenReturn(expected);

    LoginResponseDTO result = service.execute(dto);

    assertEquals(expected, result);
    verify(tokenRepo).save(any(AuthToken.class));
  }
}
