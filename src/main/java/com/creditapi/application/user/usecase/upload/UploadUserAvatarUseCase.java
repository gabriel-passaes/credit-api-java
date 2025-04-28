package com.creditapi.application.user.usecase.upload;

import org.springframework.web.multipart.MultipartFile;

public interface UploadUserAvatarUseCase {
  void execute(Long id, MultipartFile file);
}
