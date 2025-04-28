package com.creditapi.infrastructure.credit.persistence.repository;

import com.creditapi.infrastructure.credit.persistence.entity.CreditEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditJpaRepository
    extends JpaRepository<CreditEntity, Long>, JpaSpecificationExecutor<CreditEntity> {

  List<CreditEntity> findByNfseNumber(String nfseNumber);

  Optional<CreditEntity> findByCreditNumber(String creditNumber);

  boolean existsByCreditNumber(String creditNumber);

  void deleteByCreditNumber(String creditNumber);

  void deleteAllByCreditNumberIn(List<String> creditNumbers);

  Page<CreditEntity> findAll(Pageable pageable);

  Page<CreditEntity> findByNfseNumber(String nfseNumber, Pageable pageable);
}
