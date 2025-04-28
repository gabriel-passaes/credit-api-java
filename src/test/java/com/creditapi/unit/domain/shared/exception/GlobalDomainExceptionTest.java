package com.creditapi.unit.domain.shared.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.domain.shared.exception.GlobalDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GlobalDomainExceptionTest {

  static class DummyException extends GlobalDomainException {
    public DummyException(String message) {
      super(message);
    }

    public DummyException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  @Test
  @DisplayName("Deve instanciar GlobalDomainException com mensagem")
  void shouldInstantiateWithMessage() {
    GlobalDomainException ex = new DummyException("Erro genérico");
    assertThat(ex).isInstanceOf(RuntimeException.class);
    assertThat(ex.getMessage()).isEqualTo("Erro genérico");
  }

  @Test
  @DisplayName("Deve instanciar GlobalDomainException com causa")
  void shouldInstantiateWithCause() {
    Throwable cause = new IllegalArgumentException("Causa");
    GlobalDomainException ex = new DummyException("Erro com causa", cause);
    assertThat(ex.getCause()).isEqualTo(cause);
  }
}
