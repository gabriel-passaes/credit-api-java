package com.creditapi.domain.user.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class UserNotFoundException extends GlobalDomainException {

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
