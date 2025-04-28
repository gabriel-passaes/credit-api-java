package com.creditapi.application.auth.usecase.login;

import com.creditapi.application.auth.dto.request.LoginRequestDTO;
import com.creditapi.application.auth.dto.response.LoginResponseDTO;

public interface LoginUserUseCase {
  LoginResponseDTO execute(LoginRequestDTO dto);
}
