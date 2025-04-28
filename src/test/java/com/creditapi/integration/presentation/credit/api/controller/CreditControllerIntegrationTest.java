package com.creditapi.integration.presentation.credit.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.creditapi.application.credit.dto.request.CreateCreditRequestDTO;
import com.creditapi.application.credit.dto.request.UpdateCreditRequestDTO;
import com.creditapi.application.credit.dto.search.CreditSearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CreditControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private CreateCreditRequestDTO createRequest;
  private UpdateCreditRequestDTO updateRequest;

  @BeforeEach
  void setup() {
    createRequest =
        new CreateCreditRequestDTO(
            "CREDIT001",
            "NF001",
            LocalDate.now(),
            BigDecimal.valueOf(1000),
            "ISSQN",
            true,
            BigDecimal.valueOf(5.0),
            BigDecimal.valueOf(10000),
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(9500),
            1L);

    updateRequest =
        new UpdateCreditRequestDTO(
            1L,
            "NF001",
            LocalDate.now(),
            BigDecimal.valueOf(2000),
            "ISSQN",
            true,
            BigDecimal.valueOf(7.0),
            BigDecimal.valueOf(12000),
            BigDecimal.valueOf(400),
            BigDecimal.valueOf(11600),
            1L);
  }

  @Test
  @DisplayName("POST /api/credits - Deve criar crédito com sucesso")
  void shouldCreateCreditSuccessfully() throws Exception {
    mockMvc
        .perform(
            post("/api/credits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("POST /api/credits - Payload inválido deve retornar 400")
  void shouldReturn400WhenInvalidPayload() throws Exception {
    mockMvc
        .perform(
            post("/api/credits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditType\": true}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /api/credits/credit/{creditNumber} - Deve buscar crédito por número")
  void shouldFindCreditByNumber() throws Exception {
    createCredit();

    mockMvc
        .perform(get("/api/credits/credit/CREDIT001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("GET /api/credits/credit/{creditNumber} - Deve retornar 404 se não encontrado")
  void shouldReturn404WhenCreditNotFound() throws Exception {
    mockMvc.perform(get("/api/credits/credit/INVALID")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/credits/nfse/{nfseNumber} - Deve listar créditos por NFS-e")
  void shouldListCreditsByNfse() throws Exception {
    createCredit();

    mockMvc
        .perform(get("/api/credits/nfse/NF001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nfseNumber").value("NF001"));
  }

  @Test
  @DisplayName(
      "GET /api/credits/nfse/{nfseNumber} - Deve retornar lista vazia se NFS-e não existir")
  void shouldReturnEmptyListWhenNfseNotFound() throws Exception {
    mockMvc
        .perform(get("/api/credits/nfse/NF404"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/credits - Deve listar créditos paginados")
  void shouldListCreditsPaginated() throws Exception {
    createCredit();

    mockMvc
        .perform(get("/api/credits?page=0&size=5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("POST /api/credits/search - Deve realizar busca avançada")
  void shouldPerformAdvancedSearch() throws Exception {
    createCredit();

    CreditSearchCriteria criteria =
        new CreditSearchCriteria(
            "CREDIT001",
            "NF001",
            "ISSQN",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));

    mockMvc
        .perform(
            post("/api/credits/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("PUT /api/credits/credit/{creditNumber} - Deve atualizar crédito")
  void shouldUpdateCredit() throws Exception {
    createCredit();

    mockMvc
        .perform(
            put("/api/credits/credit/CREDIT001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("DELETE /api/credits/credit/{creditNumber} - Deve excluir crédito")
  void shouldDeleteCredit() throws Exception {
    createCredit();

    mockMvc.perform(delete("/api/credits/credit/CREDIT001")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/credits - Deve excluir múltiplos créditos")
  void shouldDeleteMultipleCredits() throws Exception {
    createCredit();

    mockMvc
        .perform(delete("/api/credits").param("creditNumbers", "CREDIT001"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/credits - Deve retornar 400 se não enviar creditNumbers")
  void shouldReturn400WhenDeleteListMissing() throws Exception {
    mockMvc.perform(delete("/api/credits")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/credits/{creditId}/upload - Deve fazer upload de nota fiscal")
  void shouldUploadInvoice() throws Exception {
    createCredit();

    MockMultipartFile file =
        new MockMultipartFile("file", "nota.pdf", "application/pdf", "conteudo".getBytes());

    mockMvc.perform(multipart("/api/credits/1/upload").file(file)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /api/credits/{creditId}/upload - Deve retornar 400 se arquivo não enviado")
  void shouldReturn400WhenNoFileUpload() throws Exception {
    mockMvc.perform(multipart("/api/credits/1/upload")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /api/credits/{creditNumber}/download - Deve baixar nota fiscal")
  void shouldDownloadInvoice() throws Exception {
    createCredit();

    mockMvc
        .perform(get("/api/credits/CREDIT001/download"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/pdf"))
        .andExpect(
            header()
                .string("Content-Disposition", "attachment; filename=nota-fiscal-CREDIT001.pdf"));
  }

  @Test
  @DisplayName("POST /api/credits/{creditNumber}/send-email - Deve enviar nota por e-mail")
  void shouldSendEmailWithInvoice() throws Exception {
    createCredit();

    mockMvc
        .perform(post("/api/credits/CREDIT001/send-email").param("toEmail", "cliente@exemplo.com"))
        .andExpect(status().isOk());
  }

  private ResultActions createCredit() throws Exception {
    return mockMvc.perform(
        post("/api/credits")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)));
  }
}
