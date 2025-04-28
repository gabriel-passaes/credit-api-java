package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class IntegrationException extends GlobalDomainException {
  public IntegrationException(String message) {
    super(message);
  }

  public IntegrationException(String message, Throwable cause) {
    super(message, cause);
  }
}
