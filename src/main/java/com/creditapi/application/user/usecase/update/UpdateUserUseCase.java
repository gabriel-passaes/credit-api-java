package com.creditapi.application.user.usecase.update;

import com.creditapi.application.user.dto.request.UpdateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;

public interface UpdateUserUseCase {
  UserResponseDTO execute(Long id, UpdateUserRequestDTO request);
}
