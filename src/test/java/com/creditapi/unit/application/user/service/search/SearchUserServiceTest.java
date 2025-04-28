package com.creditapi.unit.application.user.service.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.dto.search.UserSearchDTO;
import com.creditapi.application.user.service.search.SearchUserService;
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
class SearchUserServiceTest {

  @Mock private UserRepository repository;

  @InjectMocks private SearchUserService service;

  @Test
  @DisplayName("Deve buscar por nome parcial ignorando case")
  void shouldSearchByName() {
    Pageable pageable = PageRequest.of(0, 10);
    UserSearchDTO search = new UserSearchDTO("ali", null, null, null);
    Page<User> result = new PageImpl<>(List.of(User.builder().id(1L).name("Alice").build()));

    when(repository.search("ali", null, null, pageable)).thenReturn(result);

    Page<UserResponseDTO> dtoPage = service.execute(search, pageable);

    assertThat(dtoPage.getContent()).hasSize(1);
    assertThat(dtoPage.getContent().get(0).name()).isEqualTo("Alice");
  }

  @Test
  @DisplayName("Busca sem filtros deve retornar página vazia")
  void shouldReturnEmptyPageWhenNoResults() {
    Pageable pageable = PageRequest.of(0, 5);
    UserSearchDTO search = new UserSearchDTO(null, null, null, null);

    when(repository.search(null, null, null, pageable)).thenReturn(Page.empty(pageable));

    Page<UserResponseDTO> result = service.execute(search, pageable);

    assertThat(result).isEmpty();
  }
}
