package com.creditapi.domain.user.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class CnpjAlreadyRegisteredException extends GlobalDomainException {
  public CnpjAlreadyRegisteredException(String message) {
    super(message);
  }

  public CnpjAlreadyRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }
}
