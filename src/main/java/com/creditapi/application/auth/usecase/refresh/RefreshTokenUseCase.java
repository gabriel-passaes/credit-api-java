package com.creditapi.application.auth.usecase.refresh;

import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;
import com.creditapi.application.auth.dto.response.RefreshResponseDTO;

public interface RefreshTokenUseCase {
  RefreshResponseDTO execute(RefreshTokenRequestDTO dto);
}
