package com.creditapi.unit.infrastructure.shared.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.creditapi.infrastructure.shared.storage.FakeStorageService;
import com.creditapi.infrastructure.shared.storage.StorageService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FakeStorageServiceTest {

  private StorageService storageService;

  @BeforeEach
  void setup() {
    storageService = new FakeStorageService();
  }

  @Test
  @DisplayName("Deve simular upload com sucesso")
  void shouldSimulateUploadSuccessfully() throws IOException {
    MultipartFile file =
        new MockMultipartFile("file", "nota.pdf", "application/pdf", "conteúdo".getBytes());
    assertDoesNotThrow(() -> storageService.upload("credits/1/nota.pdf", file));
  }

  @Test
  @DisplayName("Deve lançar exceção se o arquivo for nulo ou vazio")
  void shouldThrowIfFileIsNull() {
    assertThrows(
        IllegalArgumentException.class, () -> storageService.upload("credits/1/test.pdf", null));

    MultipartFile empty = new MockMultipartFile("file", "nota.pdf", "application/pdf", new byte[0]);
    assertThrows(
        IllegalArgumentException.class, () -> storageService.upload("credits/1/test.pdf", empty));
  }
}
