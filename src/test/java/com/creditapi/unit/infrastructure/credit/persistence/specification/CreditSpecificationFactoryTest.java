package com.creditapi.unit.infrastructure.credit.persistence.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.creditapi.domain.credit.model.CreditFilter;
import com.creditapi.infrastructure.credit.persistence.entity.CreditEntity;
import com.creditapi.infrastructure.credit.persistence.specification.CreditSpecificationFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

class CreditSpecificationFactoryTest {

  @InjectMocks private CreditSpecificationFactory creditSpecificationFactory;

  @Mock private CriteriaBuilder cb;

  @Mock private CriteriaQuery<?> query;

  @Mock private Root<CreditEntity> root;

  @Mock private Predicate predicate;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve gerar predicado correto com filtro de NFS-e")
  void shouldGeneratePredicateForNfseNumber() {
    CreditFilter filter = new CreditFilter();
    filter.setNfseNumber("NF001");

    Specification<CreditEntity> spec = creditSpecificationFactory.build(filter);
    Predicate result = spec.toPredicate(root, query, cb);

    // Verifica se o predicado foi criado corretamente com o filtro
    verify(cb).equal(root.get("nfseNumber"), "NF001");
  }

  @Test
  @DisplayName("Não deve gerar predicado para filtro de NFS-e nulo")
  void shouldNotGeneratePredicateForNullNfseNumber() {
    CreditFilter filter = new CreditFilter();
    filter.setNfseNumber(null);

    Specification<CreditEntity> spec = creditSpecificationFactory.build(filter);
    Predicate result = spec.toPredicate(root, query, cb);

    // Verifica que o predicado não é criado quando o filtro é nulo
    verify(cb, never()).equal(root.get("nfseNumber"), any());
  }

  @Test
  @DisplayName("Deve gerar predicado para múltiplos filtros aplicados")
  void shouldGeneratePredicatesForMultipleFilters() {
    CreditFilter filter = new CreditFilter();
    filter.setNfseNumber("NF001");
    filter.setCreditNumber("CREDIT001");
    filter.setCreatedAfter(LocalDate.now().minusDays(10));

    Specification<CreditEntity> spec = creditSpecificationFactory.build(filter);
    Predicate result = spec.toPredicate(root, query, cb);

    // Verifica se o predicado foi criado para cada filtro
    verify(cb).equal(root.get("nfseNumber"), "NF001");
    verify(cb).equal(root.get("creditNumber"), "CREDIT001");
    verify(cb).greaterThanOrEqualTo(root.get("constitutionDate"), LocalDate.now().minusDays(10));
  }

  @Test
  @DisplayName("Deve gerar predicado de join com filtro de userId")
  void shouldGenerateJoinPredicateForUserId() {
    CreditFilter filter = new CreditFilter();
    filter.setUserId(1L);

    Specification<CreditEntity> spec = creditSpecificationFactory.build(filter);
    Predicate result = spec.toPredicate(root, query, cb);

    // Verifica se o join foi feito corretamente
    verify(cb).equal(root.join("user").get("id"), 1L);
  }
}
