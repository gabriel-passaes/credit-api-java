package com.creditapi.unit.application.auth.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.auth.dto.request.LoginRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginRequestDTOTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve retornar erro se o e-mail estiver vazio")
  void givenBlankEmail_whenValidate_thenConstraintViolation() {
    LoginRequestDTO dto = LoginRequestDTO.builder().email("").password("validPassword").build();

    Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  @DisplayName("Deve retornar erro se a senha estiver vazia")
  void givenBlankPassword_whenValidate_thenConstraintViolation() {
    LoginRequestDTO dto = LoginRequestDTO.builder().email("email@email.com").password("").build();

    Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
  }

  @Test
  @DisplayName("Deve ser válido com dados corretos")
  void givenValidDto_whenValidate_thenNoViolations() {
    LoginRequestDTO dto =
        LoginRequestDTO.builder().email("email@email.com").password("strongPassword").build();

    Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }
}
