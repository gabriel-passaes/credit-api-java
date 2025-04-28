package com.creditapi.unit.application.auth.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.auth.dto.request.RecoverPasswordRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecoverPasswordRequestDTOTest {

  private Validator validator;

  @BeforeEach
  void setUpValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve retornar erro se o campo email estiver nulo")
  void givenNullEmail_whenValidate_thenShouldFailValidation() {
    RecoverPasswordRequestDTO dtoWithNullEmail =
        RecoverPasswordRequestDTO.builder().email(null).build();

    Set<ConstraintViolation<RecoverPasswordRequestDTO>> violations =
        validator.validate(dtoWithNullEmail);

    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Deve retornar erro se o campo email estiver vazio")
  void givenBlankEmail_whenValidate_thenShouldFailValidation() {
    RecoverPasswordRequestDTO dtoWithBlankEmail =
        RecoverPasswordRequestDTO.builder().email("").build();

    Set<ConstraintViolation<RecoverPasswordRequestDTO>> violations =
        validator.validate(dtoWithBlankEmail);

    assertThat(violations).isNotEmpty();
  }

  @Test
  @DisplayName("Deve retornar erro se o e-mail for inválido")
  void givenInvalidEmailFormat_whenValidate_thenShouldFailValidation() {
    RecoverPasswordRequestDTO dtoWithInvalidEmail =
        RecoverPasswordRequestDTO.builder().email("email-invalido").build();

    Set<ConstraintViolation<RecoverPasswordRequestDTO>> violations =
        validator.validate(dtoWithInvalidEmail);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  @DisplayName("Deve passar se o e-mail for válido")
  void givenValidEmail_whenValidate_thenShouldPassValidation() {
    RecoverPasswordRequestDTO validDto =
        RecoverPasswordRequestDTO.builder().email("user@example.com").build();

    Set<ConstraintViolation<RecoverPasswordRequestDTO>> violations = validator.validate(validDto);

    assertThat(violations).isEmpty();
  }
}
