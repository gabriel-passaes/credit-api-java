package com.creditapi.domain.shared.exception;

public abstract class GlobalDomainException extends RuntimeException {

  public GlobalDomainException(String message) {
    super(message);
  }

  public GlobalDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
