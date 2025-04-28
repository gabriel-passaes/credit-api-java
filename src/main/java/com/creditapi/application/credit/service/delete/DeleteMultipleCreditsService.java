package com.creditapi.application.credit.service.delete;

import com.creditapi.application.credit.usecase.delete.DeleteMultipleCreditsUseCase;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class DeleteMultipleCreditsService implements DeleteMultipleCreditsUseCase {

  private static final Logger logger = LoggerFactory.getLogger(DeleteMultipleCreditsService.class);

  private final CreditRepository creditRepository;

  public DeleteMultipleCreditsService(CreditRepository creditRepository) {
    this.creditRepository = creditRepository;
  }

  @Override
  @Observed(name = "credit.bulk-delete")
  @RateLimiter(name = "creditServiceLimiter")
  @CacheEvict(
      value = {"credit-by-id", "credits-by-nfse", "paginatedCredits"},
      allEntries = true)
  public void execute(List<String> creditNumbers) {
    logger.info("Iniciando exclusão múltipla de {} créditos", creditNumbers.size());
    creditNumbers.forEach(
        number -> {
          if (creditRepository.existsByCreditNumber(number)) {
            creditRepository.delete(number);
            logger.debug("Crédito {} excluído", number);
          } else {
            logger.warn("Não encontrado para exclusão múltipla: {}", number);
          }
        });
    logger.info("Exclusão múltipla concluída");
  }
}
