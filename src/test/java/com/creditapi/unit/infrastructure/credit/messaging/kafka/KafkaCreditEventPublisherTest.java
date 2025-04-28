package com.creditapi.unit.infrastructure.credit.messaging.kafka;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.infrastructure.credit.messaging.kafka.KafkaCreditEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;

class KafkaCreditEventPublisherTest {

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;
  @InjectMocks private KafkaCreditEventPublisher kafkaCreditEventPublisher;

  private Credit credit;

  @BeforeEach
  void setup() {
    credit = new Credit();
    credit.setCreditNumber("CREDIT123");
    credit.setUser(new com.creditapi.domain.user.model.User());
  }

  @Test
  @DisplayName("Deve publicar evento de crédito criado corretamente")
  void shouldPublishCreditCreatedEvent() {
    kafkaCreditEventPublisher.publishCreditCreated(credit);
    verify(kafkaTemplate, times(1)).send("credit.created", credit);
  }

  @Test
  @DisplayName("Deve publicar evento de crédito atualizado corretamente")
  void shouldPublishCreditUpdatedEvent() {
    kafkaCreditEventPublisher.publishCreditUpdated(credit);
    verify(kafkaTemplate, times(1)).send("credit.updated", credit);
  }

  @Test
  @DisplayName("Deve publicar evento de crédito deletado corretamente")
  void shouldPublishCreditDeletedEvent() {
    kafkaCreditEventPublisher.publishCreditDeleted(credit);
    verify(kafkaTemplate, times(1)).send("credit.deleted", credit);
  }

  @Test
  @DisplayName("Deve publicar evento de consulta por número corretamente")
  void shouldPublishCreditQueriedByNumberEvent() {
    kafkaCreditEventPublisher.publishCreditQueriedByNumber(credit);
    verify(kafkaTemplate, times(1)).send("credit.queried.number", credit);
  }

  @Test
  @DisplayName("Deve publicar evento de consulta por NFS-e corretamente")
  void shouldPublishCreditQueriedByNfseEvent() {
    kafkaCreditEventPublisher.publishCreditQueriedByNfse("NFSE123");
    verify(kafkaTemplate, times(1)).send("credit.queried.nfse", "NFSE123");
  }
}
