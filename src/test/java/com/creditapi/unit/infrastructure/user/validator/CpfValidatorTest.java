package com.creditapi.unit.infrastructure.user.validator;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.infrastructure.user.validator.CpfValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CpfValidatorTest {

  private final CpfValidator validator = new CpfValidator();

  @Test
  @DisplayName("Deve retornar true para CPF válido")
  void shouldReturnTrue_whenCpfIsValid() {
    String validCpf = "52998224725";
    boolean result = validator.isValid(validCpf, null);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Deve retornar false para CPF inválido")
  void shouldReturnFalse_whenCpfIsInvalid() {
    String invalidCpf = "12345678900";
    boolean result = validator.isValid(invalidCpf, null);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Deve retornar false para CPF nulo ou vazio")
  void shouldReturnFalse_whenCpfIsNullOrEmpty() {
    assertThat(validator.isValid(null, null)).isFalse();
    assertThat(validator.isValid("", null)).isFalse();
    assertThat(validator.isValid("           ", null)).isFalse();
  }

  @Test
  @DisplayName("Deve retornar false para CPF com todos os dígitos iguais")
  void shouldReturnFalse_whenCpfHasRepeatedDigits() {
    assertThat(validator.isValid("11111111111", null)).isFalse();
  }
}
