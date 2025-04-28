package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class InvalidCreditRateException extends GlobalDomainException {
  public InvalidCreditRateException(String message) {
    super(message);
  }

  public InvalidCreditRateException(String message, Throwable cause) {
    super(message, cause);
  }
}
