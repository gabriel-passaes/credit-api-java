package com.creditapi.application.user.service.query;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.mapper.UserMapper;
import com.creditapi.application.user.usecase.query.GetUserByEmailUseCase;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetUserByEmailService implements GetUserByEmailUseCase {

  private static final Logger logger = LoggerFactory.getLogger(GetUserByEmailService.class);
  private final UserRepository repository;

  public GetUserByEmailService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  @RateLimiter(name = "userService")
  public UserResponseDTO execute(String email) {
    logger.info("Buscando usuário por e-mail: {}", email);

    User user =
        repository
            .findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

    return UserMapper.toResponse(user);
  }
}
