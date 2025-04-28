package com.creditapi.domain.auth.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class RefreshTokenExpiredException extends GlobalDomainException {

  public RefreshTokenExpiredException(String message) {
    super(message);
  }

  public RefreshTokenExpiredException(String message, Throwable cause) {
    super(message, cause);
  }
}
