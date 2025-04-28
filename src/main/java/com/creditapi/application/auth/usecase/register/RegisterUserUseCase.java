package com.creditapi.application.auth.usecase.register;

import com.creditapi.application.auth.dto.request.RegisterRequestDTO;
import com.creditapi.application.auth.dto.response.RegisterResponseDTO;

public interface RegisterUserUseCase {
  RegisterResponseDTO execute(RegisterRequestDTO req);
}
