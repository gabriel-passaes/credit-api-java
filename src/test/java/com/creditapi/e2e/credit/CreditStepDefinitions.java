package com.creditapi.e2e.credit;

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
public class CreditStepDefinitions {

  @Autowired
  private TestRestTemplate restTemplate;

  private ResponseEntity<String> response;

  @Dado("que o sistema de crédito está inicializado")
  public void creditSystemIsInitialized() {
    // Sistema pronto para testes
  }

  @Dado("que existe um crédito salvo com número CREDIT001 no sistema de crédito")
  public void creditExistsInCreditSystem() {
    // Mock ou setup de crédito já existente
  }

  @Quando("eu envio os dados válidos para criar um crédito")
  public void sendValidCreditPayload() {
    String payload = """
      {
        "creditNumber": "CREDIT123",
        "nfseNumber": "NFSE123",
        "constitutionDate": "2025-04-25",
        "issqnAmount": 100.0,
        "creditType": "ISSQN",
        "simpleNational": true,
        "rate": 5.0,
        "billedAmount": 2000.0,
        "deductionAmount": 500.0,
        "calculationBase": 1500.0,
        "userId": 1
      }
    """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    response = restTemplate.postForEntity("/credit", request, String.class);
  }

  @Então("o sistema de crédito deve retornar status 201 e os dados do crédito criado")
  public void shouldReturnCreatedCredit() {
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("CREDIT123"));
  }

  @Quando("eu faço uma requisição para buscar o crédito por número")
  public void requestCreditByCreditNumber() {
    response = restTemplate.getForEntity("/credit/CREDIT001", String.class);
  }

  @Então("o sistema de crédito deve retornar status 200 e os dados corretos do crédito")
  public void shouldReturnOkCreditData() {
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("CREDIT001"));
  }

  @Quando("eu envio um payload com valor inválido para o crédito")
  public void sendInvalidCreditPayload() {
    String payload = """
      {
        "creditNumber": "CREDIT123",
        "nfseNumber": "NFSE123",
        "constitutionDate": "2025-04-25",
        "issqnAmount": -100.0,
        "creditType": "ISSQN",
        "simpleNational": true,
        "rate": 5.0,
        "billedAmount": 2000.0,
        "deductionAmount": 500.0,
        "calculationBase": 1500.0,
        "userId": 1
      }
    """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    response = restTemplate.postForEntity("/credit", request, String.class);
  }

  @Então("o sistema de crédito deve retornar status 400 e uma mensagem de erro")
  public void shouldReturnBadRequestCredit() {
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().toLowerCase().contains("issqn"));
  }

  @Quando("eu faço uma requisição para buscar um crédito com número inexistente")
  public void requestNonExistentCredit() {
    response = restTemplate.getForEntity("/credit/INVALID", String.class);
  }

  @Então("o sistema de crédito deve retornar status 404")
  public void shouldReturnNotFoundCredit() {
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
