package com.creditapi.e2e.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthStepDefinitions {

  @Autowired
  private TestRestTemplate restTemplate;

  private ResponseEntity<String> response;

  private final String validEmail = "auth@example.com";
  private final String validPassword = "StrongPassword123!";
  private final String validRefreshToken = "mock-refresh-token";

  @Dado("que o sistema de autenticação está inicializado")
  public void authSystemIsInitialized() {
    // Contexto inicializado automaticamente
  }

  @Dado("que existe um usuário cadastrado no sistema de autenticação")
  public void userIsRegisteredInAuthSystem() {
    // Pré-condição mockada
  }

  @Quando("eu envio email e senha válidos para autenticação")
  public void sendValidLoginCredentials() {
    String payload = """
      {
        "email": "%s",
        "password": "%s"
      }
      """.formatted(validEmail, validPassword);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    response = restTemplate.postForEntity("/auth/login", request, String.class);
  }

  @Então("o sistema de autenticação deve retornar status 200 e um token JWT válido")
  public void shouldReturnAuthToken() {
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("accessToken"));
  }

  @Quando("eu envio o email correto e senha incorreta para autenticação")
  public void sendWrongPasswordForAuth() {
    String payload = """
      {
        "email": "%s",
        "password": "senhaErrada"
      }
      """.formatted(validEmail);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    response = restTemplate.postForEntity("/auth/login", request, String.class);
  }

  @Então("o sistema de autenticação deve retornar status 401")
  public void shouldReturnUnauthorizedFromAuth() {
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Quando("eu envio email de usuário não cadastrado para autenticação")
  public void sendNonExistentEmailForAuth() {
    String payload = """
      {
        "email": "naoexiste@example.com",
        "password": "qualquerSenha"
      }
      """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    response = restTemplate.postForEntity("/auth/login", request, String.class);
  }

  @Então("o sistema de autenticação deve retornar status 404")
  public void shouldReturnNotFoundFromAuth() {
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Dado("que tenho um refresh token válido de autenticação")
  public void haveValidRefreshToken() {
    // Pré-condição simulada
  }

  @Quando("eu solicito um novo access token de autenticação")
  public void requestNewAccessToken() {
    String payload = """
      {
        "refreshToken": "%s"
      }
      """.formatted(validRefreshToken);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    response = restTemplate.postForEntity("/auth/refresh", request, String.class);
  }

  @Então("o sistema de autenticação deve retornar status 200 e um novo token JWT")
  public void shouldReturnNewAuthToken() {
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("accessToken"));
  }
}
