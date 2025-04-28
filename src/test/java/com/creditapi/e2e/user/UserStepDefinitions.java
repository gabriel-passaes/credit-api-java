package com.creditapi.e2e.user;

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
public class UserStepDefinitions {

  @Autowired
  private TestRestTemplate restTemplate;

  private ResponseEntity<String> response;

  @Dado("que o sistema de usuários está inicializado")
  public void userSystemIsInitialized() {
    // Sistema pronto para testes
  }

  @Dado("que existe um usuário salvo com ID 1")
  public void userExistsWithIdOne() {
    // Mock ou setup de usuário já existente
  }

  @Quando("eu envio os dados válidos para criar um usuário")
  public void sendValidUserPayload() {
    String payload = """
      {
        "name": "Gabriel Bittencourt",
        "email": "gabriel@example.com",
        "document": "12345678900",
        "role": "USER"
      }
    """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    response = restTemplate.postForEntity("/user", request, String.class);
  }

  @Então("o sistema deve retornar status 201 e os dados do usuário criado")
  public void shouldReturnCreatedStatusAndUserData() {
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Gabriel"));
  }

  @Quando("eu faço uma requisição para buscar o usuário por ID")
  public void requestUserById() {
    response = restTemplate.getForEntity("/user/1", String.class);
  }

  @Então("o sistema deve retornar status 200 e os dados corretos do usuário")
  public void shouldReturnOkAndUserData() {
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Quando("eu envio um payload com email inválido")
  public void sendInvalidEmailPayload() {
    String payload = """
      {
        "name": "Invalid User",
        "email": "emailinvalido",
        "document": "12345678900",
        "role": "USER"
      }
    """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    response = restTemplate.postForEntity("/user", request, String.class);
  }

  @Então("o sistema deve retornar status 400 e uma mensagem de erro")
  public void shouldReturnBadRequestAndErrorMessage() {
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().toLowerCase().contains("email"));
  }

  @Quando("eu faço uma requisição para buscar o usuário com ID inexistente")
  public void requestNonExistentUser() {
    response = restTemplate.getForEntity("/user/99999", String.class);
  }

  @Então("o sistema deve retornar status 404 de usuário não encontrado")
  public void shouldReturnUserNotFound() {
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
