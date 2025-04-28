package com.creditapi.application.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenRequestDTO {

  @NotBlank(message = "Token de refresh é obrigatório")
  private String refreshToken;
}
