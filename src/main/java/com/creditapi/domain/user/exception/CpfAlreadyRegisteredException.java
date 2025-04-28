package com.creditapi.domain.user.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class CpfAlreadyRegisteredException extends GlobalDomainException {
  public CpfAlreadyRegisteredException(String message) {
    super(message);
  }

  public CpfAlreadyRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }
}
