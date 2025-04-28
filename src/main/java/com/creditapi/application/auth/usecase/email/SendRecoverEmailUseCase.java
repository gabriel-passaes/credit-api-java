package com.creditapi.application.auth.usecase.email;

import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;

public interface SendRecoverEmailUseCase {
  PasswordRecoveryResponseDTO execute(String to, String name, String token);
}
