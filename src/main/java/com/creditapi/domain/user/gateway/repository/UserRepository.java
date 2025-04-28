package com.creditapi.domain.user.gateway.repository;

import com.creditapi.domain.user.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

  User save(User user);

  Optional<User> findById(Long id);

  Optional<User> findByEmail(String email);

  boolean existsByCpf(String cpf);

  boolean existsByCnpj(String cnpj);

  boolean existsByEmail(String email);

  void deleteById(Long id);

  void deleteAllById(List<Long> ids);

  Page<User> findAll(Pageable pageable);

  Page<User> search(String name, String document, String email, Pageable pageable);
}
