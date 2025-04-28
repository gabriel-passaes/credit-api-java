package com.creditapi.unit.presentation.invoice.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;
import com.creditapi.application.invoice.service.download.DownloadInvoiceService;
import com.creditapi.application.invoice.service.status.ConsultInvoiceStatusService;
import com.creditapi.domain.invoice.exception.InvoiceNotFoundException;
import com.creditapi.presentation.invoice.api.controller.InvoiceController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerUnitTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ConsultInvoiceStatusService consultUseCase;

  @MockBean private DownloadInvoiceService downloadUseCase;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("Deve retornar 200 e status da nota quando sucesso")
  void shouldReturn200WhenConsultStatusSuccess() throws Exception {
    var request = new DownloadInvoiceRequestDTO("123456", "11222333000144");
    var response =
        new InvoiceStatusResponseDTO(
            "AUTORIZADA", "Status retornado pela TiraNota: AUTORIZADA", LocalDateTime.now());

    Mockito.when(consultUseCase.execute(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/invoice/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("AUTORIZADA"))
        .andExpect(jsonPath("$.message").value("Status retornado pela TiraNota: AUTORIZADA"));
  }

  @Test
  @DisplayName("Deve retornar 404 quando nota não for encontrada na consulta")
  void shouldReturn404WhenStatusNotFound() throws Exception {
    var request = new DownloadInvoiceRequestDTO("000", "000");

    Mockito.when(consultUseCase.execute(any()))
        .thenThrow(new InvoiceNotFoundException("Nota não encontrada"));

    mockMvc
        .perform(
            post("/invoice/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Deve retornar 200 e conteúdo PDF ao baixar nota")
  void shouldReturnPdfWhenDownloadSuccess() throws Exception {
    var request = new DownloadInvoiceRequestDTO("123", "456");

    Mockito.when(downloadUseCase.execute(any())).thenReturn(new byte[] {1, 2, 3});

    mockMvc
        .perform(
            post("/invoice/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_PDF));
  }

  @Test
  @DisplayName("Deve retornar 404 ao tentar baixar nota inexistente")
  void shouldReturn404WhenDownloadNotFound() throws Exception {
    var request = new DownloadInvoiceRequestDTO("000", "000");

    Mockito.when(downloadUseCase.execute(any()))
        .thenThrow(new InvoiceNotFoundException("Nota não encontrada"));

    mockMvc
        .perform(
            post("/invoice/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Deve retornar 400 se body da requisição for inválido")
  void shouldReturn400ForInvalidBody() throws Exception {
    var request = new DownloadInvoiceRequestDTO("", "");

    mockMvc
        .perform(
            post("/invoice/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Deve retornar 500 em erro interno inesperado no status")
  void shouldReturn500WhenUnexpectedErrorInStatus() throws Exception {
    var request = new DownloadInvoiceRequestDTO("123", "456");

    Mockito.when(consultUseCase.execute(any())).thenThrow(new RuntimeException("Erro inesperado"));

    mockMvc
        .perform(
            post("/invoice/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("Deve retornar 500 em erro inesperado no download")
  void shouldReturn500WhenUnexpectedErrorInDownload() throws Exception {
    var request = new DownloadInvoiceRequestDTO("123", "456");

    Mockito.when(downloadUseCase.execute(any())).thenThrow(new RuntimeException("Falha geral"));

    mockMvc
        .perform(
            post("/invoice/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("Deve retornar PDF mesmo com poucos bytes (caso de borda)")
  void shouldReturnMinimalPdfInEdgeCase() throws Exception {
    var request = new DownloadInvoiceRequestDTO("999", "999");
    Mockito.when(downloadUseCase.execute(any())).thenReturn(new byte[] {0x25});

    mockMvc
        .perform(
            post("/invoice/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_PDF));
  }
}
