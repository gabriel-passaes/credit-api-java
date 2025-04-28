package com.creditapi.unit.infrastructure.credit.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.credit.strategy.TaxCalculationStrategy;
import com.creditapi.infrastructure.credit.strategy.StrategyContext;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StrategyContextTest {

  @Mock private TaxCalculationStrategy issqnStrategy;

  @Mock private TaxCalculationStrategy otherStrategy;

  @InjectMocks private StrategyContext strategyContext;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve calcular imposto corretamente usando a estratégia de ISSQN")
  void shouldCalculateTaxUsingIssqnStrategy() {
    Credit credit = new Credit();
    when(issqnStrategy.calculate(credit)).thenReturn(BigDecimal.valueOf(100));

    strategyContext = new StrategyContext(Arrays.asList(issqnStrategy, otherStrategy));
    BigDecimal result = strategyContext.calculate("issqn", credit);

    assertEquals(BigDecimal.valueOf(100), result);
    verify(issqnStrategy, times(1)).calculate(credit);
  }

  @Test
  @DisplayName("Deve retornar zero se a estratégia não for encontrada")
  void shouldReturnZeroIfStrategyNotFound() {
    Credit credit = new Credit();
    strategyContext = new StrategyContext(Arrays.asList(issqnStrategy, otherStrategy));

    BigDecimal result = strategyContext.calculate("nonexistent", credit);

    assertEquals(BigDecimal.ZERO, result);
    verify(issqnStrategy, never()).calculate(credit);
  }

  @Test
  @DisplayName("Deve lançar exceção se a lista de estratégias estiver vazia")
  void shouldThrowExceptionIfStrategiesListIsEmpty() {
    Credit credit = new Credit();
    strategyContext = new StrategyContext(Arrays.asList());

    Exception exception =
        assertThrows(NullPointerException.class, () -> strategyContext.calculate("issqn", credit));
    assertTrue(exception.getMessage().contains("No strategy found for credit type"));
  }
}
