package com.creditapi.unit.application.auth.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.auth.dto.request.RefreshTokenRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefreshTokenRequestDTOTest {

  private Validator validator;

  @BeforeEach
  void setUpValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve falhar se o refresh token estiver vazio")
  void givenBlankToken_whenValidate_thenFail() {
    RefreshTokenRequestDTO dto = RefreshTokenRequestDTO.builder().refreshToken("").build();

    Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("refreshToken"));
  }

  @Test
  @DisplayName("Deve passar se o refresh token for válido")
  void givenValidToken_whenValidate_thenPass() {
    RefreshTokenRequestDTO dto =
        RefreshTokenRequestDTO.builder().refreshToken("valid-refresh-token").build();

    Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }
}
