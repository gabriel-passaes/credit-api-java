package com.creditapi.application.auth.usecase.recover;

import com.creditapi.application.auth.dto.request.ResetPasswordRequestDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;

public interface ResetPasswordUseCase {
  RefreshResponseDTO execute(ResetPasswordRequestDTO dto);
}
