package com.creditapi.infrastructure.credit.persistence.repository;

import com.creditapi.application.credit.mapper.CreditMapper;
import com.creditapi.domain.credit.gateway.event.CreditEventPublisher;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.credit.model.CreditFilter;
import com.creditapi.infrastructure.credit.persistence.entity.CreditEntity;
import com.creditapi.infrastructure.credit.persistence.specification.CreditSpecificationFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CreditRepositoryImpl implements CreditRepository {

  private static final Logger logger = LoggerFactory.getLogger(CreditRepositoryImpl.class);

  private final CreditJpaRepository jpa;
  private final CreditMapper mapper;
  private final CreditEventPublisher eventPublisher;

  public CreditRepositoryImpl(
      CreditJpaRepository jpa, CreditMapper mapper, CreditEventPublisher eventPublisher) {
    this.jpa = jpa;
    this.mapper = mapper;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public List<Credit> findByNfseNumber(String nfseNumber) {
    return jpa.findByNfseNumber(nfseNumber).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Credit> findByCreditNumber(String creditNumber) {
    return jpa.findByCreditNumber(creditNumber).map(mapper::toDomain);
  }

  @Override
  public Credit save(Credit credit) {
    CreditEntity entity = mapper.toEntity(credit);
    CreditEntity saved = jpa.save(entity);
    Credit domain = mapper.toDomain(saved);

    if (credit.getId() == null) {
      eventPublisher.publishCreditCreated(domain);
      logger.info(
          "CreditRepositoryImpl: evento CREATED publicado para '{}'", domain.getCreditNumber());
    } else {
      eventPublisher.publishCreditUpdated(domain);
      logger.info(
          "CreditRepositoryImpl: evento UPDATED publicado para '{}'", domain.getCreditNumber());
    }
    return domain;
  }

  @Override
  public void delete(String creditNumber) {
    findByCreditNumber(creditNumber).ifPresent(eventPublisher::publishCreditDeleted);
    jpa.deleteByCreditNumber(creditNumber);
    logger.info(
        "CreditRepositoryImpl: crédito '{}' excluído e evento DELETED publicado", creditNumber);
  }

  @Override
  public boolean existsByCreditNumber(String creditNumber) {
    return jpa.existsByCreditNumber(creditNumber);
  }

  @Override
  public List<Credit> findAll() {
    return jpa.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Page<Credit> findAll(Pageable pageable) {
    return jpa.findAll(pageable).map(mapper::toDomain);
  }

  @Override
  public Page<Credit> findByNfseNumber(String nfseNumber, Pageable pageable) {
    return jpa.findByNfseNumber(nfseNumber, pageable).map(mapper::toDomain);
  }

  @Override
  public void deleteAll(List<String> creditNumbers) {
    creditNumbers.forEach(
        n -> {
          findByCreditNumber(n).ifPresent(eventPublisher::publishCreditDeleted);
          logger.debug("CreditRepositoryImpl: evento DELETED publicado para '{}'", n);
        });
    jpa.deleteAllByCreditNumberIn(creditNumbers);
    logger.info(
        "CreditRepositoryImpl: exclusão em massa de {} créditos concluída", creditNumbers.size());
  }

  @Override
  public List<Credit> findAllByFilter(CreditFilter filter) {
    var spec = CreditSpecificationFactory.build(filter);
    return jpa.findAll(spec).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Page<Credit> findAllByFilter(CreditFilter filter, Pageable pageable) {
    var spec = CreditSpecificationFactory.build(filter);
    return jpa.findAll(spec, pageable).map(mapper::toDomain);
  }
}
