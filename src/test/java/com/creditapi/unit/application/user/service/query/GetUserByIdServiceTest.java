package com.creditapi.unit.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.service.query.GetUserByIdService;
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
class GetUserByIdServiceTest {

  @Mock private UserRepository repository;

  @InjectMocks private GetUserByIdService service;

  @Test
  @DisplayName("Deve retornar usuário encontrado")
  void shouldReturnUserById() {
    User user = User.builder().id(5L).name("Any").build();
    when(repository.findById(5L)).thenReturn(Optional.of(user));

    UserResponseDTO response = service.execute(5L);

    assertThat(response.id()).isEqualTo(5L);
  }

  @Test
  @DisplayName("Deve lançar exceção quando usuário não for encontrado")
  void shouldThrowWhenUserNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.execute(99L)).isInstanceOf(UserNotFoundException.class);
  }
}
