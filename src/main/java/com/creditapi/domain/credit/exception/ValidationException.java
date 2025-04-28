package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class ValidationException extends GlobalDomainException {
  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
