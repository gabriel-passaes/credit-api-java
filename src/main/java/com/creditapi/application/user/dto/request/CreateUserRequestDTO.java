package com.creditapi.application.user.dto.request;

import com.creditapi.infrastructure.user.validator.annotation.ValidCpfOrCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserRequestDTO(
    @NotBlank(message = "O nome é obrigatório") String name,
    @Email(message = "E-mail inválido") @NotBlank(message = "O e-mail é obrigatório") String email,
    @NotBlank(message = "CPF ou CNPJ é obrigatório") @ValidCpfOrCnpj String document,
    @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password) {}
