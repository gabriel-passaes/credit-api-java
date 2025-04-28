package com.creditapi.application.credit.service.query;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.usecase.query.GetPaginatedCreditsUseCase;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.infrastructure.credit.strategy.StrategyContext;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.observation.annotation.Observed;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetPaginatedCreditsService implements GetPaginatedCreditsUseCase {

  private static final Logger logger = LoggerFactory.getLogger(GetPaginatedCreditsService.class);

  private final CreditRepository creditRepository;
  private final StrategyContext strategyContext;

  public GetPaginatedCreditsService(
      CreditRepository creditRepository, StrategyContext strategyContext) {
    this.creditRepository = creditRepository;
    this.strategyContext = strategyContext;
  }

  @Override
  @Observed(name = "credit.list-paginated")
  @Cacheable(value = "paginated-credits", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
  @RateLimiter(name = "creditService")
  public Page<CreditResponseDTO> execute(Pageable pageable) {
    logger.info(
        "Buscando créditos paginados (page={}, size={})",
        pageable.getPageNumber(),
        pageable.getPageSize());

    return creditRepository
        .findAll(pageable)
        .map(
            credit -> {
              BigDecimal tax = strategyContext.calculate(credit.getCreditType(), credit);
              logger.debug("Crédito {} -> imposto {}", credit.getCreditNumber(), tax);
              return toDto(credit, tax);
            });
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
