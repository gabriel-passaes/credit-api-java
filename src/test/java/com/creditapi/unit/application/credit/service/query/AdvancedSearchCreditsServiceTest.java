package com.creditapi.unit.application.credit.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.dto.search.CreditSearchCriteria;
import com.creditapi.application.credit.service.query.AdvancedSearchCreditsService;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.credit.model.CreditFilter;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.credit.strategy.StrategyContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("Testes para AdvancedSearchCreditsService")
class AdvancedSearchCreditsServiceTest {

  @Mock private CreditRepository creditRepository;

  @Mock private StrategyContext strategyContext;

  @InjectMocks private AdvancedSearchCreditsService advancedSearchCreditsService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve buscar créditos com critérios válidos e calcular imposto corretamente")
  void shouldSearchCreditsAndCalculateTaxSuccessfully() {
    CreditSearchCriteria criteria =
        new CreditSearchCriteria(
            "C001", "NF001", "ISSQN", LocalDate.now().minusDays(10), LocalDate.now());
    Pageable pageable = PageRequest.of(0, 10);

    Credit credit = buildCredit("C001", "NF001", "ISSQN");
    when(creditRepository.findAllByFilter(any(CreditFilter.class), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(credit)));
    when(strategyContext.calculate(eq("ISSQN"), eq(credit))).thenReturn(BigDecimal.valueOf(50));

    Page<CreditResponseDTO> page = advancedSearchCreditsService.execute(criteria, pageable);

    assertEquals(1, page.getTotalElements());
    assertEquals("C001", page.getContent().get(0).creditNumber());
    assertEquals(BigDecimal.valueOf(50), page.getContent().get(0).calculatedTax());
  }

  @Test
  @DisplayName("Deve lançar CreditNotFoundException se nenhum crédito for encontrado")
  void shouldThrowCreditNotFoundExceptionIfNoCreditsFound() {
    CreditSearchCriteria criteria = new CreditSearchCriteria("C002", "NF404", null, null, null);
    Pageable pageable = PageRequest.of(0, 5);

    when(creditRepository.findAllByFilter(any(CreditFilter.class), eq(pageable)))
        .thenReturn(Page.empty());

    assertThrows(
        CreditNotFoundException.class,
        () -> advancedSearchCreditsService.execute(criteria, pageable));
  }

  @Test
  @DisplayName("Deve buscar créditos ignorando campos nulos nos filtros")
  void shouldSearchCreditsIgnoringNullFilters() {
    CreditSearchCriteria criteria = new CreditSearchCriteria(null, null, null, null, null);
    Pageable pageable = PageRequest.of(0, 5);

    Credit credit = buildCredit("C003", "NF002", "OUTROS");
    when(creditRepository.findAllByFilter(any(CreditFilter.class), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(credit)));
    when(strategyContext.calculate(any(), any())).thenReturn(BigDecimal.valueOf(25));

    Page<CreditResponseDTO> page = advancedSearchCreditsService.execute(criteria, pageable);

    assertEquals(1, page.getTotalElements());
    assertEquals("C003", page.getContent().get(0).creditNumber());
  }

  @Test
  @DisplayName("Deve retornar múltiplos créditos paginados e calcular impostos")
  void shouldReturnMultiplePaginatedCredits() {
    Credit creditA = buildCredit("C004", "NF003", "ISSQN");
    Credit creditB = buildCredit("C005", "NF004", "OUTROS");
    Pageable pageable = PageRequest.of(0, 2);

    when(creditRepository.findAllByFilter(any(CreditFilter.class), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(creditA, creditB)));
    when(strategyContext.calculate("ISSQN", creditA)).thenReturn(BigDecimal.valueOf(10));
    when(strategyContext.calculate("OUTROS", creditB)).thenReturn(BigDecimal.valueOf(20));

    Page<CreditResponseDTO> page =
        advancedSearchCreditsService.execute(
            new CreditSearchCriteria(null, null, null, null, null), pageable);

    assertEquals(2, page.getTotalElements());
    assertEquals(BigDecimal.valueOf(10), page.getContent().get(0).calculatedTax());
    assertEquals(BigDecimal.valueOf(20), page.getContent().get(1).calculatedTax());
  }

  @Test
  @DisplayName("Deve lidar com página vazia sem lançar exceção extra")
  void shouldHandleEmptyPageGracefully() {
    CreditSearchCriteria criteria = new CreditSearchCriteria("X", "NF-X", "ISSQN", null, null);
    Pageable pageable = PageRequest.of(0, 10);

    when(creditRepository.findAllByFilter(any(CreditFilter.class), eq(pageable)))
        .thenReturn(Page.empty());

    assertThrows(
        CreditNotFoundException.class,
        () -> advancedSearchCreditsService.execute(criteria, pageable));
  }

  @Test
  @DisplayName("Deve lançar exceção se cálculo de imposto falhar")
  void shouldThrowExceptionIfTaxCalculationFails() {
    Credit credit = buildCredit("C007", "NF007", "ISSQN");
    Pageable pageable = PageRequest.of(0, 1);

    when(creditRepository.findAllByFilter(any(CreditFilter.class), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(credit)));
    when(strategyContext.calculate(any(), any()))
        .thenThrow(new RuntimeException("Falha no cálculo de imposto"));

    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () ->
                advancedSearchCreditsService.execute(
                    new CreditSearchCriteria(null, null, null, null, null), pageable));

    assertEquals("Falha no cálculo de imposto", ex.getMessage());
  }

  @Test
  @DisplayName("Deve calcular impostos diferentes para tipos de crédito diferentes")
  void shouldCalculateDifferentTaxesForDifferentTypes() {
    Credit issqn = buildCredit("C008", "NF008", "ISSQN");
    Credit outros = buildCredit("C009", "NF009", "OUTROS");
    Pageable pageable = PageRequest.of(0, 2);

    when(creditRepository.findAllByFilter(any(CreditFilter.class), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(issqn, outros)));
    when(strategyContext.calculate("ISSQN", issqn)).thenReturn(BigDecimal.valueOf(12));
    when(strategyContext.calculate("OUTROS", outros)).thenReturn(BigDecimal.valueOf(7));

    Page<CreditResponseDTO> page =
        advancedSearchCreditsService.execute(
            new CreditSearchCriteria(null, null, null, null, null), pageable);

    assertEquals(2, page.getContent().size());
    assertEquals(BigDecimal.valueOf(12), page.getContent().get(0).calculatedTax());
    assertEquals(BigDecimal.valueOf(7), page.getContent().get(1).calculatedTax());
  }

  private Credit buildCredit(String creditNumber, String nfseNumber, String creditType) {
    return Credit.builder()
        .id(1L)
        .creditNumber(creditNumber)
        .nfseNumber(nfseNumber)
        .constitutionDate(LocalDate.now())
        .issqnAmount(BigDecimal.valueOf(100))
        .creditType(creditType)
        .simpleNational(true)
        .rate(BigDecimal.valueOf(5))
        .billedAmount(BigDecimal.valueOf(500))
        .deductionAmount(BigDecimal.ZERO)
        .calculationBase(BigDecimal.valueOf(500))
        .user(
            User.builder()
                .id(1L)
                .name("Test User")
                .email("user@example.com")
                .document("12345678900")
                .role(Role.USER)
                .build())
        .build();
  }
}
