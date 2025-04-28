package com.creditapi.application.user.mapper;

import com.creditapi.application.user.dto.request.CreateUserRequestDTO;
import com.creditapi.application.user.dto.request.UpdateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.user.persistence.entity.UserEntity;
import java.time.LocalDateTime;

public class UserMapper {

  public static User toDomain(UserEntity entity) {
    if (entity == null) return null;

    return User.builder()
        .id(entity.getId())
        .name(entity.getName())
        .email(entity.getEmail())
        .document(entity.getDocument())
        .avatarUrl(entity.getAvatarUrl())
        .role(Role.valueOf(entity.getRole()))
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .password(entity.getPassword())
        .build();
  }

  public static UserEntity toEntity(User user) {
    if (user == null) return null;

    UserEntity entity = new UserEntity();
    entity.setId(user.getId());
    entity.setName(user.getName());
    entity.setEmail(user.getEmail());
    entity.setDocument(user.getDocument());
    entity.setAvatarUrl(user.getAvatarUrl());
    entity.setRole(user.getRole().name());
    entity.setCreatedAt(user.getCreatedAt());
    entity.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt() : LocalDateTime.now());
    entity.setPassword(user.getPassword());
    return entity;
  }

  public static User fromCreateDto(CreateUserRequestDTO dto, Role role, String encodedPassword) {
    if (dto == null) return null;

    return User.builder()
        .name(dto.name())
        .email(dto.email())
        .document(dto.document())
        .avatarUrl(null)
        .role(role)
        .createdAt(LocalDateTime.now())
        .updatedAt(null)
        .password(encodedPassword)
        .build();
  }

  public static User fromUpdateDto(UpdateUserRequestDTO dto, User existingUser) {
    if (dto == null || existingUser == null) return null;

    return User.builder()
        .id(existingUser.getId())
        .name(dto.name() != null ? dto.name() : existingUser.getName())
        .email(dto.email() != null ? dto.email() : existingUser.getEmail())
        .document(dto.document() != null ? dto.document() : existingUser.getDocument())
        .avatarUrl(existingUser.getAvatarUrl())
        .role(existingUser.getRole())
        .createdAt(existingUser.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .password(existingUser.getPassword())
        .build();
  }

  public static UserResponseDTO toResponse(User user) {
    if (user == null) return null;

    return new UserResponseDTO(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getDocument(),
        user.getAvatarUrl(),
        user.getRole().name(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}
