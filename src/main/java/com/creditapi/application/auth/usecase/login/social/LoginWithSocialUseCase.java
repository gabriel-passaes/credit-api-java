package com.creditapi.application.auth.usecase.login.social;

import com.creditapi.application.auth.dto.request.SocialLoginRequestDTO;
import com.creditapi.application.auth.dto.response.LoginResponseDTO;

public interface LoginWithSocialUseCase {
  LoginResponseDTO execute(SocialLoginRequestDTO dto);
}
