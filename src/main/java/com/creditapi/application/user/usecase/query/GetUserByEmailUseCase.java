package com.creditapi.application.user.usecase.query;

import com.creditapi.application.user.dto.response.UserResponseDTO;

public interface GetUserByEmailUseCase {
  UserResponseDTO execute(String email);
}
