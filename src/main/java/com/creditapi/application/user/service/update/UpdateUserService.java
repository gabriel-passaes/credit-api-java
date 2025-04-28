package com.creditapi.application.user.service.update;

import com.creditapi.application.user.dto.request.UpdateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.mapper.UserMapper;
import com.creditapi.application.user.usecase.update.UpdateUserUseCase;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserService implements UpdateUserUseCase {

  private static final Logger logger = LoggerFactory.getLogger(UpdateUserService.class);
  private final UserRepository repository;

  public UpdateUserService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  @RateLimiter(name = "userService")
  public UserResponseDTO execute(Long id, UpdateUserRequestDTO request) {
    logger.info("Atualizando usuário ID: {}", id);

    User user =
        repository
            .findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

    user.update(request.name(), request.email(), request.document());

    User updated = repository.save(user);
    logger.info("Usuário ID: {} atualizado com sucesso", updated.getId());

    return UserMapper.toResponse(updated);
  }
}
