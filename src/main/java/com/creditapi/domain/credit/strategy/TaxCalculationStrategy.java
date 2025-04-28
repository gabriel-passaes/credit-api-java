package com.creditapi.domain.credit.strategy;

import com.creditapi.domain.credit.model.Credit;
import java.math.BigDecimal;

public interface TaxCalculationStrategy {

  BigDecimal calculate(Credit credit);
}
