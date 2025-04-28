package com.creditapi.infrastructure.shared.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Profile("test") 
@Component
public class FakeStorageService implements StorageService {

  @Override
  public void upload(String path, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Arquivo inválido");
    }
  }

  @Override
  public InputStream download(String path) {
    return new ByteArrayInputStream("arquivo simulado".getBytes());
  }
}
