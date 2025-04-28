package com.creditapi.unit.infrastructure.credit.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.infrastructure.credit.storage.CreditStorageServiceImpl;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

class CreditStorageServiceImplTest {

  @Mock private MultipartFile file;

  @InjectMocks private CreditStorageServiceImpl storageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve fazer upload corretamente para o caminho especificado")
  void shouldUploadFileSuccessfully() throws IOException {
    String path = "credit/1/nota.pdf";
    String rootDir = "mock-storage";
    Path destination = Paths.get(rootDir, path);

    doNothing().when(file).transferTo(destination.toFile());

    storageService.upload(path, file);

    verify(file, times(1)).transferTo(destination.toFile());
  }

  @Test
  @DisplayName("Deve lançar exceção ao falhar no upload")
  void shouldThrowExceptionWhenUploadFails() throws IOException {
    String path = "credit/1/nota.pdf";
    String rootDir = "mock-storage";
    Path destination = Paths.get(rootDir, path);

    doThrow(new IOException("Falha no upload")).when(file).transferTo(destination.toFile());

    IOException exception =
        assertThrows(IOException.class, () -> storageService.upload(path, file));
    assertEquals("Falha no upload do arquivo: Falha no upload", exception.getMessage());
  }

  @Test
  @DisplayName("Deve fazer o download corretamente do arquivo")
  void shouldDownloadFileSuccessfully() throws IOException {
    String path = "credit/1/nota.pdf";
    String rootDir = "mock-storage";
    Path fullPath = Paths.get(rootDir, path);

    FileInputStream inputStream = mock(FileInputStream.class);
    when(inputStream.available()).thenReturn(100);

    when(new FileInputStream(fullPath.toFile())).thenReturn(inputStream);

    FileInputStream result = (FileInputStream) storageService.download(path);

    assertNotNull(result);
    assertEquals(inputStream, result);
  }

  @Test
  @DisplayName("Deve lançar exceção quando arquivo não for encontrado para download")
  void shouldThrowExceptionWhenFileNotFoundForDownload() throws IOException {
    String path = "credit/1/nota.pdf";
    String rootDir = "mock-storage";
    Path fullPath = Paths.get(rootDir, path);

    doThrow(new IOException("Arquivo não encontrado")).when(file).transferTo(fullPath.toFile());

    IOException exception = assertThrows(IOException.class, () -> storageService.download(path));
    assertEquals("Arquivo não encontrado: credit/1/nota.pdf", exception.getMessage());
  }
}
