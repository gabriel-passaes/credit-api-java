package com.creditapi.application.credit.service.update;

import com.creditapi.application.credit.dto.request.UpdateCreditRequestDTO;
import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.usecase.update.UpdateCreditUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.exception.UnauthorizedCreditUpdateException;
import com.creditapi.domain.credit.gateway.event.CreditEventPublisher;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UpdateCreditService implements UpdateCreditUseCase {

  private static final Logger logger = LoggerFactory.getLogger(UpdateCreditService.class);
  private static final String RATE_LIMITER = "creditServiceLimiter";

  private final CreditRepository creditRepository;
  private final CreditEventPublisher eventPublisher;

  public UpdateCreditService(
      CreditRepository creditRepository, CreditEventPublisher eventPublisher) {
    this.creditRepository = creditRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  @Observed(name = "credit.update")
  @RateLimiter(name = RATE_LIMITER)
  @CacheEvict(
      value = {"credit-by-id", "credits-by-nfse", "paginatedCredits"},
      key = "#creditNumber")
  public CreditResponseDTO execute(String creditNumber, UpdateCreditRequestDTO requestDTO) {
    logger.info("Iniciando atualização do crédito {}", creditNumber);

    Credit existingCredit =
        creditRepository
            .findByCreditNumber(creditNumber)
            .orElseThrow(
                () -> {
                  logger.warn("Crédito não encontrado para atualização: {}", creditNumber);
                  return new CreditNotFoundException("Crédito não encontrado: " + creditNumber);
                });

    if (!existingCredit.getUser().getId().equals(requestDTO.userId())) {
      if (!existingCredit.getUser().getRole().equals(Role.SUPER_ADMIN)) {
        logger.warn("Tentativa de alterar usuário do crédito por não-superadmin: {}", creditNumber);
        throw new UnauthorizedCreditUpdateException(
            "Apenas SUPER_ADMIN pode alterar o usuário de uma nota fiscal");
      }
      User newUser = User.builder().id(requestDTO.userId()).build();
      existingCredit.setUser(newUser);
    }

    existingCredit.setConstitutionDate(requestDTO.constitutionDate());
    existingCredit.setIssqnAmount(requestDTO.issqnAmount());
    existingCredit.setCreditType(requestDTO.creditType());
    existingCredit.setSimpleNational(requestDTO.simpleNational());
    existingCredit.setRate(requestDTO.rate());
    existingCredit.setBilledAmount(requestDTO.billedAmount());
    existingCredit.setDeductionAmount(requestDTO.deductionAmount());
    existingCredit.setCalculationBase(requestDTO.calculationBase());

    Credit updatedCredit = creditRepository.save(existingCredit);

    logger.debug("Crédito {} atualizado no repositório", creditNumber);
    eventPublisher.publishCreditUpdated(updatedCredit);
    logger.info("Evento de atualização publicado para crédito {}", creditNumber);

    return new CreditResponseDTO(
        updatedCredit.getCreditNumber(),
        updatedCredit.getNfseNumber(),
        updatedCredit.getConstitutionDate(),
        updatedCredit.getIssqnAmount(),
        updatedCredit.getCreditType(),
        updatedCredit.isSimpleNational(),
        updatedCredit.getRate(),
        updatedCredit.getBilledAmount(),
        updatedCredit.getDeductionAmount(),
        updatedCredit.getCalculationBase(),
        null,
        updatedCredit.getUser().getId(),
        updatedCredit.getUser().getName(),
        updatedCredit.getUser().getEmail());
  }
}
