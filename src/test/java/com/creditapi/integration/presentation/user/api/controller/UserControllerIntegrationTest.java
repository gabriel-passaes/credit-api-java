package com.creditapi.integration.presentation.user.api.controller;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.creditapi.application.user.dto.request.CreateUserRequestDTO;
import com.creditapi.application.user.dto.request.UpdateUserRequestDTO;
import com.creditapi.application.user.dto.search.UserSearchDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private WebTestClient webClient;

  @Test
  @DisplayName("POST /user - Deve criar e retornar usuário criado")
  void shouldCreateUser() {
    CreateUserRequestDTO request = CreateUserRequestDTO.builder()
        .name("Carlos")
        .email("carlos@email.com")
        .document("12345678900")
        .password("Senha123")
        .build();

    webClient
        .post()
        .uri("/user")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.email")
        .isEqualTo("carlos@email.com");
  }

  @Test
  @DisplayName("POST /user - Deve retornar 400 para dados inválidos")
  void shouldFailToCreateUserWithInvalidData() {
    CreateUserRequestDTO request = CreateUserRequestDTO.builder()
        .name("")
        .email("invalido")
        .document("")
        .password("")
        .build();

    webClient
        .post()
        .uri("/user")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("PUT /user/{id} - Deve atualizar usuário existente")
  void shouldUpdateUser() {
    UpdateUserRequestDTO update = UpdateUserRequestDTO.builder()
        .name("Carlos Atualizado")
        .email("carlos@atualizado.com")
        .document("12345678900")
        .build();

    webClient
        .put()
        .uri("/user/1")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.email")
        .isEqualTo("carlos@atualizado.com");
  }

  @Test
  @DisplayName("PUT /user/{id} - Deve retornar 404 se usuário não existir")
  void shouldReturnNotFoundWhenUpdatingNonexistentUser() {
    UpdateUserRequestDTO update = UpdateUserRequestDTO.builder()
        .name("Ghost")
        .email("ghost@fail.com")
        .document("99999999999")
        .build();

    webClient
        .put()
        .uri("/user/99999")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @DisplayName("GET /user/{id} - Deve retornar usuário pelo ID")
  void shouldGetUserById() {
    webClient
        .get()
        .uri("/user/1")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(1);
  }

  @Test
  @DisplayName("GET /user/email/{email} - Deve retornar usuário por e-mail")
  void shouldGetUserByEmail() {
    webClient
        .get()
        .uri("/user/email/carlos@email.com")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo("Carlos");
  }

  @Test
  @DisplayName("GET /user/email/{email} - Deve retornar 404 se e-mail não existir")
  void shouldReturnNotFoundWhenEmailNotExists() {
    webClient
        .get()
        .uri("/user/email/naoexiste@email.com")
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @DisplayName("GET /user - Deve retornar lista paginada")
  void shouldListUsers() {
    webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/user")
            .queryParam("page", 0)
            .queryParam("size", 10)
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content")
        .isArray();
  }

  @Test
  @DisplayName("POST /user/search - Deve realizar busca avançada")
  void shouldSearchUsers() {
    UserSearchDTO search = UserSearchDTO.builder()
        .name("Carlos")
        .build();

    webClient
        .post()
        .uri("/user/search")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(search)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content")
        .isArray();
  }

  @Test
  @DisplayName("DELETE /user/{id} - Deve remover usuário")
  void shouldDeleteUser() {
    webClient
        .delete()
        .uri("/user/1")
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @DisplayName("DELETE /user - Deve deletar múltiplos usuários")
  void shouldDeleteMultipleUsers() {
    List<Long> ids = List.of(2L, 3L);

    webClient
        .method(HttpMethod.DELETE)
        .uri("/user")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .bodyValue(ids)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @DisplayName("POST /user/{id}/upload - Deve realizar upload de avatar")
  void shouldUploadAvatar() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "avatar.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});

    webClient
        .post()
        .uri("/user/1/upload")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(file)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("POST /user/{id}/upload - Deve falhar com arquivo vazio")
  void shouldFailToUploadEmptyAvatar() {
    MockMultipartFile empty = new MockMultipartFile(
        "file", "empty.png", MediaType.IMAGE_PNG_VALUE, new byte[0]);

    webClient
        .post()
        .uri("/user/1/upload")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(empty)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }
}
