package com.creditapi.unit.application.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.auth.dto.response.LoginResponseDTO;
import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;
import com.creditapi.application.auth.dto.response.RegisterResponseDTO;
import com.creditapi.application.auth.mapper.AuthMapper;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthMapperTest {

  private final AuthMapper mapper = new AuthMapper();

  @Test
  @DisplayName("Deve mapear LoginResponseDTO corretamente com authSource = EMAIL")
  void givenUserAndTokens_whenToLoginResponse_thenReturnDTO() {
    User user = createFakeUser();
    String access = "access.jwt";
    String refresh = "refresh.jwt";

    LoginResponseDTO dto = mapper.toLoginResponse(user, access, refresh);

    assertThat(dto.getUserId()).isEqualTo(user.getId());
    assertThat(dto.getName()).isEqualTo(user.getName());
    assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    assertThat(dto.getAccessToken()).isEqualTo(access);
    assertThat(dto.getRefreshToken()).isEqualTo(refresh);
    assertThat(dto.getAuthSource()).isEqualTo("EMAIL");
  }

  @Test
  @DisplayName("Deve mapear RegisterResponseDTO corretamente com authSource = EMAIL")
  void givenUserAndTokens_whenToRegisterResponse_thenReturnDTO() {
    User user = createFakeUser();
    String access = "access.token";
    String refresh = "refresh.token";

    RegisterResponseDTO dto = mapper.toRegisterResponse(user, access, refresh);

    assertThat(dto.getUserId()).isEqualTo(user.getId());
    assertThat(dto.getAccessToken()).isEqualTo(access);
    assertThat(dto.getRefreshToken()).isEqualTo(refresh);
    assertThat(dto.getAuthSource()).isEqualTo("EMAIL");
  }

  @Test
  @DisplayName("Deve mapear RefreshResponseDTO corretamente com authSource = REFRESH")
  void givenTokens_whenToRefreshResponse_thenReturnDTO() {
    String access = "access.new";
    String refresh = "refresh.new";

    RefreshResponseDTO dto = mapper.toRefreshResponse(access, refresh);

    assertThat(dto.getAccessToken()).isEqualTo(access);
    assertThat(dto.getRefreshToken()).isEqualTo(refresh);
    assertThat(dto.getAuthSource()).isEqualTo("REFRESH");
  }

  @Test
  @DisplayName("Deve mapear PasswordRecoveryResponseDTO corretamente")
  void givenMessage_whenToPasswordRecoveryResponse_thenReturnDTO() {
    String message = "E-mail enviado com sucesso";

    PasswordRecoveryResponseDTO dto = mapper.toPasswordRecoveryResponse(message);

    assertThat(dto.getMessage()).isEqualTo(message);
  }

  private User createFakeUser() {
    return User.builder()
        .id(100L)
        .name("Marcos Valente")
        .email("marcos@example.com")
        .document("12345678900")
        .avatarUrl("https://s3.com/avatar.jpg")
        .role(Role.USER)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .password("encoded-password")
        .build();
  }
}
