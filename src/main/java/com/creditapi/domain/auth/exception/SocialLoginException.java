package com.creditapi.domain.auth.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class SocialLoginException extends GlobalDomainException {

  public SocialLoginException(String message) {
    super(message);
  }

  public SocialLoginException(String message, Throwable cause) {
    super(message, cause);
  }
}
