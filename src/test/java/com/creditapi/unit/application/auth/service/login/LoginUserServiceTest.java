package com.creditapi.unit.application.auth.service.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.dto.request.LoginRequestDTO;
import com.creditapi.application.auth.dto.response.LoginResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.service.login.LoginUserService;
import com.creditapi.domain.auth.exception.InvalidCredentialsException;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class LoginUserServiceTest {

  private UserRepository userRepo;
  private PasswordEncoder passwordEncoder;
  private JwtTokenProvider jwtProvider;
  private AuthTokenRepository tokenRepo;
  private AuthMapper mapper;
  private LoginUserService service;

  @BeforeEach
  void setUp() {
    userRepo = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    jwtProvider = mock(JwtTokenProvider.class);
    tokenRepo = mock(AuthTokenRepository.class);
    mapper = mock(AuthMapper.class);

    service = new LoginUserService(userRepo, passwordEncoder, jwtProvider, tokenRepo, mapper);
  }

  @Test
  @DisplayName("Deve realizar login com sucesso quando credenciais forem válidas")
  void givenValidCredentials_whenLogin_thenReturnTokens() {
    LoginRequestDTO dto =
        LoginRequestDTO.builder().email("email@teste.com").password("senha123").build();

    User user = User.builder().id(1L).email("email@teste.com").password("senha123").build();

    when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
    when(jwtProvider.generateAccessToken(user)).thenReturn("access-token");
    when(jwtProvider.generateRefreshTokenValue()).thenReturn("refresh-token");
    when(jwtProvider.getRefreshTtl()).thenReturn(Duration.ofDays(1));

    LoginResponseDTO expectedResponse =
        LoginResponseDTO.builder()
            .userId(1L)
            .email("email@teste.com")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .authSource("EMAIL")
            .build();

    when(mapper.toLoginResponse(user, "access-token", "refresh-token"))
        .thenReturn(expectedResponse);

    LoginResponseDTO response = service.execute(dto);

    assertNotNull(response);
    assertEquals(expectedResponse, response);
    verify(tokenRepo).save(any(AuthToken.class));
  }

  @Test
  @DisplayName("Deve lançar exceção quando usuário não for encontrado")
  void givenInvalidEmail_whenLogin_thenThrowException() {
    LoginRequestDTO dto =
        LoginRequestDTO.builder().email("naoexiste@teste.com").password("senha").build();

    when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

    assertThrows(InvalidCredentialsException.class, () -> service.execute(dto));
  }

  @Test
  @DisplayName("Deve lançar exceção quando senha for inválida")
  void givenInvalidPassword_whenLogin_thenThrowException() {
    LoginRequestDTO dto =
        LoginRequestDTO.builder().email("email@teste.com").password("senhaErrada").build();

    User user = User.builder().id(1L).email("email@teste.com").password("senhaCerta").build();

    when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> service.execute(dto));
  }
}
