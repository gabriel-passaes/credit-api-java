package com.creditapi.infrastructure.credit.messaging.mock;

import com.creditapi.domain.credit.gateway.event.CreditEventPublisher;
import com.creditapi.domain.credit.model.Credit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class FakeCreditEventPublisher implements CreditEventPublisher {

  private static final Logger logger = LoggerFactory.getLogger(FakeCreditEventPublisher.class);

  @Override
  public void publishCreditCreated(Credit credit) {
    logger.info(
        "FAKE: Evento de criação publicado: crédito={} usuário={}",
        credit.getCreditNumber(),
        credit.getUser().getId());
  }

  @Override
  public void publishCreditUpdated(Credit credit) {
    logger.info(
        "FAKE: Evento de atualização publicado: crédito={} usuário={}",
        credit.getCreditNumber(),
        credit.getUser().getId());
  }

  @Override
  public void publishCreditDeleted(Credit credit) {
    logger.info(
        "FAKE: Evento de deleção publicado: crédito={} usuário={}",
        credit.getCreditNumber(),
        credit.getUser().getId());
  }

  @Override
  public void publishCreditQueriedByNumber(Credit credit) {
    logger.info(
        "FAKE: Evento de consulta por número publicado: crédito={} usuário={}",
        credit.getCreditNumber(),
        credit.getUser().getId());
  }

  @Override
  public void publishCreditQueriedByNfse(String nfseNumber) {
    logger.info("FAKE: Evento de consulta por NFS-e publicado: {}", nfseNumber);
  }
}
