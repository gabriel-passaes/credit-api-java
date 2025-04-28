package com.creditapi.application.user.service.delete;

import com.creditapi.application.user.usecase.delete.DeleteUserUseCase;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserService implements DeleteUserUseCase {

  private static final Logger logger = LoggerFactory.getLogger(DeleteUserService.class);
  private final UserRepository repository;

  public DeleteUserService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  @RateLimiter(name = "userService")
  public void execute(Long id) {
    logger.info("Removendo usuário ID: {}", id);

    if (repository.findById(id).isEmpty()) {
      throw new UserNotFoundException("Usuário não encontrado");
    }

    repository.deleteById(id);
    logger.info("Usuário ID: {} removido com sucesso", id);
  }
}
