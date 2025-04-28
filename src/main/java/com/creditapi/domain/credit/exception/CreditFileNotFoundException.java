package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class CreditFileNotFoundException extends GlobalDomainException {
  public CreditFileNotFoundException(String message) {
    super(message);
  }

  public CreditFileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
