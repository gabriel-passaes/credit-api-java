package com.creditapi.application.user.service.search;

import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.dto.search.UserSearchDTO;
import com.creditapi.application.user.mapper.UserMapper;
import com.creditapi.application.user.usecase.search.SearchUserUseCase;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchUserService implements SearchUserUseCase {

  private static final Logger logger = LoggerFactory.getLogger(SearchUserService.class);
  private final UserRepository repository;

  public SearchUserService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  @Cacheable(
      value = "users-advanced-search",
      key = "#search + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
  @RateLimiter(name = "userService")
  public Page<UserResponseDTO> execute(UserSearchDTO search, Pageable pageable) {
    logger.info(
        "Busca avançada: filtro={}, página={}, tamanho={}",
        search,
        pageable.getPageNumber(),
        pageable.getPageSize());

    return repository
        .search(search.name(), search.document(), search.email(), pageable)
        .map(UserMapper::toResponse);
  }
}
