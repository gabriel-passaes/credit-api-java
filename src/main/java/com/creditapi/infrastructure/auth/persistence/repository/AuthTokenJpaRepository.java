package com.creditapi.infrastructure.auth.persistence.repository;

import com.creditapi.infrastructure.auth.persistence.entity.AuthTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokenJpaRepository extends JpaRepository<AuthTokenEntity, Long> {

  Optional<AuthTokenEntity> findByValue(String value);

  @Modifying
  @Query("UPDATE AuthTokenEntity a SET a.invalidated = true WHERE a.value = :value")
  void invalidateByValue(@Param("value") String value);

  @Modifying
  @Query("UPDATE AuthTokenEntity a SET a.invalidated = true WHERE a.userId = :userId")
  void invalidateByUserId(@Param("userId") Long userId);
}
