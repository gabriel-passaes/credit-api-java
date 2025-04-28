package com.creditapi.presentation.shared.exception;

import com.creditapi.domain.auth.exception.InvalidCredentialsException;
import com.creditapi.domain.auth.exception.RefreshTokenExpiredException;
import com.creditapi.domain.auth.exception.RefreshTokenInvalidException;
import com.creditapi.domain.auth.exception.SocialLoginException;
import com.creditapi.domain.credit.exception.BusinessException;
import com.creditapi.domain.credit.exception.CreditFileNotFoundException;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.exception.DuplicateCreditException;
import com.creditapi.domain.credit.exception.IntegrationException;
import com.creditapi.domain.credit.exception.InvalidCreditRateException;
import com.creditapi.domain.credit.exception.UnauthorizedAccessException;
import com.creditapi.domain.credit.exception.UnauthorizedCreditUpdateException;
import com.creditapi.domain.credit.exception.ValidationException;
import com.creditapi.domain.invoice.exception.InvoiceNotFoundException;
import com.creditapi.domain.user.exception.CnpjAlreadyRegisteredException;
import com.creditapi.domain.user.exception.CpfAlreadyRegisteredException;
import com.creditapi.domain.user.exception.EmailAlreadyRegisteredException;
import com.creditapi.domain.user.exception.InvalidCnpjException;
import com.creditapi.domain.user.exception.InvalidCpfException;
import com.creditapi.domain.user.exception.UnauthorizedUserOperationException;
import com.creditapi.domain.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> handleConstraint(ConstraintViolationException ex) {
    return ResponseEntity.badRequest().body("Erro de validação: " + ex.getMessage());
  }

  @ExceptionHandler(CreditNotFoundException.class)
  public ResponseEntity<String> handleCreditNotFound(CreditNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(DuplicateCreditException.class)
  public ResponseEntity<String> handleDuplicateCredit(DuplicateCreditException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(InvalidCreditRateException.class)
  public ResponseEntity<String> handleInvalidCreditRate(InvalidCreditRateException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler(CreditFileNotFoundException.class)
  public ResponseEntity<String> handleCreditFileNotFound(CreditFileNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(InvoiceNotFoundException.class)
  public ResponseEntity<String> handleInvoiceNotFound(InvoiceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyRegisteredException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(CpfAlreadyRegisteredException.class)
  public ResponseEntity<String> handleCpfAlreadyExists(CpfAlreadyRegisteredException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(CnpjAlreadyRegisteredException.class)
  public ResponseEntity<String> handleCnpjAlreadyExists(CnpjAlreadyRegisteredException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(InvalidCpfException.class)
  public ResponseEntity<String> handleInvalidCpf(InvalidCpfException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(InvalidCnpjException.class)
  public ResponseEntity<String> handleInvalidCnpj(InvalidCnpjException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(UnauthorizedUserOperationException.class)
  public ResponseEntity<String> handleUnauthorizedUser(UnauthorizedUserOperationException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(RefreshTokenInvalidException.class)
  public ResponseEntity<String> handleInvalidToken(RefreshTokenInvalidException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(RefreshTokenExpiredException.class)
  public ResponseEntity<String> handleExpiredToken(RefreshTokenExpiredException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(SocialLoginException.class)
  public ResponseEntity<String> handleSocialLogin(SocialLoginException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(UnauthorizedAccessException.class)
  public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler(UnauthorizedCreditUpdateException.class)
  public ResponseEntity<String> handleUnauthorizedCreditUpdate(
      UnauthorizedCreditUpdateException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<String> handleBusiness(BusinessException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<String> handleValidationCustom(ValidationException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(IntegrationException.class)
  public ResponseEntity<String> handleIntegration(IntegrationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body("Erro de integração: " + ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Erro interno: " + ex.getMessage());
  }
}
