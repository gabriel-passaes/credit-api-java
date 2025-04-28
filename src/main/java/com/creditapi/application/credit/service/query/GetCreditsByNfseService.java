package com.creditapi.application.credit.service.query;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.usecase.query.GetCreditsByNfseUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
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
public class GetCreditsByNfseService implements GetCreditsByNfseUseCase {

  private static final Logger logger = LoggerFactory.getLogger(GetCreditsByNfseService.class);

  private final CreditRepository creditRepository;
  private final StrategyContext strategyContext;

  public GetCreditsByNfseService(
      CreditRepository creditRepository, StrategyContext strategyContext) {
    this.creditRepository = creditRepository;
    this.strategyContext = strategyContext;
  }

  @Override
  @Observed(name = "credit.by-nfse")
  @Cacheable(
      value = "credits-by-nfse",
      key = "#nfseNumber + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
  @RateLimiter(name = "creditService")
  public Page<CreditResponseDTO> execute(String nfseNumber, Pageable pageable) {
    logger.info(
        "Buscando créditos da NFS-e {} (page={}, size={})",
        nfseNumber,
        pageable.getPageNumber(),
        pageable.getPageSize());

    Page<Credit> page = creditRepository.findByNfseNumber(nfseNumber, pageable);
    if (page.isEmpty()) {
      logger.warn("Nenhum crédito encontrado para NFS-e {}", nfseNumber);
      throw new CreditNotFoundException("Nenhum crédito encontrado para a NFS-e: " + nfseNumber);
    }

    return page.map(
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
