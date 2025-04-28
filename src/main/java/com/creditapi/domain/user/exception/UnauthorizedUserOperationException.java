package com.creditapi.domain.user.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class UnauthorizedUserOperationException extends GlobalDomainException {
  public UnauthorizedUserOperationException(String message) {
    super(message);
  }

  public UnauthorizedUserOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
