package com.creditapi.unit.infrastructure.credit.messaging.mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.infrastructure.credit.messaging.mock.FakeCreditEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

class FakeCreditEventPublisherTest {

  @Mock private Logger logger;

  @InjectMocks private FakeCreditEventPublisher fakeCreditEventPublisher;

  private Credit credit;

  @BeforeEach
  void setup() {
    credit = new Credit();
    credit.setCreditNumber("CREDIT123");
    credit.setUser(new com.creditapi.domain.user.model.User());
  }

  @Test
  @DisplayName("Deve registrar evento de crédito criado no log")
  void shouldLogCreditCreatedEvent() {
    fakeCreditEventPublisher.publishCreditCreated(credit);
    verify(logger, times(1))
        .info(
            "FAKE: Evento de criação publicado: crédito={} usuário={}",
            "CREDIT123",
            credit.getUser().getId());
  }

  @Test
  @DisplayName("Deve registrar evento de crédito atualizado no log")
  void shouldLogCreditUpdatedEvent() {
    fakeCreditEventPublisher.publishCreditUpdated(credit);
    verify(logger, times(1))
        .info(
            "FAKE: Evento de atualização publicado: crédito={} usuário={}",
            "CREDIT123",
            credit.getUser().getId());
  }

  @Test
  @DisplayName("Deve registrar evento de crédito deletado no log")
  void shouldLogCreditDeletedEvent() {
    fakeCreditEventPublisher.publishCreditDeleted(credit);
    verify(logger, times(1))
        .info(
            "FAKE: Evento de deleção publicado: crédito={} usuário={}",
            "CREDIT123",
            credit.getUser().getId());
  }

  @Test
  @DisplayName("Deve registrar evento de consulta por número no log")
  void shouldLogCreditQueriedByNumberEvent() {
    fakeCreditEventPublisher.publishCreditQueriedByNumber(credit);
    verify(logger, times(1))
        .info(
            "FAKE: Evento de consulta por número publicado: crédito={} usuário={}",
            "CREDIT123",
            credit.getUser().getId());
  }

  @Test
  @DisplayName("Deve registrar evento de consulta por NFS-e no log")
  void shouldLogCreditQueriedByNfseEvent() {
    fakeCreditEventPublisher.publishCreditQueriedByNfse("NFSE123");
    verify(logger, times(1)).info("FAKE: Evento de consulta por NFS-e publicado: {}", "NFSE123");
  }
}
