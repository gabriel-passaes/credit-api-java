package com.creditapi.application.user.usecase.create;

import com.creditapi.application.user.dto.request.CreateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;

public interface CreateUserUseCase {
  UserResponseDTO execute(CreateUserRequestDTO request);
}
