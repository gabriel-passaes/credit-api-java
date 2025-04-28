package com.creditapi.application.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordRecoveryResponseDTO {
  private String message;
}
