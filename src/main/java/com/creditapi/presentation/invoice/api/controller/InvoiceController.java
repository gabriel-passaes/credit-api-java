package com.creditapi.presentation.invoice.api.controller;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import com.creditapi.application.invoice.dto.response.InvoiceStatusResponseDTO;
import com.creditapi.application.invoice.usecase.download.DownloadInvoiceUseCase;
import com.creditapi.application.invoice.usecase.status.ConsultInvoiceStatusUseCase;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice")
@Tag(name = "Invoice", description = "Endpoints relacionados a notas fiscais")
public class InvoiceController {

  private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

  private final ConsultInvoiceStatusUseCase consultStatusUseCase;
  private final DownloadInvoiceUseCase downloadUseCase;

  public InvoiceController(
      ConsultInvoiceStatusUseCase consultStatusUseCase, DownloadInvoiceUseCase downloadUseCase) {
    this.consultStatusUseCase = consultStatusUseCase;
    this.downloadUseCase = downloadUseCase;
  }

  @PostMapping("/status")
  @Operation(
      summary = "Consultar status de nota fiscal",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Status consultado com sucesso",
            content = @Content(schema = @Schema(implementation = InvoiceStatusResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "404", description = "Nota fiscal não encontrada")
      })
  @Cacheable(value = "invoice-status", key = "#request.invoiceNumber() + '-' + #request.cnpj()")
  public ResponseEntity<InvoiceStatusResponseDTO> consultStatus(
      @RequestBody @Valid DownloadInvoiceRequestDTO request) {
    logger.info(
        "📄 Requisição recebida para consultar status da nota fiscal: {}", request.invoiceNumber());

    InvoiceStatusResponseDTO response = consultStatusUseCase.execute(request);

    logger.info("✅ Status da nota retornado: {}", response.status());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/download")
  @Operation(
      summary = "Realizar download do PDF da nota fiscal",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Download realizado com sucesso",
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "404", description = "Nota fiscal não encontrada")
      })
  @RateLimiter(name = "invoice-download")
  public ResponseEntity<byte[]> downloadInvoice(
      @RequestBody @Valid DownloadInvoiceRequestDTO request) {
    logger.info("📥 Requisição recebida para download da nota fiscal: {}", request.invoiceNumber());

    byte[] pdf = downloadUseCase.execute(request);

    String filename =
        String.format(
            "NF-%s_%s.pdf",
            request.invoiceNumber(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

    logger.info("📎 Download gerado com nome do arquivo: {}", filename);
    return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
  }
}
