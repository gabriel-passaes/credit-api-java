package com.creditapi.application.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponseDTO {
  private Long userId;
  private String accessToken;
  private String refreshToken;
  private String authSource;
}
