package com.creditapi.domain.auth.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class AuthUserNotFoundException extends GlobalDomainException {

  public AuthUserNotFoundException(String message) {
    super(message);
  }

  public AuthUserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
