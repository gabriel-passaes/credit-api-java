package com.creditapi.unit.infrastructure.user.validator;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.infrastructure.user.validator.CnpjValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CnpjValidatorTest {

  private final CnpjValidator validator = new CnpjValidator();

  @Test
  @DisplayName("Deve retornar true para CNPJ válido")
  void shouldReturnTrue_whenCnpjIsValid() {
    String validCnpj = "11222333000199";
    boolean result = validator.isValid(validCnpj, null);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Deve retornar false para CNPJ inválido")
  void shouldReturnFalse_whenCnpjIsInvalid() {
    String invalidCnpj = "12345678901234";
    boolean result = validator.isValid(invalidCnpj, null);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Deve retornar false para CNPJ com todos os dígitos iguais")
  void shouldReturnFalse_whenCnpjHasRepeatedDigits() {
    assertThat(validator.isValid("00000000000000", null)).isFalse();
  }

  @Test
  @DisplayName("Deve retornar false para CNPJ nulo ou vazio")
  void shouldReturnFalse_whenCnpjIsNullOrEmpty() {
    assertThat(validator.isValid(null, null)).isFalse();
    assertThat(validator.isValid("", null)).isFalse();
    assertThat(validator.isValid("            ", null)).isFalse();
  }
}
