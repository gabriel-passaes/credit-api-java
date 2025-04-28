package com.creditapi.application.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequestDTO {

  @NotBlank(message = "Token de recuperação é obrigatório")
  private String token;

  @NotBlank(message = "Nova senha é obrigatória")
  private String newPassword;
}
