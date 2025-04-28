package com.creditapi.unit.application.auth.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.auth.dto.request.ResetPasswordRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResetPasswordRequestDTOTest {

  private Validator validator;

  @BeforeEach
  void setUpValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve falhar se o token de recuperação for nulo")
  void givenNullToken_whenValidate_thenShouldFailValidation() {
    ResetPasswordRequestDTO dto =
        ResetPasswordRequestDTO.builder().token(null).newPassword("novaSenhaSegura123").build();

    Set<ConstraintViolation<ResetPasswordRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("token"));
  }

  @Test
  @DisplayName("Deve falhar se a nova senha for nula")
  void givenNullNewPassword_whenValidate_thenShouldFailValidation() {
    ResetPasswordRequestDTO dto =
        ResetPasswordRequestDTO.builder().token("valid-token-123").newPassword(null).build();

    Set<ConstraintViolation<ResetPasswordRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("newPassword"));
  }

  @Test
  @DisplayName("Deve falhar se o token estiver vazio")
  void givenBlankToken_whenValidate_thenShouldFailValidation() {
    ResetPasswordRequestDTO dto =
        ResetPasswordRequestDTO.builder().token("").newPassword("novaSenha123").build();

    Set<ConstraintViolation<ResetPasswordRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("token"));
  }

  @Test
  @DisplayName("Deve passar com token e nova senha válidos")
  void givenValidFields_whenValidate_thenShouldPass() {
    ResetPasswordRequestDTO dto =
        ResetPasswordRequestDTO.builder()
            .token("valid-token")
            .newPassword("senha123Segura")
            .build();

    Set<ConstraintViolation<ResetPasswordRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }
}
