package com.creditapi.unit.application.user.service.delete;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.user.service.delete.DeleteUserService;
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
class DeleteUserServiceTest {

  @Mock private UserRepository repository;

  @InjectMocks private DeleteUserService service;

  @Test
  @DisplayName("Deve remover usuário existente")
  void execute_deletesSuccessfully() {
    when(repository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));

    service.execute(1L);

    verify(repository).deleteById(1L);
  }

  @Test
  @DisplayName("Deve lançar exceção se usuário não existir")
  void execute_throwsWhenNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.execute(99L))
        .isInstanceOf(UserNotFoundException.class);

    verify(repository, never()).deleteById(anyLong());
  }
}
