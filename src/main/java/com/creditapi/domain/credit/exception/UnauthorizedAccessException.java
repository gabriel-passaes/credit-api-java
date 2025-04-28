package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class UnauthorizedAccessException extends GlobalDomainException {
  public UnauthorizedAccessException(String message) {
    super(message);
  }

  public UnauthorizedAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
