package com.creditapi.integration.presentation.invoice.api.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class InvoiceControllerIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private WebTestClient webClient;

  @Test
  @DisplayName("POST /invoice/status - Deve retornar status da nota com sucesso")
  void shouldReturnStatusSuccessfully() {
    var request = new DownloadInvoiceRequestDTO("123456", "11222333000144");

    webClient
        .post()
        .uri("/invoice/status")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(OK)
        .expectBody()
        .jsonPath("$.status")
        .isNotEmpty()
        .jsonPath("$.message")
        .isNotEmpty();
  }

  @Test
  @DisplayName("POST /invoice/status - Deve retornar 404 para nota inexistente")
  void shouldReturn404ForNonexistentStatus() {
    var request = new DownloadInvoiceRequestDTO("000000", "00000000000000");

    webClient
        .post()
        .uri("/invoice/status")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("POST /invoice/status - Deve retornar 400 para dados inválidos")
  void shouldReturn400ForInvalidStatusRequest() {
    var request = new DownloadInvoiceRequestDTO("", "");

    webClient
        .post()
        .uri("/invoice/status")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(BAD_REQUEST);
  }

  @Test
  @DisplayName("POST /invoice/download - Deve retornar PDF com sucesso")
  void shouldDownloadPdfSuccessfully() {
    var request = new DownloadInvoiceRequestDTO("123456", "11222333000144");

    webClient
        .post()
        .uri("/invoice/download")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(OK)
        .expectHeader()
        .contentType(MediaType.APPLICATION_PDF)
        .expectBody(byte[].class)
        .consumeWith(response -> {
          byte[] body = response.getResponseBody();
          assert body != null;
          assert body.length > 0;
        });
  }

  @Test
  @DisplayName("POST /invoice/download - Deve retornar 404 para nota inexistente")
  void shouldReturn404ForDownloadNotFound() {
    var request = new DownloadInvoiceRequestDTO("000000", "00000000000000");

    webClient
        .post()
        .uri("/invoice/download")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("POST /invoice/download - Deve retornar 400 para requisição inválida")
  void shouldReturn400ForInvalidDownloadRequest() {
    var request = new DownloadInvoiceRequestDTO("", "");

    webClient
        .post()
        .uri("/invoice/download")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(BAD_REQUEST);
  }

  @Test
  @DisplayName("POST /invoice/download - Deve retornar PDF mínimo no caso de borda")
  void shouldReturnMinimalPdfInEdgeCase() {
    var request = new DownloadInvoiceRequestDTO("EDGE", "11111111111111");

    webClient
        .post()
        .uri("/invoice/download")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(OK)
        .expectHeader()
        .contentType(MediaType.APPLICATION_PDF)
        .expectBody()
        .consumeWith(result -> {
          byte[] body = result.getResponseBody();
          assert body != null;
          assert body.length >= 1;
        });
  }

  @Test
  @DisplayName("POST /invoice/status - Deve simular erro 500 em falha geral (mock)")
  void shouldSimulate500InStatus() {
    var request = new DownloadInvoiceRequestDTO("999ERR", "11111111111111");

    webClient
        .post()
        .uri("/invoice/status")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("POST /invoice/download - Deve simular erro 500 em falha geral (mock)")
  void shouldSimulate500InDownload() {
    var request = new DownloadInvoiceRequestDTO("999ERR", "11111111111111");

    webClient
        .post()
        .uri("/invoice/download")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isEqualTo(INTERNAL_SERVER_ERROR);
  }
}
