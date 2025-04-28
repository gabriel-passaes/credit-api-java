package com.creditapi.domain.user.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class InvalidCnpjException extends GlobalDomainException {
  public InvalidCnpjException(String message) {
    super(message);
  }

  public InvalidCnpjException(String message, Throwable cause) {
    super(message, cause);
  }
}
