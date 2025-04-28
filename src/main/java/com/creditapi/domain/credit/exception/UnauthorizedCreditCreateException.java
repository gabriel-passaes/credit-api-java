package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class UnauthorizedCreditCreateException extends GlobalDomainException {
  public UnauthorizedCreditCreateException(String message) {
    super(message);
  }

  public UnauthorizedCreditCreateException(String message, Throwable cause) {
    super(message, cause);
  }
}
