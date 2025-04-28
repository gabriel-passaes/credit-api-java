package com.creditapi.application.user.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserResponseDTO(
    Long id,
    String name,
    String email,
    String document,
    String avatarUrl,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
