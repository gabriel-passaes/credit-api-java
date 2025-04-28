package com.creditapi.infrastructure.credit.strategy;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.credit.strategy.TaxCalculationStrategy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class StrategyContext {

  private final Map<String, TaxCalculationStrategy> strategyMap = new HashMap<>();

  public StrategyContext(List<TaxCalculationStrategy> strategies) {
    strategies.forEach(
        strategy -> {
          String key = strategy.getClass().getSimpleName().replace("Strategy", "").toLowerCase();
          strategyMap.put(key, strategy);
        });
  }

  public BigDecimal calculate(String creditType, Credit credit) {
    TaxCalculationStrategy strategy =
        strategyMap.getOrDefault(creditType.toLowerCase(), c -> BigDecimal.ZERO);
    return strategy.calculate(credit);
  }
}
