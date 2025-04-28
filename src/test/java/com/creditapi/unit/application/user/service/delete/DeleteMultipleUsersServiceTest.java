package com.creditapi.unit.application.user.service.delete;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.creditapi.application.user.service.delete.DeleteMultipleUsersService;
import com.creditapi.domain.user.exception.UnauthorizedUserOperationException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteMultipleUsersServiceTest {

  @Mock private UserRepository repository;

  @InjectMocks private DeleteMultipleUsersService service;

  @Test
  @DisplayName("SUPER_ADMIN pode deletar múltiplos usuários")
  void execute_superAdminDeletes() {
    User superAdmin = User.builder().id(1L).role(Role.SUPER_ADMIN).build();
    List<Long> ids = List.of(2L, 3L);

    service.execute(superAdmin, ids);

    verify(repository).deleteAllById(ids);
  }

  @Test
  @DisplayName("ADMIN não pode deletar múltiplos usuários")
  void execute_adminThrows() {
    User admin = User.builder().id(1L).role(Role.ADMIN).build();
    List<Long> ids = List.of(2L);

    assertThatThrownBy(() -> service.execute(admin, ids))
        .isInstanceOf(UnauthorizedUserOperationException.class);

    verify(repository, never()).deleteAllById(any());
  }
}
