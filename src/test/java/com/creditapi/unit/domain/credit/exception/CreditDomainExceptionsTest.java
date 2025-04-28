package com.creditapi.unit.domain.credit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.creditapi.domain.credit.exception.BusinessException;
import com.creditapi.domain.credit.exception.CreditFileNotFoundException;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.exception.DuplicateCreditException;
import com.creditapi.domain.credit.exception.IntegrationException;
import com.creditapi.domain.credit.exception.InvalidCreditRateException;
import com.creditapi.domain.credit.exception.UnauthorizedAccessException;
import com.creditapi.domain.credit.exception.UnauthorizedCreditCreateException;
import com.creditapi.domain.credit.exception.UnauthorizedCreditUpdateException;
import com.creditapi.domain.credit.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes para Exceções do Domínio de Crédito")
class CreditDomainExceptionsTest {

  @Test
  @DisplayName("Deve lançar a exceção DuplicateCreditException corretamente")
  void shouldThrowDuplicateCreditException() {
    String message = "Crédito duplicado!";
    DuplicateCreditException exception = new DuplicateCreditException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        DuplicateCreditException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção IntegrationException corretamente")
  void shouldThrowIntegrationException() {
    String message = "Erro de integração!";
    IntegrationException exception = new IntegrationException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        IntegrationException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção InvalidCreditRateException corretamente")
  void shouldThrowInvalidCreditRateException() {
    String message = "Taxa de crédito inválida!";
    InvalidCreditRateException exception = new InvalidCreditRateException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        InvalidCreditRateException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção UnauthorizedAccessException corretamente")
  void shouldThrowUnauthorizedAccessException() {
    String message = "Acesso não autorizado!";
    UnauthorizedAccessException exception = new UnauthorizedAccessException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        UnauthorizedAccessException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção CreditNotFoundException corretamente")
  void shouldThrowCreditNotFoundException() {
    String message = "Crédito não encontrado!";
    CreditNotFoundException exception = new CreditNotFoundException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        CreditNotFoundException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção CreditFileNotFoundException corretamente")
  void shouldThrowCreditFileNotFoundException() {
    String message = "Arquivo de crédito não encontrado!";
    CreditFileNotFoundException exception = new CreditFileNotFoundException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        CreditFileNotFoundException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção BusinessException corretamente")
  void shouldThrowBusinessException() {
    String message = "Erro de negócio!";
    BusinessException exception = new BusinessException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        BusinessException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção UnauthorizedCreditCreateException corretamente")
  void shouldThrowUnauthorizedCreditCreateException() {
    String message = "Não autorizado a criar crédito!";
    UnauthorizedCreditCreateException exception = new UnauthorizedCreditCreateException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        UnauthorizedCreditCreateException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção UnauthorizedCreditUpdateException corretamente")
  void shouldThrowUnauthorizedCreditUpdateException() {
    String message = "Não autorizado a atualizar crédito!";
    UnauthorizedCreditUpdateException exception = new UnauthorizedCreditUpdateException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        UnauthorizedCreditUpdateException.class,
        () -> {
          throw exception;
        });
  }

  @Test
  @DisplayName("Deve lançar a exceção ValidationException corretamente")
  void shouldThrowValidationException() {
    String message = "Validação falhou!";
    ValidationException exception = new ValidationException(message);

    assertEquals(message, exception.getMessage());
    assertThrows(
        ValidationException.class,
        () -> {
          throw exception;
        });
  }
}
