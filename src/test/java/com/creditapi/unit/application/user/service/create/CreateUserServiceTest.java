package com.creditapi.unit.application.user.service.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.user.dto.request.CreateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.service.create.CreateUserService;
import com.creditapi.domain.user.exception.CnpjAlreadyRegisteredException;
import com.creditapi.domain.user.exception.CpfAlreadyRegisteredException;
import com.creditapi.domain.user.exception.EmailAlreadyRegisteredException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

  @Mock private UserRepository repository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private CreateUserService service;

  private CreateUserRequestDTO request;

  @BeforeEach
  void setUp() {
    request =
        CreateUserRequestDTO.builder()
            .name("João")
            .email("joao@test.com")
            .document("12345678910")
            .password("StrongPass1!")
            .build();
  }

  @Test
  @DisplayName("Deve criar usuário quando e‑mail e CPF forem únicos")
  void shouldCreateUserWhenValid() {
    when(repository.existsByEmail(request.email())).thenReturn(false);
    when(repository.existsByCpf(request.document())).thenReturn(false);
    when(passwordEncoder.encode(request.password())).thenReturn("encoded");

    User user =
        User.builder()
            .id(1L)
            .name("João")
            .email("joao@test.com")
            .document("12345678910")
            .password("encoded")
            .role(Role.USER)
            .build();

    when(repository.save(any(User.class))).thenReturn(user);

    UserResponseDTO response = service.execute(request);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.email()).isEqualTo("joao@test.com");
    verify(repository).save(any(User.class));
  }

  @Test
  @DisplayName("Deve lançar exceção quando e‑mail já estiver em uso")
  void shouldThrowWhenEmailExists() {
    when(repository.existsByEmail(request.email())).thenReturn(true);

    assertThatThrownBy(() -> service.execute(request))
        .isInstanceOf(EmailAlreadyRegisteredException.class);

    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção quando CPF já estiver em uso")
  void shouldThrowWhenCpfExists() {
    when(repository.existsByEmail(request.email())).thenReturn(false);
    when(repository.existsByCpf(request.document())).thenReturn(true);

    assertThatThrownBy(() -> service.execute(request))
        .isInstanceOf(CpfAlreadyRegisteredException.class);

    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("Deve aceitar criação com CNPJ quando CPF for nulo")
  void shouldAcceptCnpjWhenCpfNull() {
    request =
        CreateUserRequestDTO.builder()
            .name("Maria")
            .email("maria@test.com")
            .document("11222333000199")
            .password("SenhaForte123")
            .build();

    when(repository.existsByEmail(request.email())).thenReturn(false);
    when(repository.existsByCnpj(request.document())).thenReturn(false);
    when(passwordEncoder.encode(request.password())).thenReturn("encoded");

    User user =
        User.builder()
            .id(2L)
            .name("Maria")
            .email("maria@test.com")
            .document("11222333000199")
            .password("encoded")
            .role(Role.USER)
            .build();

    when(repository.save(any(User.class))).thenReturn(user);

    UserResponseDTO response = service.execute(request);

    assertThat(response.id()).isEqualTo(2L);
    assertThat(response.email()).isEqualTo("maria@test.com");
    verify(repository).save(any(User.class));
  }

  @Test
  @DisplayName("Deve lançar exceção quando CNPJ já estiver em uso")
  void shouldThrowWhenCnpjExists() {
    request =
        CreateUserRequestDTO.builder()
            .name("Maria")
            .email("maria@test.com")
            .document("11222333000199")
            .password("SenhaForte123")
            .build();

    when(repository.existsByEmail(request.email())).thenReturn(false);
    when(repository.existsByCnpj(request.document())).thenReturn(true);

    assertThatThrownBy(() -> service.execute(request))
        .isInstanceOf(CnpjAlreadyRegisteredException.class);

    verify(repository, never()).save(any());
  }
}
