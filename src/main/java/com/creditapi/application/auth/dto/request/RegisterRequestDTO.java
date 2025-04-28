package com.creditapi.application.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestDTO {

  @NotBlank(message = "Nome é obrigatório")
  private String name;

  @Email(message = "E-mail inválido")
  @NotBlank(message = "E-mail é obrigatório")
  private String email;

  @NotBlank(message = "Senha é obrigatória")
  private String password;
}
