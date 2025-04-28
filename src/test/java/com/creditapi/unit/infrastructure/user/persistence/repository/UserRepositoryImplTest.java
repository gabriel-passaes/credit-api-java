package com.creditapi.unit.infrastructure.user.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.creditapi.application.user.mapper.UserMapper;
import com.creditapi.domain.user.gateway.repository.UserRepository;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.user.persistence.entity.UserEntity;
import com.creditapi.infrastructure.user.persistence.repository.UserJpaRepository;

@Repository
public class UserRepositoryImplTest implements UserRepository {

  private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImplTest.class);
  private final UserJpaRepository jpa;

  public UserRepositoryImplTest(UserJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public User save(User user) {
    UserEntity entity = UserMapper.toEntity(user);
    UserEntity saved = jpa.save(entity);
    User domain = UserMapper.toDomain(saved);
    logger.info("User salvo ou atualizado com sucesso. ID: {}", domain.getId());
    return domain;
  }

  @Override
  public Optional<User> findById(Long id) {
    return jpa.findById(id).map(UserMapper::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jpa.findByEmail(email).map(UserMapper::toDomain);
  }

  @Override
  public boolean existsByCpf(String cpf) {
    return jpa.existsByDocument(cpf);
  }

  @Override
  public boolean existsByCnpj(String cnpj) {
    return jpa.existsByDocument(cnpj);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpa.existsByEmail(email);
  }

  @Override
  public void deleteById(Long id) {
    jpa.deleteById(id);
    logger.info("Usuário removido com sucesso. ID: {}", id);
  }

  @Override
  public void deleteAllById(List<Long> ids) {
    jpa.deleteAllByIdIn(ids);
    logger.info("Usuários removidos em lote: {}", ids);
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return jpa.findAll(pageable).map(UserMapper::toDomain);
  }

  @Override
  public Page<User> search(String name, String document, String email, Pageable pageable) {
    return jpa.search(name, document, email, pageable).map(UserMapper::toDomain);
  }
}
