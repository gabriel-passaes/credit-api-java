package com.creditapi.unit.application.credit.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.service.query.GetCreditByNumberService;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.credit.strategy.StrategyContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("Testes para GetCreditByNumberService")
class GetCreditByNumberServiceTest {

  @Mock private CreditRepository creditRepository;

  @Mock private StrategyContext strategyContext;

  @InjectMocks private GetCreditByNumberService getCreditByNumberService;

  private Credit mockCredit;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    User user =
        User.builder()
            .id(10L)
            .name("Fulano")
            .email("fulano@email.com")
            .document("12345678900")
            .role(Role.USER)
            .build();

    mockCredit =
        Credit.builder()
            .id(1L)
            .creditNumber("CREDIT001")
            .nfseNumber("NF001")
            .constitutionDate(LocalDate.now())
            .issqnAmount(BigDecimal.valueOf(100))
            .creditType("ISSQN")
            .simpleNational(true)
            .rate(BigDecimal.valueOf(5))
            .billedAmount(BigDecimal.valueOf(1000))
            .deductionAmount(BigDecimal.valueOf(100))
            .calculationBase(BigDecimal.valueOf(900))
            .user(user)
            .build();
  }

  @Test
  @DisplayName("Deve retornar DTO completo quando crédito for encontrado e imposto calculado")
  void shouldReturnDtoSuccessfully_whenCreditFound() {
    when(creditRepository.findByCreditNumber("CREDIT001")).thenReturn(Optional.of(mockCredit));
    when(strategyContext.calculate(eq("ISSQN"), any(Credit.class)))
        .thenReturn(BigDecimal.valueOf(45));

    CreditResponseDTO result = getCreditByNumberService.execute("CREDIT001");

    assertNotNull(result);
    assertEquals("CREDIT001", result.creditNumber());
    assertEquals("NF001", result.nfseNumber());
    assertEquals(BigDecimal.valueOf(45), result.calculatedTax());
    assertEquals("Fulano", result.userName());
    assertEquals("fulano@email.com", result.userEmail());
  }

  @Test
  @DisplayName("Deve lançar exceção se crédito não for encontrado")
  void shouldThrowException_whenCreditNotFound() {
    when(creditRepository.findByCreditNumber("NOT_FOUND")).thenReturn(Optional.empty());

    CreditNotFoundException ex =
        assertThrows(
            CreditNotFoundException.class, () -> getCreditByNumberService.execute("NOT_FOUND"));

    assertEquals("Crédito não encontrado: NOT_FOUND", ex.getMessage());
    verify(strategyContext, never()).calculate(any(), any());
  }

  @Test
  @DisplayName("Deve retornar imposto zero se estratégia retornar zero")
  void shouldReturnZeroTax_whenStrategyReturnsZero() {
    when(creditRepository.findByCreditNumber("CREDIT001")).thenReturn(Optional.of(mockCredit));
    when(strategyContext.calculate(eq("ISSQN"), any(Credit.class))).thenReturn(BigDecimal.ZERO);

    CreditResponseDTO result = getCreditByNumberService.execute("CREDIT001");

    assertEquals(BigDecimal.ZERO, result.calculatedTax());
  }

  @Test
  @DisplayName("Deve lançar exceção se cálculo de imposto falhar")
  void shouldThrowException_whenStrategyCalculationFails() {
    when(creditRepository.findByCreditNumber("CREDIT001")).thenReturn(Optional.of(mockCredit));
    when(strategyContext.calculate(any(), any()))
        .thenThrow(new RuntimeException("Erro interno na estratégia"));

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> getCreditByNumberService.execute("CREDIT001"));

    assertEquals("Erro interno na estratégia", ex.getMessage());
  }

  @Test
  @DisplayName("Deve calcular imposto com tipo de crédito alternativo")
  void shouldCalculateTaxWithDifferentCreditType() {
    mockCredit.setCreditType("OTHER");

    when(creditRepository.findByCreditNumber("CREDIT001")).thenReturn(Optional.of(mockCredit));
    when(strategyContext.calculate(eq("OTHER"), any(Credit.class)))
        .thenReturn(BigDecimal.valueOf(33));

    CreditResponseDTO result = getCreditByNumberService.execute("CREDIT001");

    assertEquals(BigDecimal.valueOf(33), result.calculatedTax());
  }
}
