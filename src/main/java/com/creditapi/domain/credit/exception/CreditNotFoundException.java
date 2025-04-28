package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class CreditNotFoundException extends GlobalDomainException {
  public CreditNotFoundException(String message) {
    super(message);
  }

  public CreditNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
