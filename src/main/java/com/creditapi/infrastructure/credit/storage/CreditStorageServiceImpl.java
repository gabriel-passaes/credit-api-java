package com.creditapi.infrastructure.credit.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.creditapi.infrastructure.shared.storage.StorageService;

@Primary // 👉 Indica que é o principal quando houver dúvida
@Service
public class CreditStorageServiceImpl implements StorageService {

  private static final Logger logger = LoggerFactory.getLogger(CreditStorageServiceImpl.class);
  private static final String ROOT_DIR = "mock-storage";

  @Override
  public void upload(String path, MultipartFile file) {
    try {
      Path destination = Paths.get(ROOT_DIR, path);
      Files.createDirectories(destination.getParent());
      file.transferTo(destination.toFile());
      logger.info("📤 Upload simulado concluído para arquivo: {}", destination);
    } catch (IOException e) {
      logger.error("Erro ao simular upload para o path {}", path, e);
      throw new RuntimeException("Falha no upload do arquivo: " + e.getMessage(), e);
    }
  }

  @Override
  public InputStream download(String path) {
    try {
      Path fullPath = Paths.get(ROOT_DIR, path);
      logger.info("📥 Lendo arquivo de: {}", fullPath);
      return new FileInputStream(fullPath.toFile());
    } catch (FileNotFoundException e) {
      logger.error("Arquivo não encontrado para download: {}", path);
      throw new RuntimeException("Arquivo não encontrado: " + path, e);
    }
  }
}
