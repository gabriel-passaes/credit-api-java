package com.creditapi.unit.presentation.shared.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.creditapi.domain.credit.exception.BusinessException;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.exception.DuplicateCreditException;
import com.creditapi.domain.credit.exception.IntegrationException;
import com.creditapi.domain.credit.exception.InvalidCreditRateException;
import com.creditapi.domain.credit.exception.UnauthorizedAccessException;
import com.creditapi.domain.credit.exception.ValidationException;
import com.creditapi.domain.user.exception.UserNotFoundException;
import com.creditapi.presentation.shared.exception.GlobalExceptionHandler;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  @DisplayName("Deve retornar 404 para CreditNotFoundException")
  void shouldReturn404ForCreditNotFound() {
    ResponseEntity<String> response =
        handler.handleCreditNotFound(new CreditNotFoundException("Crédito não encontrado"));
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Crédito não encontrado", response.getBody());
  }

  @Test
  @DisplayName("Deve retornar 404 para UserNotFoundException")
  void shouldReturn404ForUserNotFound() {
    ResponseEntity<String> response =
        handler.handleUserNotFound(new UserNotFoundException("Usuário não encontrado"));
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Usuário não encontrado", response.getBody());
  }

  @Test
  @DisplayName("Deve retornar 422 para BusinessException")
  void shouldReturn422ForBusiness() {
    ResponseEntity<String> response =
        handler.handleBusiness(new BusinessException("Erro de negócio"));
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertEquals("Erro de negócio", response.getBody());
  }

  @Test
  @DisplayName("Deve retornar 400 com mensagens de validação")
  void shouldHandleValidationErrors() {
    BindingResult result = new BeanPropertyBindingResult(new Object(), "target");
    result.addError(new FieldError("target", "campo", "é obrigatório"));

    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, result);

    ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertThat(response.getBody()).containsEntry("campo", "é obrigatório");
  }

  @Test
  @DisplayName("Deve retornar 400 para ValidationException customizada")
  void shouldHandleValidationCustom() {
    ResponseEntity<String> response =
        handler.handleValidationCustom(new ValidationException("Campo inválido"));
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Campo inválido", response.getBody());
  }

  @Test
  @DisplayName("Deve retornar 502 para erro de integração")
  void shouldHandleIntegrationException() {
    ResponseEntity<String> response =
        handler.handleIntegration(new IntegrationException("Erro externo"));
    assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    assertTrue(response.getBody().contains("Erro de integração: Erro externo"));
  }

  @Test
  @DisplayName("Deve retornar 403 para acesso não autorizado")
  void shouldHandleUnauthorizedAccess() {
    ResponseEntity<String> response =
        handler.handleUnauthorizedAccess(new UnauthorizedAccessException("Sem permissão"));
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("Sem permissão", response.getBody());
  }

  @Test
  @DisplayName("Deve retornar 409 para crédito duplicado")
  void shouldHandleDuplicateCredit() {
    ResponseEntity<String> response =
        handler.handleDuplicateCredit(new DuplicateCreditException("Duplicado"));
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Duplicado", response.getBody());
  }

  @Test
  @DisplayName("Deve retornar 422 para alíquota inválida")
  void shouldHandleInvalidRate() {
    ResponseEntity<String> response =
        handler.handleInvalidCreditRate(new InvalidCreditRateException("Taxa inválida"));
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertEquals("Taxa inválida", response.getBody());
  }

  @Test
  @DisplayName("Deve retornar 500 para exceções genéricas")
  void shouldHandleGenericException() {
    ResponseEntity<String> response = handler.handleGeneric(new Exception("Falha inesperada"));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().contains("Erro interno: Falha inesperada"));
  }
}
