package com.creditapi.domain.user.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class InvalidCpfException extends GlobalDomainException {
  public InvalidCpfException(String message) {
    super(message);
  }

  public InvalidCpfException(String message, Throwable cause) {
    super(message, cause);
  }
}
