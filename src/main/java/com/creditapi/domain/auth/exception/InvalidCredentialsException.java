package com.creditapi.domain.auth.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class InvalidCredentialsException extends GlobalDomainException {

  public InvalidCredentialsException(String message) {
    super(message);
  }

  public InvalidCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }
}
