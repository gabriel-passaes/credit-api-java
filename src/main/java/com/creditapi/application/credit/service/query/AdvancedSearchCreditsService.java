package com.creditapi.application.credit.service.query;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.dto.search.CreditSearchCriteria;
import com.creditapi.application.credit.usecase.query.AdvancedSearchCreditsUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.credit.model.CreditFilter;
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
public class AdvancedSearchCreditsService implements AdvancedSearchCreditsUseCase {

  private static final Logger logger = LoggerFactory.getLogger(AdvancedSearchCreditsService.class);

  private final CreditRepository creditRepository;
  private final StrategyContext strategyContext;

  public AdvancedSearchCreditsService(
      CreditRepository creditRepository, StrategyContext strategyContext) {
    this.creditRepository = creditRepository;
    this.strategyContext = strategyContext;
  }

  @Override
  @Observed(name = "credit.advanced-search")
  @Cacheable(
      value = "credits-advanced-search",
      key = "#criteria.hashCode().toString().concat('-').concat(#pageable.pageNumber.toString())")
  @RateLimiter(name = "creditService")
  public Page<CreditResponseDTO> execute(CreditSearchCriteria criteria, Pageable pageable) {
    logger.info("🔎 Buscando créditos com critérios: {}, página: {}", criteria, pageable);

    CreditFilter filter = new CreditFilter();
    filter.setCreditNumber(criteria.creditNumber());
    filter.setNfseNumber(criteria.nfseNumber());
    filter.setCreditType(criteria.creditType());
    filter.setCreatedAfter(criteria.startDate());
    filter.setCreatedBefore(criteria.endDate());

    Page<Credit> found = creditRepository.findAllByFilter(filter, pageable);

    if (found.isEmpty()) {
      logger.warn("Nenhum crédito encontrado para critérios: {}", criteria);
      throw new CreditNotFoundException("Nenhum crédito atende aos critérios fornecidos");
    }

    return found.map(
        c -> {
          BigDecimal tax = strategyContext.calculate(c.getCreditType(), c);
          logger.debug("Crédito {} -> imposto {}", c.getCreditNumber(), tax);
          return new CreditResponseDTO(
              c.getCreditNumber(),
              c.getNfseNumber(),
              c.getConstitutionDate(),
              c.getIssqnAmount(),
              c.getCreditType(),
              c.isSimpleNational(),
              c.getRate(),
              c.getBilledAmount(),
              c.getDeductionAmount(),
              c.getCalculationBase(),
              tax,
              c.getUser().getId(),
              c.getUser().getName(),
              c.getUser().getEmail());
        });
  }
}
