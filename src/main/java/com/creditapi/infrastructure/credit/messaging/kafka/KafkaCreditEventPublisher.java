package com.creditapi.infrastructure.credit.messaging.kafka;

import com.creditapi.domain.credit.gateway.event.CreditEventPublisher;
import com.creditapi.domain.credit.model.Credit;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class KafkaCreditEventPublisher implements CreditEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public KafkaCreditEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publishCreditCreated(Credit credit) {
    kafkaTemplate.send("credit.created", credit);
  }

  @Override
  public void publishCreditUpdated(Credit credit) {
    kafkaTemplate.send("credit.updated", credit);
  }

  @Override
  public void publishCreditDeleted(Credit credit) {
    kafkaTemplate.send("credit.deleted", credit);
  }

  @Override
  public void publishCreditQueriedByNumber(Credit credit) {
    kafkaTemplate.send("credit.queried.number", credit);
  }

  @Override
  public void publishCreditQueriedByNfse(String nfseNumber) {
    kafkaTemplate.send("credit.queried.nfse", nfseNumber);
  }
}
