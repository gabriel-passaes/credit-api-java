package com.creditapi.application.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateUserRequestDTO(
    @NotBlank(message = "O nome é obrigatório") String name,
    @Email(message = "E-mail inválido") @NotBlank(message = "O e-mail é obrigatório") String email,
    @NotBlank(message = "O documento é obrigatório") String document) {}
