package com.creditapi.infrastructure.credit.strategy;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.credit.strategy.TaxCalculationStrategy;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class IssqnStrategy implements TaxCalculationStrategy {

  @Override
  public BigDecimal calculate(Credit credit) {
    if (credit.getCalculationBase() == null || credit.getRate() == null) {
      return BigDecimal.ZERO;
    }
    return credit.getCalculationBase().multiply(credit.getRate()).divide(BigDecimal.valueOf(100));
  }
}
