package com.creditapi.application.auth.usecase.refresh;

import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;

public interface InvalidateTokenUseCase {
  void execute(RefreshTokenRequestDTO dto);

  void executeAll(Long userId);
}
