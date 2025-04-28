package com.creditapi.unit.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.service.query.GetUserWithPaginationService;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetUserWithPaginationServiceTest {

  @Mock private UserRepository repository;

  @InjectMocks private GetUserWithPaginationService service;

  @Test
  @DisplayName("Deve retornar página de usuários")
  void shouldReturnPaginatedUsers() {
    Pageable pageable = PageRequest.of(0, 2);
    List<User> users =
        List.of(User.builder().id(1L).name("A").build(), User.builder().id(2L).name("B").build());

    Page<User> page = new PageImpl<>(users, pageable, 2);
    when(repository.findAll(pageable)).thenReturn(page);

    Page<UserResponseDTO> response = service.execute(pageable);

    assertThat(response.getTotalElements()).isEqualTo(2);
    assertThat(response.getContent()).hasSize(2);
  }
}
