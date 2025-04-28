package com.creditapi.unit.application.user.service.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.user.dto.request.UpdateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.service.update.UpdateUserService;
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
class UpdateUserServiceTest {

  @Mock private UserRepository repository;

  @InjectMocks private UpdateUserService service;

  @Test
  @DisplayName("Deve atualizar nome, e-mail e documento do usuário")
  void shouldUpdateUserSuccessfully() {
    Long id = 1L;
    UpdateUserRequestDTO request =
        new UpdateUserRequestDTO("Novo Nome", "novo@email.com", "12345678910");
    User existing =
        User.builder()
            .id(id)
            .name("Antigo")
            .email("antigo@email.com")
            .document("00000000000")
            .build();

    when(repository.findById(id)).thenReturn(Optional.of(existing));
    when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    UserResponseDTO response = service.execute(id, request);

    assertThat(response.name()).isEqualTo("Novo Nome");
    assertThat(response.email()).isEqualTo("novo@email.com");
    assertThat(response.document()).isEqualTo("12345678910");
  }

  @Test
  @DisplayName("Deve lançar exceção se usuário não for encontrado")
  void shouldThrowWhenUserNotFound() {
    Long id = 99L;
    UpdateUserRequestDTO request =
        new UpdateUserRequestDTO("Nome", "email@email.com", "11122233344");

    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.execute(id, request))
        .isInstanceOf(UserNotFoundException.class);

    verify(repository, never()).save(any());
  }
}
