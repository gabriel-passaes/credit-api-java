package com.creditapi.unit.presentation.shared.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.presentation.shared.exception.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

class TestUtilsTest {

  @Test
  @DisplayName("Deve construir exceção de validação com campo e mensagem fornecidos")
  void shouldBuildMockValidationException() {
    String field = "email";
    String message = "E-mail inválido";

    MethodArgumentNotValidException ex = TestUtils.buildMockValidationException(field, message);

    assertThat(ex.getBindingResult().getFieldErrors()).hasSize(1);
    assertThat(ex.getBindingResult().getFieldError().getField()).isEqualTo(field);
    assertThat(ex.getBindingResult().getFieldError().getDefaultMessage()).isEqualTo(message);
  }
}
