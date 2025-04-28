package com.creditapi.application.user.service.create;

import com.creditapi.application.user.dto.request.CreateUserRequestDTO;
import com.creditapi.application.user.dto.response.UserResponseDTO;
import com.creditapi.application.user.mapper.UserMapper;
import com.creditapi.application.user.usecase.create.CreateUserUseCase;
import com.creditapi.domain.user.exception.CnpjAlreadyRegisteredException;
import com.creditapi.domain.user.exception.CpfAlreadyRegisteredException;
import com.creditapi.domain.user.exception.EmailAlreadyRegisteredException;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

  private static final Logger log = LoggerFactory.getLogger(CreateUserService.class);

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  public CreateUserService(UserRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @RateLimiter(name = "userService")
  public UserResponseDTO execute(CreateUserRequestDTO request) {
    log.info("Criando novo usuário com e-mail: {}", request.email());

    if (repository.existsByEmail(request.email())) {
      throw new EmailAlreadyRegisteredException("E-mail já está em uso");
    }

    if (request.document().length() == 11 && repository.existsByCpf(request.document())) {
      throw new CpfAlreadyRegisteredException("CPF já registrado");
    }

    if (request.document().length() == 14 && repository.existsByCnpj(request.document())) {
      throw new CnpjAlreadyRegisteredException("CNPJ já registrado");
    }

    String encodedPassword = passwordEncoder.encode(request.password());
    Role defaultRole = Role.USER;

    User user = UserMapper.fromCreateDto(request, defaultRole, encodedPassword);
    User saved = repository.save(user);

    log.info("Usuário criado com ID: {}", saved.getId());
    return UserMapper.toResponse(saved);
  }
}
