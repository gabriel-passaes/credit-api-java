package com.creditapi.application.user.service.query;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.mapper.UserMapper;
import com.creditapi.application.user.usecase.query.GetUserWithPaginationUseCase;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetUserWithPaginationService implements GetUserWithPaginationUseCase {

  private static final Logger logger = LoggerFactory.getLogger(GetUserWithPaginationService.class);
  private final UserRepository repository;

  public GetUserWithPaginationService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  @Observed(name = "user.paginated-list")
  @Cacheable(value = "users-paginated", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
  @RateLimiter(name = "userService")
  public Page<UserResponseDTO> execute(Pageable pageable) {
    logger.info(
        "Buscando usuários paginados (page={}, size={})",
        pageable.getPageNumber(),
        pageable.getPageSize());

    return repository.findAll(pageable).map(UserMapper::toResponse);
  }
}
