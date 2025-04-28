package com.creditapi.unit.domain.auth.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.creditapi.domain.auth.exception.AuthUserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthUserNotFoundExceptionTest {

  @Test
  @DisplayName("Deve criar exceção com mensagem")
  void givenMessage_whenCreate_thenMessageIsStored() {
    AuthUserNotFoundException ex = new AuthUserNotFoundException("Usuário não localizado");
    assertEquals("Usuário não localizado", ex.getMessage());
  }

  @Test
  @DisplayName("Deve criar exceção com mensagem e causa")
  void givenMessageAndCause_whenCreate_thenMessageAndCauseAreStored() {
    Throwable cause = new RuntimeException("Causa original");
    AuthUserNotFoundException ex = new AuthUserNotFoundException("Erro ao buscar", cause);

    assertEquals("Erro ao buscar", ex.getMessage());
    assertEquals(cause, ex.getCause());
  }
}
