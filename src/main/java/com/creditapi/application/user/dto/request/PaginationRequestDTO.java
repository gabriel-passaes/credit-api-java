package com.creditapi.application.user.dto.request;

import jakarta.validation.constraints.Min;

public record PaginationRequestDTO(
    @Min(value = 0, message = "Número da página deve ser maior ou igual a 0") int page,
    @Min(value = 1, message = "Tamanho da página deve ser no mínimo 1") int size) {}
