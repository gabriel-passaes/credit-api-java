package com.creditapi.unit.application.credit.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.creditapi.application.credit.dto.request.UpdateCreditRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateCreditRequestDTOTest {

  private Validator validator;

  @BeforeEach
  void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve criar DTO válido para atualização de crédito")
  void shouldCreateValidDTO() {
    UpdateCreditRequestDTO dto =
        new UpdateCreditRequestDTO(
            1L,
            "NF123",
            LocalDate.now(),
            BigDecimal.valueOf(1000),
            "ISSQN",
            true,
            BigDecimal.valueOf(5.0),
            BigDecimal.valueOf(10000),
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(9500),
            1L);

    Set<ConstraintViolation<UpdateCreditRequestDTO>> violations = validator.validate(dto);
    assertEquals(0, violations.size());
  }

  @Test
  @DisplayName("Deve falhar se campos obrigatórios estiverem nulos ou inválidos")
  void shouldFailValidationForMissingFields() {
    UpdateCreditRequestDTO dto =
        new UpdateCreditRequestDTO(
            null,
            "",
            null,
            null,
            "",
            false,
            BigDecimal.valueOf(0),
            BigDecimal.ZERO,
            BigDecimal.valueOf(-1),
            BigDecimal.ZERO,
            null);

    Set<ConstraintViolation<UpdateCreditRequestDTO>> violations = validator.validate(dto);
    assertEquals(8, violations.size());
  }

  @Test
  @DisplayName("Deve falhar se a data de constituição for futura")
  void shouldFailIfDateIsInFuture() {
    UpdateCreditRequestDTO dto =
        new UpdateCreditRequestDTO(
            1L,
            "NF123",
            LocalDate.now().plusDays(1),
            BigDecimal.valueOf(1000),
            "ISSQN",
            true,
            BigDecimal.valueOf(5.0),
            BigDecimal.valueOf(10000),
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(9500),
            1L);

    Set<ConstraintViolation<UpdateCreditRequestDTO>> violations = validator.validate(dto);
    assertEquals(1, violations.size());
  }
}
