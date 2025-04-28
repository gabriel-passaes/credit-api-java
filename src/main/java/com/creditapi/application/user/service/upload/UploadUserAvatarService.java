package com.creditapi.application.user.service.upload;

import com.creditapi.application.user.usecase.upload.UploadUserAvatarUseCase;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.shared.storage.StorageService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadUserAvatarService implements UploadUserAvatarUseCase {

  private static final Logger logger = LoggerFactory.getLogger(UploadUserAvatarService.class);
  private final UserRepository repository;
  private final StorageService storageService;

  @Value("${app.cdn.avatar-base-url}")
  private String avatarBaseUrl;

  public UploadUserAvatarService(UserRepository repository, StorageService storageService) {
    this.repository = repository;
    this.storageService = storageService;
  }

  @Override
  @RateLimiter(name = "userService")
  public void execute(Long id, MultipartFile file) {
    logger.info("Iniciando upload de avatar para usuário ID: {}", id);

    User user =
        repository
            .findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

    String avatarPath = "avatars/" + file.getOriginalFilename();

    storageService.upload(avatarPath, file);

    String avatarUrl = avatarBaseUrl + avatarPath;

    user.updateAvatar(avatarUrl);
    repository.save(user);

    logger.info("Avatar atualizado com sucesso para usuário ID: {}", id);
  }
}
