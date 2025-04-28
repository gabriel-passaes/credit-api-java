package com.creditapi.application.credit.service.delete;

import com.creditapi.application.credit.usecase.delete.DeleteCreditUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class DeleteCreditService implements DeleteCreditUseCase {

  private static final Logger logger = LoggerFactory.getLogger(DeleteCreditService.class);
  private static final String RATE_LIMITER = "creditServiceLimiter";

  private final CreditRepository creditRepository;

  public DeleteCreditService(CreditRepository creditRepository) {
    this.creditRepository = creditRepository;
  }

  @Override
  @Observed(name = "credit.delete")
  @RateLimiter(name = RATE_LIMITER)
  @CacheEvict(
      value = {"credit-by-id", "credits-by-nfse", "paginatedCredits"},
      key = "#creditNumber")
  public void execute(String creditNumber) {
    logger.info("Solicitada exclusão do crédito {}", creditNumber);

    if (!creditRepository.existsByCreditNumber(creditNumber)) {
      logger.warn("Crédito não encontrado para exclusão: {}", creditNumber);
      throw new CreditNotFoundException("Crédito não encontrado: " + creditNumber);
    }

    creditRepository.delete(creditNumber);
    logger.info("Crédito {} excluído com sucesso", creditNumber);
  }
}
