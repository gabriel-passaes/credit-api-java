package com.creditapi.unit.application.auth.service.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.dto.request.RegisterRequestDTO;
import com.creditapi.application.auth.dto.response.RegisterResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.application.auth.service.register.RegisterUserService;
import com.creditapi.application.auth.usecase.email.SendWelcomeEmailUseCase;
import com.creditapi.domain.auth.gateway.repository.AuthTokenRepository;
import com.creditapi.domain.auth.model.AuthToken;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class RegisterUserServiceTest {

  private UserRepository userRepo;
  private PasswordEncoder encoder;
  private JwtTokenProvider jwtProvider;
  private AuthTokenRepository tokenRepo;
  private SendWelcomeEmailUseCase welcomeEmail;
  private AuthMapper mapper;
  private RegisterUserService service;

  @BeforeEach
  void setUp() {
    userRepo = mock(UserRepository.class);
    encoder = mock(PasswordEncoder.class);
    jwtProvider = mock(JwtTokenProvider.class);
    tokenRepo = mock(AuthTokenRepository.class);
    welcomeEmail = mock(SendWelcomeEmailUseCase.class);
    mapper = mock(AuthMapper.class);
    service =
        new RegisterUserService(userRepo, encoder, jwtProvider, tokenRepo, welcomeEmail, mapper);
  }

  @Test
  @DisplayName("Deve registrar usuário com sucesso e gerar tokens")
  void givenValidRequest_whenExecute_thenReturnRegisterResponse() {
    var request =
        RegisterRequestDTO.builder()
            .name("John Doe")
            .email("john@example.com")
            .password("123456")
            .build();

    var savedUser =
        User.builder()
            .id(1L)
            .name("John Doe")
            .email("john@example.com")
            .password("encoded123")
            .build();

    when(encoder.encode("123456")).thenReturn("encoded123");
    when(userRepo.save(any())).thenReturn(savedUser);
    when(jwtProvider.generateAccessToken(savedUser)).thenReturn("access");
    when(jwtProvider.generateRefreshTokenValue()).thenReturn("refresh");

    var expected =
        RegisterResponseDTO.builder()
            .userId(1L)
            .accessToken("access")
            .refreshToken("refresh")
            .authSource("EMAIL")
            .build();

    when(mapper.toRegisterResponse(savedUser, "access", "refresh")).thenReturn(expected);

    var response = service.execute(request);

    assertNotNull(response);
    assertEquals("access", response.getAccessToken());
    assertEquals("refresh", response.getRefreshToken());
    assertEquals(1L, response.getUserId());

    verify(userRepo).save(any(User.class));
    verify(welcomeEmail).execute("john@example.com", "John Doe");
    verify(tokenRepo).save(any(AuthToken.class));
  }

  @Test
  @DisplayName("Deve lançar exceção se userRepo retornar null")
  void givenNullUser_whenSave_thenThrow() {
    var request =
        RegisterRequestDTO.builder()
            .name("Error")
            .email("fail@example.com")
            .password("123")
            .build();

    when(userRepo.save(any())).thenReturn(null);

    assertThrows(NullPointerException.class, () -> service.execute(request));
  }

  @Test
  @DisplayName("Deve lançar exceção se e-mail falhar")
  void givenEmailFails_whenExecute_thenThrowException() {
    var request =
        RegisterRequestDTO.builder().name("Erro").email("fail@error.com").password("senha").build();

    var user =
        User.builder()
            .id(99L)
            .name("Erro")
            .email("fail@error.com")
            .password("senha-encoded")
            .build();

    when(encoder.encode("senha")).thenReturn("senha-encoded");
    when(userRepo.save(any())).thenReturn(user);
    when(jwtProvider.generateAccessToken(user)).thenReturn("access");
    when(jwtProvider.generateRefreshTokenValue()).thenReturn("refresh");
    when(mapper.toRegisterResponse(user, "access", "refresh"))
        .thenReturn(
            RegisterResponseDTO.builder()
                .userId(99L)
                .accessToken("access")
                .refreshToken("refresh")
                .authSource("EMAIL")
                .build());

    doThrow(new RuntimeException("Falha no envio de e-mail"))
        .when(welcomeEmail)
        .execute("fail@error.com", "Erro");

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.execute(request));
    assertEquals("Falha no envio de e-mail", ex.getMessage());
  }

  @Test
  @DisplayName("Token salvo deve conter authSource REGISTER")
  void givenRequest_whenExecute_thenAuthSourceMustBeRegister() {
    var request =
        RegisterRequestDTO.builder().name("Ana").email("ana@test.com").password("pass").build();

    var user = User.builder().id(5L).email("ana@test.com").name("Ana").password("encoded").build();
    when(userRepo.save(any())).thenReturn(user);
    when(encoder.encode("pass")).thenReturn("encoded");
    when(jwtProvider.generateAccessToken(any())).thenReturn("access");
    when(jwtProvider.generateRefreshTokenValue()).thenReturn("refresh");
    when(mapper.toRegisterResponse(user, "access", "refresh"))
        .thenReturn(
            RegisterResponseDTO.builder()
                .userId(5L)
                .accessToken("access")
                .refreshToken("refresh")
                .authSource("EMAIL")
                .build());

    service.execute(request);

    ArgumentCaptor<AuthToken> captor = ArgumentCaptor.forClass(AuthToken.class);
    verify(tokenRepo).save(captor.capture());

    AuthToken token = captor.getValue();
    assertEquals("REGISTER", token.getAuthSource());
  }
}
