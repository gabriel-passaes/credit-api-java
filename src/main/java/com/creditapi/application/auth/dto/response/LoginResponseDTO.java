package com.creditapi.application.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
  private Long userId;
  private String name;
  private String email;
  private String accessToken;
  private String refreshToken;
  private String authSource;
}
