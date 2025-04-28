package com.creditapi.unit.application.user.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.user.dto.request.UpdateUserRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateUserRequestDTOTest {

  private static Validator validator;

  @BeforeAll
  static void init() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  @DisplayName("DTO válido não gera violações")
  void validDto_noViolations() {
    UpdateUserRequestDTO dto =
        new UpdateUserRequestDTO("Novo Nome", "new@email.com", "12345678910");

    Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Deve falhar se e‑mail tiver formato inválido")
  void invalid_badEmail() {
    UpdateUserRequestDTO dto = new UpdateUserRequestDTO("Nome", "email-invalido", "12345678910");

    Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  @DisplayName("Deve falhar se documento estiver em branco")
  void invalid_blankDocument() {
    UpdateUserRequestDTO dto = new UpdateUserRequestDTO("Nome", "email@valido.com", " ");

    Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(dto);
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("document"));
  }
}
