package com.creditapi.unit.domain.user.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.domain.user.model.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  @DisplayName("Deve atualizar nome, e-mail e documento corretamente")
  void shouldUpdateUserInfo() {
    LocalDateTime originalTime = LocalDateTime.now().minusDays(1);

    User user =
        User.builder()
            .id(1L)
            .name("Antigo")
            .email("antigo@email.com")
            .document("00000000000")
            .updatedAt(originalTime)
            .build();

    user.update("Novo", "novo@email.com", "12345678910");

    assertThat(user.getName()).isEqualTo("Novo");
    assertThat(user.getEmail()).isEqualTo("novo@email.com");
    assertThat(user.getDocument()).isEqualTo("12345678910");
    assertThat(user.getUpdatedAt()).isAfter(originalTime);
  }

  @Test
  @DisplayName("Deve atualizar a URL do avatar e timestamp")
  void shouldUpdateAvatarUrl() {
    LocalDateTime before = LocalDateTime.now().minusDays(1);

    User user = User.builder().id(1L).name("User").avatarUrl("old.png").updatedAt(before).build();

    user.updateAvatar("new.png");

    assertThat(user.getAvatarUrl()).isEqualTo("new.png");
    assertThat(user.getUpdatedAt()).isAfter(before);
  }
}
