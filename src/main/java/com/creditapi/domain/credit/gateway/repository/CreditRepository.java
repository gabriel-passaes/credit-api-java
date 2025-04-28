package com.creditapi.domain.credit.gateway.repository;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.credit.model.CreditFilter;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CreditRepository {

  List<Credit> findByNfseNumber(String nfseNumber);

  Optional<Credit> findByCreditNumber(String creditNumber);

  boolean existsByCreditNumber(String creditNumber);

  Credit save(Credit credit);

  void delete(String creditNumber);

  void deleteAll(List<String> creditNumbers);

  List<Credit> findAll();

  Page<Credit> findAll(Pageable pageable);

  Page<Credit> findByNfseNumber(String nfseNumber, Pageable pageable);

  List<Credit> findAllByFilter(CreditFilter filter);

  Page<Credit> findAllByFilter(CreditFilter filter, Pageable pageable);
}
