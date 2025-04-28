package com.creditapi.application.auth.usecase.recover;

import com.creditapi.application.auth.dto.request.RecoverPasswordRequestDTO;
import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;

public interface RecoverPasswordUseCase {
  PasswordRecoveryResponseDTO execute(RecoverPasswordRequestDTO dto);
}
