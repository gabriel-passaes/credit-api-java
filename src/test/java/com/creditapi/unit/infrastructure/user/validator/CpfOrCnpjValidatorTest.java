package com.creditapi.unit.infrastructure.user.validator;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.infrastructure.user.validator.CnpjValidator;
import com.creditapi.infrastructure.user.validator.CpfOrCnpjValidator;
import com.creditapi.infrastructure.user.validator.CpfValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CpfOrCnpjValidatorTest {

  private CpfOrCnpjValidator validator;

  @BeforeEach
  void setup() {
    validator = new CpfOrCnpjValidator(new CpfValidator(), new CnpjValidator());
  }

  @Test
  @DisplayName("Deve retornar true para CPF válido")
  void shouldReturnTrue_whenCpfIsValid() {
    boolean result = validator.isValid("52998224725", null);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Deve retornar true para CNPJ válido")
  void shouldReturnTrue_whenCnpjIsValid() {
    boolean result = validator.isValid("11222333000199", null);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Deve retornar false para CPF inválido")
  void shouldReturnFalse_whenCpfIsInvalid() {
    boolean result = validator.isValid("12345678900", null);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Deve retornar false para CNPJ inválido")
  void shouldReturnFalse_whenCnpjIsInvalid() {
    boolean result = validator.isValid("12345678000100", null);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Deve retornar false para valor nulo ou vazio")
  void shouldReturnFalse_whenValueIsNullOrEmpty() {
    assertThat(validator.isValid(null, null)).isFalse();
    assertThat(validator.isValid("", null)).isFalse();
    assertThat(validator.isValid(" ", null)).isFalse();
  }
}
