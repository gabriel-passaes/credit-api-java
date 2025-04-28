package com.creditapi.unit.application.credit.service.upload;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.service.upload.UploadCreditFileService;
import com.creditapi.application.credit.usecase.upload.UploadCreditFileUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.infrastructure.shared.storage.StorageService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("Testes para UploadCreditFileService")
class UploadCreditFileServiceTest {

  private StorageService storageService;
  private CreditRepository creditRepository;
  private UploadCreditFileUseCase useCase;

  @BeforeEach
  void setUp() {
    storageService = mock(StorageService.class);
    creditRepository = mock(CreditRepository.class);
    useCase = new UploadCreditFileService(storageService, creditRepository);
  }

  @Test
  @DisplayName("Deve fazer upload corretamente e atualizar o crédito com nome e caminho do arquivo")
  void shouldUploadAndUpdateCreditSuccessfully() {
    Long creditId = 1L;
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("nota.pdf");

    Credit credit = new Credit();
    credit.setId(creditId);
    when(creditRepository.findAll()).thenReturn(List.of(credit));
    when(creditRepository.save(any())).thenReturn(credit);

    useCase.execute(creditId, file);

    verify(storageService).upload(eq("credits/1/nota.pdf"), eq(file));
    verify(creditRepository)
        .save(
            argThat(
                updated ->
                    "nota.pdf".equals(updated.getUploadedFileName())
                        && "credits/1/nota.pdf".equals(updated.getUploadedFilePath())
                        && updated.isInvoiceUploaded()));
  }

  @Test
  @DisplayName("Deve lançar IllegalArgumentException se MultipartFile for nulo")
  void shouldThrowExceptionIfFileIsNull() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, null));
    assertEquals("Arquivo inválido ou nome ausente", ex.getMessage());
  }

  @Test
  @DisplayName("Deve lançar IllegalArgumentException se nome do arquivo for nulo")
  void shouldThrowExceptionIfFilenameIsNull() {
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn(null);

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, file));
    assertEquals("Arquivo inválido ou nome ausente", ex.getMessage());
  }

  @Test
  @DisplayName("Deve lançar CreditNotFoundException se crédito não for encontrado")
  void shouldThrowExceptionIfCreditNotFound() {
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("nota.pdf");
    when(creditRepository.findAll()).thenReturn(List.of());

    CreditNotFoundException ex =
        assertThrows(CreditNotFoundException.class, () -> useCase.execute(1L, file));
    assertTrue(ex.getMessage().contains("Crédito não encontrado"));
  }

  @Test
  @DisplayName("Deve lançar RuntimeException se upload falhar")
  void shouldThrowIfUploadFails() {
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("nota.pdf");
    when(creditRepository.findAll())
        .thenReturn(
            List.of(
                new Credit() {
                  {
                    setId(1L);
                  }
                }));
    doThrow(new RuntimeException("falha no storage")).when(storageService).upload(any(), eq(file));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.execute(1L, file));
    assertEquals("falha no storage", ex.getMessage());
  }

  @Test
  @DisplayName("Deve permitir nome de arquivo com espaços e caracteres especiais")
  void shouldHandleSpecialFilenames() {
    Long creditId = 99L;
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("nota fiscal 2024 - versão final.pdf");

    Credit credit = new Credit();
    credit.setId(creditId);
    when(creditRepository.findAll()).thenReturn(List.of(credit));
    when(creditRepository.save(any())).thenReturn(credit);

    assertDoesNotThrow(() -> useCase.execute(creditId, file));

    verify(storageService).upload(eq("credits/99/nota fiscal 2024 - versão final.pdf"), eq(file));
    verify(creditRepository).save(any());
  }
}
