package com.creditapi.unit.presentation.credit.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.creditapi.application.credit.dto.request.CreateCreditRequestDTO;
import com.creditapi.application.credit.dto.request.UpdateCreditRequestDTO;
import com.creditapi.application.credit.dto.response.CreateCreditResponseDTO;
import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.dto.search.CreditSearchCriteria;
import com.creditapi.application.credit.usecase.create.CreateCreditUseCase;
import com.creditapi.application.credit.usecase.delete.DeleteCreditUseCase;
import com.creditapi.application.credit.usecase.delete.DeleteMultipleCreditsUseCase;
import com.creditapi.application.credit.usecase.download.DownloadCreditInvoiceUseCase;
import com.creditapi.application.credit.usecase.email.SendCreditByEmailUseCase;
import com.creditapi.application.credit.usecase.query.AdvancedSearchCreditsUseCase;
import com.creditapi.application.credit.usecase.query.GetCreditByNumberUseCase;
import com.creditapi.application.credit.usecase.query.GetCreditsByNfseUseCase;
import com.creditapi.application.credit.usecase.query.GetPaginatedCreditsUseCase;
import com.creditapi.application.credit.usecase.update.UpdateCreditUseCase;
import com.creditapi.application.credit.usecase.upload.UploadCreditFileUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.presentation.credit.api.controller.CreditController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CreditController.class)
class CreditControllerUnitTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CreateCreditUseCase createUseCase;
  @MockBean private GetCreditsByNfseUseCase byNfseUseCase;
  @MockBean private GetCreditByNumberUseCase byNumberUseCase;
  @MockBean private GetPaginatedCreditsUseCase paginatedUseCase;
  @MockBean private AdvancedSearchCreditsUseCase advancedSearchUseCase;
  @MockBean private UpdateCreditUseCase updateUseCase;
  @MockBean private DeleteCreditUseCase deleteUseCase;
  @MockBean private DeleteMultipleCreditsUseCase deleteMultipleUseCase;
  @MockBean private UploadCreditFileUseCase uploadUseCase;
  @MockBean private DownloadCreditInvoiceUseCase downloadUseCase;
  @MockBean private SendCreditByEmailUseCase sendEmailUseCase;

  private ObjectMapper objectMapper;
  private CreditResponseDTO responseDTO;
  private CreateCreditRequestDTO requestDTO;
  private UpdateCreditRequestDTO updateDTO;
  private CreateCreditResponseDTO createResponseDTO;

  @BeforeEach
  void setup() {
    objectMapper = new ObjectMapper();

    responseDTO =
        new CreditResponseDTO(
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
            BigDecimal.valueOf(750),
            1L,
            "Fulano",
            "fulano@email.com");

    requestDTO =
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

    updateDTO =
        new UpdateCreditRequestDTO(
            1L,
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

    createResponseDTO =
        new CreateCreditResponseDTO(
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
            BigDecimal.valueOf(750),
            1L,
            "Fulano",
            "fulano@email.com");
  }

  @Test
  @DisplayName("POST /api/credits - Deve criar crédito com sucesso")
  void shouldCreateCreditSuccessfully() throws Exception {
    when(createUseCase.execute(any())).thenReturn(createResponseDTO);

    mockMvc
        .perform(
            post("/api/credits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("POST /api/credits - Payload inválido retorna 400")
  void shouldReturn400ForInvalidPayload() throws Exception {
    mockMvc
        .perform(
            post("/api/credits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditType\": true}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /api/credits/credit/{creditNumber} - Crédito encontrado")
  void shouldGetCreditByNumber() throws Exception {
    when(byNumberUseCase.execute("CREDIT001")).thenReturn(responseDTO);

    mockMvc
        .perform(get("/api/credits/credit/CREDIT001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("GET /api/credits/credit/{creditNumber} - Crédito não encontrado")
  void shouldReturn404WhenCreditNotFound() throws Exception {
    when(byNumberUseCase.execute("INVALID")).thenThrow(new CreditNotFoundException("not found"));

    mockMvc.perform(get("/api/credits/credit/INVALID")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/credits/nfse/{nfseNumber} - Créditos encontrados por NFS-e")
  void shouldGetCreditsByNfse() throws Exception {
    when(byNfseUseCase.execute(eq("NF001"), any()))
        .thenReturn(new PageImpl<>(List.of(responseDTO)));

    mockMvc
        .perform(get("/api/credits/nfse/NF001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nfseNumber").value("NF001"));
  }

  @Test
  @DisplayName("GET /api/credits/nfse/{nfseNumber} - Nenhum crédito para NFS-e")
  void shouldReturnEmptyListForNfse() throws Exception {
    when(byNfseUseCase.execute(eq("NF404"), any())).thenReturn(Page.empty());

    mockMvc
        .perform(get("/api/credits/nfse/NF404"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/credits - Paginação de créditos")
  void shouldGetPaginatedCredits() throws Exception {
    when(paginatedUseCase.execute(any())).thenReturn(new PageImpl<>(List.of(responseDTO)));

    mockMvc
        .perform(get("/api/credits?page=0&size=5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("POST /api/credits/search - Busca avançada")
  void shouldPerformAdvancedSearch() throws Exception {
    CreditSearchCriteria criteria =
        new CreditSearchCriteria(
            "C001", "NF001", "ISSQN", LocalDate.now().minusDays(5), LocalDate.now());

    when(advancedSearchUseCase.execute(eq(criteria), any()))
        .thenReturn(new PageImpl<>(List.of(responseDTO)));

    mockMvc
        .perform(
            post("/api/credits/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("PUT /api/credits/credit/{creditNumber} - Atualiza crédito")
  void shouldUpdateCredit() throws Exception {
    when(updateUseCase.execute(eq("CREDIT001"), any())).thenReturn(responseDTO);

    mockMvc
        .perform(
            put("/api/credits/credit/CREDIT001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.creditNumber").value("CREDIT001"));
  }

  @Test
  @DisplayName("DELETE /api/credits/credit/{creditNumber} - Exclui crédito")
  void shouldDeleteCredit() throws Exception {
    mockMvc.perform(delete("/api/credits/credit/CREDIT001")).andExpect(status().isNoContent());

    verify(deleteUseCase).execute("CREDIT001");
  }

  @Test
  @DisplayName("DELETE /api/credits - Exclui múltiplos créditos")
  void shouldDeleteMultipleCredits() throws Exception {
    mockMvc
        .perform(delete("/api/credits").param("creditNumbers", "C1", "C2"))
        .andExpect(status().isNoContent());

    verify(deleteMultipleUseCase).execute(List.of("C1", "C2"));
  }

  @Test
  @DisplayName("DELETE /api/credits - Falha ao omitir creditNumbers")
  void shouldReturn400IfDeleteListEmpty() throws Exception {
    mockMvc.perform(delete("/api/credits")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/credits/{creditId}/upload - Upload nota fiscal")
  void shouldUploadInvoice() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "nota.pdf", "application/pdf", "dados".getBytes());

    mockMvc.perform(multipart("/api/credits/1/upload").file(file)).andExpect(status().isOk());

    verify(uploadUseCase).execute(eq(1L), any());
  }

  @Test
  @DisplayName("POST /api/credits/{creditId}/upload - Upload sem arquivo")
  void shouldReturn400IfNoFile() throws Exception {
    mockMvc.perform(multipart("/api/credits/1/upload")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/credits/{creditId}/upload - Crédito não encontrado")
  void shouldReturn404OnUploadWhenCreditNotFound() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "nota.pdf", "application/pdf", "dados".getBytes());

    doThrow(new CreditNotFoundException("não encontrado"))
        .when(uploadUseCase)
        .execute(eq(1L), any());

    mockMvc.perform(multipart("/api/credits/1/upload").file(file)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/credits/{creditNumber}/download - Download nota fiscal")
  void shouldDownloadInvoice() throws Exception {
    byte[] pdf = "conteudo-mock".getBytes();
    when(downloadUseCase.execute("CREDIT001")).thenReturn(pdf);

    mockMvc
        .perform(get("/api/credits/CREDIT001/download"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/pdf"))
        .andExpect(
            header()
                .string("Content-Disposition", "attachment; filename=nota-fiscal-CREDIT001.pdf"))
        .andExpect(content().bytes(pdf));
  }

  @Test
  @DisplayName("GET /api/credits/{creditNumber}/download - Crédito não encontrado")
  void shouldReturn404OnDownloadNotFound() throws Exception {
    when(downloadUseCase.execute("X")).thenThrow(new CreditNotFoundException("não encontrado"));

    mockMvc.perform(get("/api/credits/X/download")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/credits/{creditNumber}/send-email - Envia e-mail")
  void shouldSendEmailWithInvoice() throws Exception {
    mockMvc
        .perform(post("/api/credits/CREDIT001/send-email").param("toEmail", "cliente@exemplo.com"))
        .andExpect(status().isOk());

    verify(sendEmailUseCase).execute(eq("CREDIT001"), eq("cliente@exemplo.com"));
  }

  @Test
  @DisplayName("POST /api/credits/{creditNumber}/send-email - Crédito não encontrado")
  void shouldReturn404OnSendEmailIfNotFound() throws Exception {
    doThrow(new CreditNotFoundException("não encontrado"))
        .when(sendEmailUseCase)
        .execute(eq("CREDIT001"), any());

    mockMvc
        .perform(post("/api/credits/CREDIT001/send-email").param("toEmail", "cliente@exemplo.com"))
        .andExpect(status().isNotFound());
  }
}
