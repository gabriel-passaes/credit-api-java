package com.creditapi.unit.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.service.query.GetUserByEmailService;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserByEmailServiceTest {

  @Mock private UserRepository repository;

  @InjectMocks private GetUserByEmailService service;

  @Test
  @DisplayName("Deve retornar usuário pelo e‑mail")
  void shouldReturnUserByEmail() {
    User user = User.builder().id(7L).email("x@test.com").build();
    when(repository.findByEmail("x@test.com")).thenReturn(Optional.of(user));

    UserResponseDTO dto = service.execute("x@test.com");

    assertThat(dto.id()).isEqualTo(7L);
  }

  @Test
  @DisplayName("Deve lançar exceção se e‑mail não existir")
  void shouldThrowWhenEmailNotFound() {
    when(repository.findByEmail("no@test.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.execute("no@test.com"))
        .isInstanceOf(UserNotFoundException.class);
  }
}
