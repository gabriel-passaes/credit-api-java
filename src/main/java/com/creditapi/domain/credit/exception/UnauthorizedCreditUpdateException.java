package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class UnauthorizedCreditUpdateException extends GlobalDomainException {

  public UnauthorizedCreditUpdateException(String message) {
    super(message);
  }

  public UnauthorizedCreditUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
