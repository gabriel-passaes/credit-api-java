package com.creditapi.application.credit.service.query;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.usecase.query.GetCreditByNumberUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.infrastructure.credit.strategy.StrategyContext;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.observation.annotation.Observed;
import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GetCreditByNumberService implements GetCreditByNumberUseCase {

  private static final Logger logger = LoggerFactory.getLogger(GetCreditByNumberService.class);

  private final CreditRepository creditRepository;
  private final StrategyContext strategyContext;

  public GetCreditByNumberService(
      CreditRepository creditRepository, StrategyContext strategyContext) {
    this.creditRepository = creditRepository;
    this.strategyContext = strategyContext;
  }

  @Override
  @Observed(name = "credit.by-id")
  @Cacheable(value = "credit-by-id", key = "#creditNumber")
  @RateLimiter(name = "creditService")
  public CreditResponseDTO execute(String creditNumber) {
    logger.info("Buscando crédito pelo número {}", creditNumber);

    Optional<Credit> creditOpt = creditRepository.findByCreditNumber(creditNumber);
    Credit credit =
        creditOpt.orElseThrow(
            () -> {
              logger.warn("Crédito não encontrado: {}", creditNumber);
              return new CreditNotFoundException("Crédito não encontrado: " + creditNumber);
            });

    BigDecimal calculatedTax = strategyContext.calculate(credit.getCreditType(), credit);
    logger.debug("Imposto calculado para {}: {}", creditNumber, calculatedTax);

    return toDto(credit, calculatedTax);
  }

  private CreditResponseDTO toDto(Credit credit, BigDecimal calculatedTax) {
    return new CreditResponseDTO(
        credit.getCreditNumber(),
        credit.getNfseNumber(),
        credit.getConstitutionDate(),
        credit.getIssqnAmount(),
        credit.getCreditType(),
        credit.isSimpleNational(),
        credit.getRate(),
        credit.getBilledAmount(),
        credit.getDeductionAmount(),
        credit.getCalculationBase(),
        calculatedTax,
        credit.getUser().getId(),
        credit.getUser().getName(),
        credit.getUser().getEmail());
  }
}
