package com.creditapi.unit.presentation.user.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.creditapi.application.user.dto.request.CreateUserRequestDTO;
import com.creditapi.application.user.dto.request.UpdateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.dto.search.UserSearchDTO;
import com.creditapi.application.user.usecase.create.CreateUserUseCase;
import com.creditapi.application.user.usecase.delete.DeleteMultipleUsersUseCase;
import com.creditapi.application.user.usecase.delete.DeleteUserUseCase;
import com.creditapi.application.user.usecase.query.GetUserByEmailUseCase;
import com.creditapi.application.user.usecase.query.GetUserByIdUseCase;
import com.creditapi.application.user.usecase.query.GetUserWithPaginationUseCase;
import com.creditapi.application.user.usecase.search.SearchUserUseCase;
import com.creditapi.application.user.usecase.update.UpdateUserUseCase;
import com.creditapi.application.user.usecase.upload.UploadUserAvatarUseCase;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class UserControllerUnitTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CreateUserUseCase createUser;
  @MockBean private UpdateUserUseCase updateUser;
  @MockBean private DeleteUserUseCase deleteUser;
  @MockBean private DeleteMultipleUsersUseCase deleteMultiple;
  @MockBean private GetUserByIdUseCase getById;
  @MockBean private GetUserByEmailUseCase getByEmail;
  @MockBean private GetUserWithPaginationUseCase getAll;
  @MockBean private SearchUserUseCase search;
  @MockBean private UploadUserAvatarUseCase upload;
  @MockBean private UserRepository repository;

  private final ObjectMapper mapper = new ObjectMapper();

  private CreateUserRequestDTO createRequest;
  private UpdateUserRequestDTO updateRequest;
  private UserResponseDTO userResponse;

  @BeforeEach
  void setup() {
    createRequest =
        CreateUserRequestDTO.builder()
            .name("João")
            .email("joao@test.com")
            .document("12345678910")
            .password("Strong123!")
            .build();

    updateRequest =
        UpdateUserRequestDTO.builder()
            .name("João Atualizado")
            .email("atualizado@test.com")
            .document("12345678910")
            .build();

    userResponse =
        UserResponseDTO.builder()
            .id(1L)
            .name("João")
            .email("joao@test.com")
            .document("12345678910")
            .avatarUrl("https://cdn.com/avatar.jpg")
            .role(Role.USER.name())
            .build();
  }

  @Test
  @DisplayName("POST /user - Deve criar usuário com sucesso")
  void shouldCreateUserSuccessfully() throws Exception {
    when(createUser.execute(any())).thenReturn(userResponse);

    mockMvc
        .perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.email").value("joao@test.com"));
  }

  @Test
  @DisplayName("PUT /user/{id} - Deve atualizar usuário com sucesso")
  void shouldUpdateUserSuccessfully() throws Exception {
    when(updateUser.execute(1L, updateRequest)).thenReturn(userResponse);

    mockMvc
        .perform(
            put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("joao@test.com"));
  }

  @Test
  @DisplayName("PUT /user/{id} - Deve retornar 404 se usuário não for encontrado")
  void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
    when(updateUser.execute(1L, updateRequest))
        .thenThrow(new UserNotFoundException("Usuário não encontrado"));

    mockMvc
        .perform(
            put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Usuário não encontrado"));
  }

  @Test
  @DisplayName("DELETE /user/{id} - Deve remover usuário com sucesso")
  void shouldDeleteUserSuccessfully() throws Exception {
    mockMvc.perform(delete("/user/1")).andExpect(status().isNoContent());
    verify(deleteUser).execute(1L);
  }

  @Test
  @DisplayName("DELETE /user - Deve deletar múltiplos se for SUPER_ADMIN")
  void shouldDeleteMultipleUsers() throws Exception {
    User executor = User.builder().id(10L).role(Role.SUPER_ADMIN).build();
    when(repository.findById(10L)).thenReturn(Optional.of(executor));

    mockMvc
        .perform(
            delete("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(() -> "10")
                .content(mapper.writeValueAsString(List.of(1L, 2L))))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /user - Deve retornar 500 se executor não for encontrado")
  void shouldFailWhenExecutorNotFound() throws Exception {
    when(repository.findById(10L)).thenReturn(Optional.empty());

    mockMvc
        .perform(
            delete("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(() -> "10")
                .content(mapper.writeValueAsString(List.of(1L))))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Usuário executor não encontrado"));
  }

  @Test
  @DisplayName("GET /user/{id} - Deve retornar usuário pelo ID")
  void shouldGetUserById() throws Exception {
    when(getById.execute(1L)).thenReturn(userResponse);

    mockMvc
        .perform(get("/user/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  @DisplayName("GET /user/email/{email} - Deve retornar usuário pelo e-mail")
  void shouldGetUserByEmail() throws Exception {
    when(getByEmail.execute("joao@test.com")).thenReturn(userResponse);

    mockMvc
        .perform(get("/user/email/joao@test.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("joao@test.com"));
  }

  @Test
  @DisplayName("GET /user - Deve retornar usuários paginados")
  void shouldListUsersWithPagination() throws Exception {
    var page = new PageImpl<>(List.of(userResponse), PageRequest.of(0, 10), 1);
    when(getAll.execute(any())).thenReturn(page);

    mockMvc
        .perform(get("/user?page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1));
  }

  @Test
  @DisplayName("POST /user/search - Deve retornar resultado da busca avançada")
  void shouldSearchUsers() throws Exception {
    var searchDto = UserSearchDTO.builder().name("joao").build();
    var page = new PageImpl<>(List.of(userResponse));
    when(search.execute(any(), any())).thenReturn(page);

    mockMvc
        .perform(
            post("/user/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(searchDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1));
  }

  @Test
  @DisplayName("POST /user/{id}/upload - Deve aceitar upload de avatar")
  void shouldUploadAvatar() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "avatar.png", "image/png", new byte[] {1, 2});

    mockMvc.perform(multipart("/user/1/upload").file(file)).andExpect(status().isOk());

    verify(upload).execute(1L, file);
  }
}
