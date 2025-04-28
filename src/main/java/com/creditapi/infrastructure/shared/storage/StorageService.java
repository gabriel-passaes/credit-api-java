package com.creditapi.infrastructure.shared.storage;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  void upload(String path, MultipartFile file);

  InputStream download(String path);
}
