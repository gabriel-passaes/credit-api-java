package com.creditapi.unit.application.user.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.user.dto.request.CreateUserRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateUserRequestDTOTest {

  private static Validator validator;

  @BeforeAll
  static void init() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  @DisplayName("DTO válido não deve ter violações")
  void validDto_hasNoViolations() {
    CreateUserRequestDTO dto =
        new CreateUserRequestDTO("Alice", "alice@test.com", "12345678910", "Strong123!");

    Set<ConstraintViolation<CreateUserRequestDTO>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Deve falhar quando nome estiver em branco")
  void invalid_blankName() {
    CreateUserRequestDTO dto =
        new CreateUserRequestDTO(" ", "user@test.com", "12345678910", "Strong123!");

    Set<ConstraintViolation<CreateUserRequestDTO>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
  }

  @Test
  @DisplayName("Deve falhar quando e-mail for inválido")
  void invalid_badEmail() {
    CreateUserRequestDTO dto =
        new CreateUserRequestDTO("Bob", "invalid-email", "12345678910", "Strong123!");

    Set<ConstraintViolation<CreateUserRequestDTO>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  @DisplayName("Deve falhar quando CPF/CNPJ for inválido")
  void invalid_badDocument() {
    CreateUserRequestDTO dto = new CreateUserRequestDTO("Bob", "bob@test.com", "000", "Strong123!");

    Set<ConstraintViolation<CreateUserRequestDTO>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("document"));
  }

  @Test
  @DisplayName("Deve falhar quando senha for muito fraca")
  void invalid_weakPassword() {
    CreateUserRequestDTO dto =
        new CreateUserRequestDTO("Bob", "bob@test.com", "12345678910", "123");

    Set<ConstraintViolation<CreateUserRequestDTO>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
  }
}
