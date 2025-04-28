package com.creditapi.application.auth.dto.request;

import com.creditapi.infrastructure.auth.provider.social.SocialProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SocialLoginRequestDTO {

  @NotNull(message = "provider é obrigatório")
  private SocialProvider provider;

  @NotBlank(message = "token é obrigatório")
  private String token;
}
