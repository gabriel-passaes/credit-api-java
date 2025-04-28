package com.creditapi.unit.domain.invoice.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.creditapi.domain.invoice.exception.InvoiceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvoiceNotFoundExceptionTest {

  @Test
  @DisplayName("Deve instanciar exceção com mensagem")
  void shouldInstantiateWithMessage() {
    var exception = new InvoiceNotFoundException("Nota não encontrada");
    assertEquals("Nota não encontrada", exception.getMessage());
  }

  @Test
  @DisplayName("Deve instanciar exceção com mensagem e causa")
  void shouldInstantiateWithMessageAndCause() {
    var cause = new RuntimeException("Erro interno");
    var exception = new InvoiceNotFoundException("Falha ao consultar", cause);
    assertEquals("Falha ao consultar", exception.getMessage());
    assertSame(cause, exception.getCause());
  }
}
