package com.creditapi.domain.invoice.exception;

import com.creditapi.domain.shared.exception.GlobalDomainException;

public class InvoiceNotFoundException extends GlobalDomainException {

  public InvoiceNotFoundException(String message) {
    super(message);
  }

  public InvoiceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
