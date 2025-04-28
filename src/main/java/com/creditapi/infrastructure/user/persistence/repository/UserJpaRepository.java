package com.creditapi.infrastructure.user.persistence.repository;

import com.creditapi.infrastructure.user.persistence.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository
    extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

  Optional<UserEntity> findByEmail(String email);

  boolean existsByDocument(String document);

  boolean existsByEmail(String email);

  void deleteAllByIdIn(List<Long> ids);

  @Query(
      """
           SELECT u
           FROM UserEntity u
           WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))
             AND (:document IS NULL OR u.document LIKE CONCAT('%', :document, '%'))
             AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
           """)
  Page<UserEntity> search(
      @Param("name") String name,
      @Param("document") String document,
      @Param("email") String email,
      Pageable pageable);
}
