package com.creditapi.presentation.credit.api.controller;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/credits")
@Tag(name = "Créditos", description = "Operações de crédito")
public class CreditController {

  private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

  private final CreateCreditUseCase createUseCase;
  private final GetCreditsByNfseUseCase getCreditsByNfseUseCase;
  private final GetCreditByNumberUseCase getCreditByNumberUseCase;
  private final GetPaginatedCreditsUseCase getPaginatedCreditsUseCase;
  private final AdvancedSearchCreditsUseCase advancedSearchCreditsUseCase;
  private final UpdateCreditUseCase updateUseCase;
  private final DeleteCreditUseCase deleteUseCase;
  private final DeleteMultipleCreditsUseCase deleteMultipleUseCase;
  private final UploadCreditFileUseCase uploadUseCase;
  private final DownloadCreditInvoiceUseCase downloadUseCase;
  private final SendCreditByEmailUseCase sendEmailUseCase;

  public CreditController(
      CreateCreditUseCase createUseCase,
      GetCreditsByNfseUseCase getCreditsByNfseUseCase,
      GetCreditByNumberUseCase getCreditByNumberUseCase,
      GetPaginatedCreditsUseCase getPaginatedCreditsUseCase,
      AdvancedSearchCreditsUseCase advancedSearchCreditsUseCase,
      UpdateCreditUseCase updateUseCase,
      DeleteCreditUseCase deleteUseCase,
      DeleteMultipleCreditsUseCase deleteMultipleUseCase,
      UploadCreditFileUseCase uploadUseCase,
      DownloadCreditInvoiceUseCase downloadUseCase,
      SendCreditByEmailUseCase sendEmailUseCase) {
    this.createUseCase = createUseCase;
    this.getCreditsByNfseUseCase = getCreditsByNfseUseCase;
    this.getCreditByNumberUseCase = getCreditByNumberUseCase;
    this.getPaginatedCreditsUseCase = getPaginatedCreditsUseCase;
    this.advancedSearchCreditsUseCase = advancedSearchCreditsUseCase;
    this.updateUseCase = updateUseCase;
    this.deleteUseCase = deleteUseCase;
    this.deleteMultipleUseCase = deleteMultipleUseCase;
    this.uploadUseCase = uploadUseCase;
    this.downloadUseCase = downloadUseCase;
    this.sendEmailUseCase = sendEmailUseCase;
  }

  @PostMapping
  @Operation(summary = "Criar um novo crédito")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Crédito criado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
    @ApiResponse(responseCode = "422", description = "Crédito duplicado")
  })
  public ResponseEntity<CreateCreditResponseDTO> create(
      @RequestBody @Valid CreateCreditRequestDTO request) {
    logger.info("POST /api/credits - Criando crédito: {}", request.creditNumber());
    CreateCreditResponseDTO response = createUseCase.execute(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/nfse/{nfseNumber}")
  @Operation(summary = "Consultar créditos vinculados a uma NFS-e")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Créditos encontrados"),
    @ApiResponse(responseCode = "404", description = "Nenhum crédito encontrado")
  })
  public ResponseEntity<List<CreditResponseDTO>> getByNfse(@PathVariable String nfseNumber) {
    var list = getCreditsByNfseUseCase.execute(nfseNumber, Pageable.unpaged()).getContent();
    return ResponseEntity.ok(list);
  }

  @GetMapping
  @Operation(summary = "Listar todos os créditos com paginação")
  @ApiResponse(responseCode = "200", description = "Lista de créditos paginada")
  public ResponseEntity<Page<CreditResponseDTO>> getAllPaginated(Pageable pageable) {
    return ResponseEntity.ok(getPaginatedCreditsUseCase.execute(pageable));
  }

  @GetMapping("/credit/{creditNumber}")
  @Operation(summary = "Buscar crédito pelo número")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Crédito encontrado"),
    @ApiResponse(responseCode = "404", description = "Crédito não encontrado")
  })
  public ResponseEntity<CreditResponseDTO> getByNumber(@PathVariable String creditNumber) {
    return ResponseEntity.ok(getCreditByNumberUseCase.execute(creditNumber));
  }

  @PostMapping("/search")
  @Operation(summary = "Busca avançada de créditos com filtros")
  @ApiResponse(responseCode = "200", description = "Lista filtrada de créditos")
  public ResponseEntity<Page<CreditResponseDTO>> advancedSearch(
      @RequestBody CreditSearchCriteria criteria, Pageable pageable) {
    return ResponseEntity.ok(advancedSearchCreditsUseCase.execute(criteria, pageable));
  }

  @PutMapping("/credit/{creditNumber}")
  @Operation(summary = "Atualizar crédito existente")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Crédito atualizado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
    @ApiResponse(responseCode = "404", description = "Crédito não encontrado")
  })
  public ResponseEntity<CreditResponseDTO> update(
      @PathVariable String creditNumber, @RequestBody @Valid UpdateCreditRequestDTO request) {
    return ResponseEntity.ok(updateUseCase.execute(creditNumber, request));
  }

  @DeleteMapping("/credit/{creditNumber}")
  @Operation(summary = "Excluir crédito pelo número")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Crédito excluído com sucesso"),
    @ApiResponse(responseCode = "404", description = "Crédito não encontrado")
  })
  public ResponseEntity<Void> deleteOne(@PathVariable String creditNumber) {
    deleteUseCase.execute(creditNumber);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  @Operation(summary = "Excluir múltiplos créditos")
  @ApiResponse(responseCode = "204", description = "Créditos excluídos com sucesso")
  public ResponseEntity<Void> deleteMultiple(@RequestParam List<String> creditNumbers) {
    deleteMultipleUseCase.execute(creditNumbers);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{creditId}/upload")
  @Operation(summary = "Upload de nota fiscal (mock)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Upload realizado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
    @ApiResponse(responseCode = "404", description = "Crédito não encontrado")
  })
  public ResponseEntity<Void> uploadInvoice(
      @PathVariable Long creditId, @RequestParam("file") MultipartFile file) {
    uploadUseCase.execute(creditId, file);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{creditNumber}/download")
  @Operation(summary = "Download da nota fiscal em PDF")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Crédito não encontrado")
  })
  public ResponseEntity<byte[]> downloadInvoice(@PathVariable String creditNumber) {
    byte[] pdfBytes = downloadUseCase.execute(creditNumber);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(
        ContentDisposition.builder("attachment")
            .filename("nota-fiscal-" + creditNumber + ".pdf")
            .build());
    return ResponseEntity.ok().headers(headers).body(pdfBytes);
  }

  @PostMapping("/{creditNumber}/send-email")
  @Operation(summary = "Enviar nota fiscal por e-mail")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "E-mail enviado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Crédito não encontrado")
  })
  public ResponseEntity<Void> sendEmail(
      @PathVariable String creditNumber, @RequestParam String toEmail) {
    sendEmailUseCase.execute(creditNumber, toEmail);
    return ResponseEntity.ok().build();
  }
}
