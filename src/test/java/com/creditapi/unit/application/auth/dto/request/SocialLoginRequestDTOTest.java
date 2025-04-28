package com.creditapi.unit.application.auth.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.application.auth.dto.request.SocialLoginRequestDTO;
import com.creditapi.infrastructure.auth.provider.social.SocialProvider;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SocialLoginRequestDTOTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve retornar erro se o token estiver vazio")
  void givenBlankToken_whenValidate_thenConstraintViolation() {
    SocialLoginRequestDTO dto =
        SocialLoginRequestDTO.builder().provider(SocialProvider.GOOGLE).token("").build();

    Set<ConstraintViolation<SocialLoginRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("token"));
  }

  @Test
  @DisplayName("Deve retornar erro se o provider for nulo")
  void givenNullProvider_whenValidate_thenConstraintViolation() {
    SocialLoginRequestDTO dto =
        SocialLoginRequestDTO.builder().provider(null).token("validToken123").build();

    Set<ConstraintViolation<SocialLoginRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("provider"));
  }

  @Test
  @DisplayName("Deve ser válido com provider e token preenchidos")
  void givenValidDto_whenValidate_thenNoViolations() {
    SocialLoginRequestDTO dto =
        SocialLoginRequestDTO.builder()
            .provider(SocialProvider.FACEBOOK)
            .token("socialTokenABC")
            .build();

    Set<ConstraintViolation<SocialLoginRequestDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }
}
