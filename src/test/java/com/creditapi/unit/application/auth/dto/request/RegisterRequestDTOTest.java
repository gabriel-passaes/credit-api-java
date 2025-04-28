package com.creditapi.unit.application.auth.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.auth.dto.request.RegisterRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterRequestDTOTest {

  private Validator validator;

  @BeforeEach
  void setupValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve falhar se o nome estiver em branco")
  void givenBlankName_whenValidate_thenFailValidation() {
    RegisterRequestDTO dto =
        RegisterRequestDTO.builder()
            .name("")
            .email("user@email.com")
            .password("senhaForte123")
            .build();

    Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
  }

  @Test
  @DisplayName("Deve falhar se o e-mail estiver em branco")
  void givenBlankEmail_whenValidate_thenFailValidation() {
    RegisterRequestDTO dto =
        RegisterRequestDTO.builder()
            .name("Usuário Teste")
            .email("")
            .password("senhaForte123")
            .build();

    Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  @DisplayName("Deve falhar se o e-mail for inválido")
  void givenInvalidEmail_whenValidate_thenFailValidation() {
    RegisterRequestDTO dto =
        RegisterRequestDTO.builder()
            .name("Usuário Teste")
            .email("email-invalido")
            .password("senhaForte123")
            .build();

    Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  @DisplayName("Deve falhar se a senha estiver vazia")
  void givenBlankPassword_whenValidate_thenFailValidation() {
    RegisterRequestDTO dto =
        RegisterRequestDTO.builder()
            .name("Usuário Teste")
            .email("user@email.com")
            .password("")
            .build();

    Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
  }

  @Test
  @DisplayName("Deve passar se os dados forem válidos")
  void givenValidFields_whenValidate_thenPass() {
    RegisterRequestDTO dto =
        RegisterRequestDTO.builder()
            .name("Usuário Completo")
            .email("user@email.com")
            .password("senhaTop123")
            .build();

    Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }
}
