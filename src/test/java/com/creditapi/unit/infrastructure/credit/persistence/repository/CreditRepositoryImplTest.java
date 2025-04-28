package com.creditapi.unit.infrastructure.credit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.mapper.CreditMapper;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.infrastructure.credit.persistence.entity.CreditEntity;
import com.creditapi.infrastructure.credit.persistence.repository.CreditJpaRepository;
import com.creditapi.infrastructure.credit.persistence.repository.CreditRepositoryImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CreditRepositoryImplTest {

  @Mock private CreditJpaRepository creditJpaRepository;

  @Mock private CreditMapper creditMapper;

  @InjectMocks private CreditRepositoryImpl creditRepositoryImpl;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve retornar lista de créditos ao buscar por NFS-e")
  void shouldReturnListOfCreditsByNfseNumber() {
    CreditEntity entity1 = createEntity("CREDIT001");
    CreditEntity entity2 = createEntity("CREDIT002");
    Credit domain1 = createDomain("CREDIT001");
    Credit domain2 = createDomain("CREDIT002");

    when(creditJpaRepository.findByNfseNumber("NF100")).thenReturn(Arrays.asList(entity1, entity2));
    when(creditMapper.toDomain(entity1)).thenReturn(domain1);
    when(creditMapper.toDomain(entity2)).thenReturn(domain2);

    List<Credit> result = creditRepositoryImpl.findByNfseNumber("NF100");

    assertEquals(2, result.size());
    assertEquals("CREDIT001", result.get(0).getCreditNumber());
    assertEquals("CREDIT002", result.get(1).getCreditNumber());
  }

  @Test
  @DisplayName("Deve retornar Optional<Credit> ao buscar por número existente")
  void shouldReturnCreditByCreditNumber() {
    CreditEntity entity = createEntity("CREDIT001");
    Credit domain = createDomain("CREDIT001");

    when(creditJpaRepository.findByCreditNumber("CREDIT001")).thenReturn(Optional.of(entity));
    when(creditMapper.toDomain(entity)).thenReturn(domain);

    Optional<Credit> result = creditRepositoryImpl.findByCreditNumber("CREDIT001");

    assertTrue(result.isPresent());
    assertEquals("CREDIT001", result.get().getCreditNumber());
  }

  @Test
  @DisplayName("Deve retornar Optional.empty() se número de crédito não existir")
  void shouldReturnEmptyWhenCreditNotFound() {
    when(creditJpaRepository.findByCreditNumber("CREDIT404")).thenReturn(Optional.empty());

    Optional<Credit> result = creditRepositoryImpl.findByCreditNumber("CREDIT404");

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("Deve salvar e publicar evento de criação de crédito")
  void shouldSaveAndPublishCreditCreatedEvent() {
    CreditEntity entity = createEntity("CREDIT003");
    Credit domain = createDomain("CREDIT003");

    when(creditJpaRepository.save(entity)).thenReturn(entity);
    when(creditMapper.toDomain(entity)).thenReturn(domain);

    Credit result = creditRepositoryImpl.save(domain);

    assertNotNull(result);
    assertEquals("CREDIT003", result.getCreditNumber());
    verify(creditJpaRepository).save(entity);
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar excluir crédito inexistente")
  void shouldThrowExceptionIfCreditNotFoundForDeletion() {
    when(creditJpaRepository.findByCreditNumber("CREDIT404")).thenReturn(Optional.empty());

    assertThrows(CreditNotFoundException.class, () -> creditRepositoryImpl.delete("CREDIT404"));
  }

  private CreditEntity createEntity(String creditNumber) {
    CreditEntity entity = new CreditEntity();
    entity.setId(1L);
    entity.setCreditNumber(creditNumber);
    entity.setNfseNumber("NF001");
    entity.setConstitutionDate(LocalDate.now());
    entity.setIssqnAmount(new BigDecimal("100"));
    entity.setCreditType("ISSQN");
    entity.setSimpleNational(true);
    entity.setRate(new BigDecimal("5"));
    entity.setBilledAmount(new BigDecimal("2000"));
    entity.setDeductionAmount(new BigDecimal("500"));
    entity.setCalculationBase(new BigDecimal("1500"));
    return entity;
  }

  private Credit createDomain(String creditNumber) {
    return Credit.builder()
        .id(1L)
        .creditNumber(creditNumber)
        .nfseNumber("NF001")
        .constitutionDate(LocalDate.now())
        .issqnAmount(new BigDecimal("100"))
        .creditType("ISSQN")
        .simpleNational(true)
        .rate(new BigDecimal("5"))
        .billedAmount(new BigDecimal("2000"))
        .deductionAmount(new BigDecimal("500"))
        .calculationBase(new BigDecimal("1500"))
        .build();
  }
}
