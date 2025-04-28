package com.creditapi.unit.application.invoice.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.creditapi.application.invoice.dto.request.DownloadInvoiceRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DownloadInvoiceRequestDTOTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve ser válido com todos os campos preenchidos corretamente")
  void shouldBeValidWhenAllFieldsArePresent() {
    DownloadInvoiceRequestDTO dto = new DownloadInvoiceRequestDTO("NF001", "12345678901234");

    Set<ConstraintViolation<DownloadInvoiceRequestDTO>> violations = validator.validate(dto);

    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve falhar quando CNPJ estiver nulo")
  void shouldFailWhenCnpjIsNull() {
    DownloadInvoiceRequestDTO dto = new DownloadInvoiceRequestDTO("NF001", null);

    Set<ConstraintViolation<DownloadInvoiceRequestDTO>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cnpj")));
  }

  @Test
  @DisplayName("Deve falhar quando invoiceNumber estiver nulo")
  void shouldFailWhenInvoiceNumberIsNull() {
    DownloadInvoiceRequestDTO dto = new DownloadInvoiceRequestDTO(null, "12345678901234");

    Set<ConstraintViolation<DownloadInvoiceRequestDTO>> violations = validator.validate(dto);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("invoiceNumber")));
  }

  @Test
  @DisplayName("Deve falhar quando ambos os campos forem nulos")
  void shouldFailWhenBothFieldsAreNull() {
    DownloadInvoiceRequestDTO dto = new DownloadInvoiceRequestDTO(null, null);

    Set<ConstraintViolation<DownloadInvoiceRequestDTO>> violations = validator.validate(dto);

    assertEquals(2, violations.size());
  }
}
