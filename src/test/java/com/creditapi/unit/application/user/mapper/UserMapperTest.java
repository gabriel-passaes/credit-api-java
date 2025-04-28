package com.creditapi.unit.application.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.user.mapper.UserMapper;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.user.persistence.entity.UserEntity;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserMapperTest {

  @Test
  @DisplayName("toEntity deve mapear corretamente os campos")
  void toEntity_mapsCorrectly() {
    User domain =
        User.builder()
            .id(1L)
            .name("Gabriel")
            .email("gabriel@teste.com")
            .document("12345678910")
            .avatarUrl("url")
            .password("123")
            .role(Role.ADMIN)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    UserEntity entity = UserMapper.toEntity(domain);

    assertThat(entity.getId()).isEqualTo(domain.getId());
    assertThat(entity.getEmail()).isEqualTo(domain.getEmail());
    assertThat(entity.getDocument()).isEqualTo(domain.getDocument());
    assertThat(entity.getRole()).isEqualTo(domain.getRole().name());
  }

  @Test
  @DisplayName("toDomain deve mapear corretamente os campos")
  void toDomain_mapsCorrectly() {
    UserEntity entity =
        UserEntity.builder()
            .id(2L)
            .name("Maria")
            .email("maria@teste.com")
            .document("11222333000199")
            .role("USER")
            .avatarUrl("url")
            .password("123")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    User domain = UserMapper.toDomain(entity);

    assertThat(domain.getId()).isEqualTo(entity.getId());
    assertThat(domain.getEmail()).isEqualTo(entity.getEmail());
    assertThat(domain.getDocument()).isEqualTo(entity.getDocument());
    assertThat(domain.getRole().name()).isEqualTo(entity.getRole());
  }
}
