package com.creditapi.domain.credit.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class DuplicateCreditException extends GlobalDomainException {
  public DuplicateCreditException(String message) {
    super(message);
  }

  public DuplicateCreditException(String message, Throwable cause) {
    super(message, cause);
  }
}
