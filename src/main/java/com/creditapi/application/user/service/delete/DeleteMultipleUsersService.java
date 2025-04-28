package com.creditapi.application.user.service.delete;

import com.creditapi.application.user.usecase.delete.DeleteMultipleUsersUseCase;
import com.creditapi.domain.user.exception.UnauthorizedUserOperationException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeleteMultipleUsersService implements DeleteMultipleUsersUseCase {

  private static final Logger logger = LoggerFactory.getLogger(DeleteMultipleUsersService.class);
  private final UserRepository repository;

  public DeleteMultipleUsersService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  @RateLimiter(name = "userService")
  public void execute(User executor, List<Long> userIdsToDelete) {
    logger.info("Usuário ID: {} solicitou exclusão de múltiplos usuários", executor.getId());

    if (executor.getRole() != Role.SUPER_ADMIN) {
      logger.warn(
          "Usuário ID: {} tentou deletar múltiplos usuários sem permissão", executor.getId());
      throw new UnauthorizedUserOperationException(
          "Apenas SUPER_ADMIN pode deletar múltiplos usuários");
    }

    repository.deleteAllById(userIdsToDelete);
    logger.info("Usuários deletados com sucesso: {}", userIdsToDelete);
  }
}
