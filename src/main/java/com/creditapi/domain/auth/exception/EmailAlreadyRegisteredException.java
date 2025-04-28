package com.creditapi.domain.auth.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class EmailAlreadyRegisteredException extends GlobalDomainException {

  public EmailAlreadyRegisteredException(String message) {
    super(message);
  }

  public EmailAlreadyRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }
}
