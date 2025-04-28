package com.creditapi.e2e.invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InvoiceStepDefinitions {

  @Autowired
  private TestRestTemplate restTemplate;

  private ResponseEntity<byte[]> binaryResponse;
  private ResponseEntity<String> response;

  @Dado("que existe uma nota fiscal salva com ID 1")
  public void invoiceExistsWithId1() {
    // Setup mock de nota fiscal existente
  }

  @Quando("eu faço uma requisição para baixar a nota")
  public void sendRequestToDownloadInvoice() {
    binaryResponse = restTemplate.getForEntity("/invoice/1/download", byte[].class);
  }

  @Então("o sistema deve retornar status 200 e um PDF válido")
  public void shouldReturnInvoicePDF() {
    assertEquals(HttpStatus.OK, binaryResponse.getStatusCode());
    assertEquals(MediaType.APPLICATION_PDF, binaryResponse.getHeaders().getContentType());
    assertNotNull(binaryResponse.getBody());
    assertTrue(binaryResponse.getBody().length > 100);
  }

  @Quando("eu faço uma requisição para baixar a nota com ID inválido")
  public void sendRequestToDownloadInvalidInvoice() {
    response = restTemplate.getForEntity("/invoice/99999/download", String.class);
  }

  @Então("o sistema deve retornar status 404 de nota fiscal não encontrada")
  public void shouldReturnInvoiceNotFound() {
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Quando("eu faço upload de um arquivo PDF válido para a nota com ID 1")
  public void uploadValidPdfToInvoice() {
    byte[] fakePdf = {0x25, 0x50, 0x44, 0x46}; // '%PDF'
    Resource fileResource = new ByteArrayResource(fakePdf) {
      @Override
      public String getFilename() { return "nota.pdf"; }
    };

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", fileResource);

    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
    response = restTemplate.postForEntity("/invoice/1/upload", request, String.class);
  }

  @Então("o sistema deve retornar status 200 e uma confirmação")
  public void shouldReturnUploadConfirmation() {
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().toLowerCase().contains("upload"));
  }

  @Quando("eu faço upload de um arquivo .txt para a nota com ID 1")
  public void uploadInvalidFileToInvoice() {
    byte[] fakeTxt = "isso não é um PDF".getBytes();
    Resource fileResource = new ByteArrayResource(fakeTxt) {
      @Override
      public String getFilename() { return "nota.txt"; }
    };

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", fileResource);

    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
    response = restTemplate.postForEntity("/invoice/1/upload", request, String.class);
  }

  @Então("o sistema deve retornar status 400 com mensagem de erro")
  public void shouldReturnInvalidFileError() {
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().toLowerCase().contains("formato") || response.getBody().toLowerCase().contains("erro"));
  }
}
