package com.creditapi.infrastructure.credit.strategy;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.credit.strategy.TaxCalculationStrategy;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class OtherCreditStrategy implements TaxCalculationStrategy {

  @Override
  public BigDecimal calculate(Credit credit) {
    return BigDecimal.ZERO;
  }
}
