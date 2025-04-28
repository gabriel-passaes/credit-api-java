package com.creditapi.integration.presentation.auth.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("deve retornar 400 ao tentar registrar sem nome")
  void shouldReturn400WhenRegisterWithoutName() throws Exception {
    var body =
        """
        {
            "email": "email@email.com",
            "password": "senha123"
        }
        """;

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("deve retornar 400 ao tentar registrar com e-mail inválido")
  void shouldReturn400WhenRegisterWithInvalidEmail() throws Exception {
    var body =
        """
        {
            "name": "João",
            "email": "email_invalido",
            "password": "senha123"
        }
        """;

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("deve retornar 400 ao tentar registrar sem senha")
  void shouldReturn400WhenRegisterWithoutPassword() throws Exception {
    var body =
        """
        {
            "name": "João",
            "email": "joao@email.com"
        }
        """;

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("deve retornar 401 ao tentar login com senha incorreta")
  void shouldReturn401WhenLoginWithInvalidPassword() throws Exception {
    var body =
        """
        {
            "email": "user@email.com",
            "password": "senha_incorreta"
        }
        """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("deve retornar 400 para token de refresh vazio")
  void shouldReturn400WhenRefreshTokenIsBlank() throws Exception {
    var body = 
        """
        {
            "refreshToken": ""
        }
        """;

    mockMvc
        .perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("deve retornar 404 ao recuperar senha com e-mail inexistente")
  void shouldReturn404WhenRecoverPasswordWithUnknownEmail() throws Exception {
    var body =
        """
        {
            "email": "email-inexistente@email.com"
        }
        """;

    mockMvc
        .perform(post("/auth/recover").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("deve retornar 400 ao resetar senha com token inválido")
  void shouldReturn400WhenResetPasswordWithInvalidToken() throws Exception {
    var body =
        """
        {
            "token": "token_invalido",
            "newPassword": "novaSenha123"
        }
        """;

    mockMvc
        .perform(post("/auth/reset").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("deve retornar 400 ao realizar login social com body inválido")
  void shouldReturn400WhenSocialLoginWithEmptyFields() throws Exception {
    var body =
        """
        {
            "provider": "",
            "token": ""
        }
        """;

    mockMvc
        .perform(post("/auth/social").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("deve retornar 401 ao realizar logout sem estar autenticado")
  void shouldReturn401WhenLogoutAllWithoutAuth() throws Exception {
    mockMvc.perform(post("/auth/logout/all")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("deve retornar 400 ao tentar logout com refresh token vazio")
  void shouldReturn400WhenLogoutWithBlankToken() throws Exception {
    var body = 
        """
        {
            "refreshToken": ""
        }
        """;

    mockMvc
        .perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
  }
}
