package com.creditapi.domain.credit.gateway.event;

import com.creditapi.domain.credit.model.Credit;

public interface CreditEventPublisher {

  void publishCreditCreated(Credit credit);

  void publishCreditUpdated(Credit credit);

  void publishCreditDeleted(Credit credit);

  void publishCreditQueriedByNumber(Credit credit);

  void publishCreditQueriedByNfse(String nfseNumber);
}
