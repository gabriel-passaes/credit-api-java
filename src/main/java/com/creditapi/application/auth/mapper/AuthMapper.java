package com.creditapi.application.auth.mapper;

import com.creditapi.application.auth.dto.response.LoginResponseDTO;
import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;
import com.creditapi.application.auth.dto.response.RegisterResponseDTO;
import com.creditapi.domain.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

  public LoginResponseDTO toLoginResponse(User user, String accessToken, String refreshToken) {
    return LoginResponseDTO.builder()
        .userId(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .authSource("EMAIL")
        .build();
  }

  public RegisterResponseDTO toRegisterResponse(
      User user, String accessToken, String refreshToken) {
    return RegisterResponseDTO.builder()
        .userId(user.getId())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .authSource("EMAIL")
        .build();
  }

  public RefreshResponseDTO toRefreshResponse(String accessToken, String refreshToken) {
    return RefreshResponseDTO.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .authSource("REFRESH")
        .build();
  }

  public PasswordRecoveryResponseDTO toPasswordRecoveryResponse(String message) {
    return PasswordRecoveryResponseDTO.builder().message(message).build();
  }
}
