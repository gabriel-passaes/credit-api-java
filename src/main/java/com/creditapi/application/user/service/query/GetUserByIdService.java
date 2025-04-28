package com.creditapi.application.user.service.query;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.mapper.UserMapper;
import com.creditapi.application.user.usecase.query.GetUserByIdUseCase;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdService implements GetUserByIdUseCase {

  private static final Logger logger = LoggerFactory.getLogger(GetUserByIdService.class);
  private final UserRepository repository;

  public GetUserByIdService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  @RateLimiter(name = "userService")
  public UserResponseDTO execute(Long id) {
    logger.info("Buscando usuário pelo ID: {}", id);

    User user =
        repository
            .findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

    return UserMapper.toResponse(user);
  }
}
