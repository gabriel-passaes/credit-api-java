package com.creditapi.application.credit.service.create;

import com.creditapi.application.credit.dto.request.CreateCreditRequestDTO;
import com.creditapi.application.credit.dto.response.CreateCreditResponseDTO;
import com.creditapi.application.credit.usecase.create.CreateCreditUseCase;
import com.creditapi.domain.credit.exception.DuplicateCreditException;
import com.creditapi.domain.credit.exception.UnauthorizedCreditCreateException;
import com.creditapi.domain.credit.factory.CreditFactory;
import com.creditapi.domain.credit.gateway.event.CreditEventPublisher;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.security.AuthenticatedUserProvider;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CreateCreditService implements CreateCreditUseCase {

  private static final Logger logger = LoggerFactory.getLogger(CreateCreditService.class);
  private static final String RATE_LIMITER = "creditServiceLimiter";

  private final CreditRepository creditRepository;
  private final CreditFactory creditFactory;
  private final CreditEventPublisher eventPublisher;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  public CreateCreditService(
      CreditRepository creditRepository,
      CreditFactory creditFactory,
      CreditEventPublisher eventPublisher,
      AuthenticatedUserProvider authenticatedUserProvider) {
    this.creditRepository = creditRepository;
    this.creditFactory = creditFactory;
    this.eventPublisher = eventPublisher;
    this.authenticatedUserProvider = authenticatedUserProvider;
  }

  @Override
  @Observed(name = "credit.create")
  @RateLimiter(name = RATE_LIMITER)
  @CacheEvict(
      value = {"credit-by-id", "credits-by-nfse", "paginatedCredits"},
      allEntries = true)
  public CreateCreditResponseDTO execute(CreateCreditRequestDTO requestDTO) {
    logger.info("Iniciando criação de crédito {}", requestDTO.creditNumber());

    if (creditRepository.findByCreditNumber(requestDTO.creditNumber()).isPresent()) {
      logger.warn("Tentativa de criar crédito duplicado: {}", requestDTO.creditNumber());
      throw new DuplicateCreditException(
          "Já existe um crédito cadastrado com o número: " + requestDTO.creditNumber());
    }

    User authenticated = authenticatedUserProvider.getRequiredUser();

    if (authenticated.getRole() != Role.SUPER_ADMIN
        && !authenticated.getId().equals(requestDTO.userId())) {
      logger.warn(
          "Usuário {} tentou criar crédito para outro usuário: {}",
          authenticated.getId(),
          requestDTO.userId());
      throw new UnauthorizedCreditCreateException(
          "Você não tem permissão para criar crédito para outro usuário.");
    }

    User user = User.builder().id(requestDTO.userId()).build();

    Credit credit = creditFactory.fromRequest(requestDTO, user);
    Credit savedCredit = creditRepository.save(credit);

    logger.debug("Crédito salvo com ID {}", savedCredit.getId());
    eventPublisher.publishCreditCreated(savedCredit);
    logger.info("Evento de criação publicado para crédito {}", savedCredit.getCreditNumber());

    return new CreateCreditResponseDTO(
        savedCredit.getCreditNumber(),
        savedCredit.getNfseNumber(),
        savedCredit.getConstitutionDate(),
        savedCredit.getIssqnAmount(),
        savedCredit.getCreditType(),
        savedCredit.isSimpleNational(),
        savedCredit.getRate(),
        savedCredit.getBilledAmount(),
        savedCredit.getDeductionAmount(),
        savedCredit.getCalculationBase(),
        null, // ou calcular se tiver regra
        savedCredit.getUser().getId(),
        savedCredit.getUser().getName(),
        savedCredit.getUser().getEmail());
  }
}
