package com.creditapi.unit.application.credit.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.service.query.GetPaginatedCreditsService;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
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

@DisplayName("Testes para GetPaginatedCreditsService")
class GetPaginatedCreditsServiceTest {

  @Mock private CreditRepository creditRepository;

  @Mock private StrategyContext strategyContext;

  @InjectMocks private GetPaginatedCreditsService getPaginatedCreditsService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve retornar créditos paginados com imposto calculado corretamente")
  void shouldReturnPaginatedCreditsWithCalculatedTax() {
    Pageable pageable = PageRequest.of(0, 2);

    Credit creditISSQN = createCredit("CREDIT_ISSQN", "ISSQN");
    Credit creditService = createCredit("CREDIT_SERVICE", "SERVICE");

    Page<Credit> creditPage = new PageImpl<>(List.of(creditISSQN, creditService), pageable, 2);

    when(creditRepository.findAll(pageable)).thenReturn(creditPage);
    when(strategyContext.calculate(eq("ISSQN"), eq(creditISSQN)))
        .thenReturn(BigDecimal.valueOf(50));
    when(strategyContext.calculate(eq("SERVICE"), eq(creditService)))
        .thenReturn(BigDecimal.valueOf(30));

    Page<CreditResponseDTO> result = getPaginatedCreditsService.execute(pageable);

    assertEquals(2, result.getTotalElements());
    assertEquals("CREDIT_ISSQN", result.getContent().get(0).creditNumber());
    assertEquals(BigDecimal.valueOf(50), result.getContent().get(0).calculatedTax());
    assertEquals("CREDIT_SERVICE", result.getContent().get(1).creditNumber());
    assertEquals(BigDecimal.valueOf(30), result.getContent().get(1).calculatedTax());

    verify(creditRepository, times(1)).findAll(pageable);
    verify(strategyContext, times(2)).calculate(any(), any());
  }

  @Test
  @DisplayName("Deve retornar página vazia sem falhar")
  void shouldReturnEmptyPageWhenNoCreditsExist() {
    Pageable pageable = PageRequest.of(0, 1);

    when(creditRepository.findAll(pageable)).thenReturn(Page.empty());

    Page<CreditResponseDTO> result = getPaginatedCreditsService.execute(pageable);

    assertEquals(0, result.getTotalElements());
    verify(creditRepository, times(1)).findAll(pageable);
    verifyNoInteractions(strategyContext);
  }

  @Test
  @DisplayName("Deve lançar exceção se cálculo de imposto falhar para algum crédito")
  void shouldThrowExceptionIfTaxCalculationFails() {
    Pageable pageable = PageRequest.of(0, 1);

    Credit problematicCredit = createCredit("CREDIT_FAIL", "PROBLEM_TYPE");

    Page<Credit> creditPage = new PageImpl<>(List.of(problematicCredit), pageable, 1);

    when(creditRepository.findAll(pageable)).thenReturn(creditPage);
    when(strategyContext.calculate(eq("PROBLEM_TYPE"), eq(problematicCredit)))
        .thenThrow(new RuntimeException("Erro ao calcular imposto"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> getPaginatedCreditsService.execute(pageable));

    assertEquals("Erro ao calcular imposto", exception.getMessage());
  }

  private Credit createCredit(String creditNumber, String creditType) {
    User user =
        User.builder()
            .id(1L)
            .name("Test User")
            .email("test.user@example.com")
            .document("12345678900")
            .role(Role.USER)
            .build();

    return Credit.builder()
        .id(1L)
        .creditNumber(creditNumber)
        .nfseNumber("NFSE001")
        .constitutionDate(LocalDate.now())
        .issqnAmount(BigDecimal.valueOf(1000))
        .creditType(creditType)
        .simpleNational(false)
        .rate(BigDecimal.valueOf(5))
        .billedAmount(BigDecimal.valueOf(10000))
        .deductionAmount(BigDecimal.valueOf(500))
        .calculationBase(BigDecimal.valueOf(9500))
        .user(user)
        .build();
  }
}
