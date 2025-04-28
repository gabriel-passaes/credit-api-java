package com.creditapi.domain.auth.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class RefreshTokenInvalidException extends GlobalDomainException {

  public RefreshTokenInvalidException(String message) {
    super(message);
  }

  public RefreshTokenInvalidException(String message, Throwable cause) {
    super(message, cause);
  }
}
