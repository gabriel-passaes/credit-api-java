package com.creditapi.unit.application.user.dto.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.user.dto.search.UserSearchDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserSearchDTOTest {

  @Test
  @DisplayName("Deve preencher corretamente todos os campos")
  void builder_setsAllFields() {
    UserSearchDTO dto = new UserSearchDTO("Alice", "alice@test.com", "123", "ADMIN");

    assertThat(dto.name()).isEqualTo("Alice");
    assertThat(dto.email()).isEqualTo("alice@test.com");
    assertThat(dto.document()).isEqualTo("123");
    assertThat(dto.role()).isEqualTo("ADMIN");
  }
}
