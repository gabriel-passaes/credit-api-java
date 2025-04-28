package com.creditapi.application.credit.usecase.upload;

import org.springframework.web.multipart.MultipartFile;

public interface UploadCreditFileUseCase {
  void execute(Long creditId, MultipartFile file);
}
