package com.creditapi.infrastructure.user.validator;

import com.creditapi.infrastructure.user.validator.annotation.ValidCpfOrCnpj;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class CpfOrCnpjValidator implements ConstraintValidator<ValidCpfOrCnpj, String> {

  private final CpfValidator cpfValidator;
  private final CnpjValidator cnpjValidator;

  public CpfOrCnpjValidator(CpfValidator cpfValidator, CnpjValidator cnpjValidator) {
    this.cpfValidator = cpfValidator;
    this.cnpjValidator = cnpjValidator;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.trim().isEmpty()) return false;
    return cpfValidator.isValid(value) || cnpjValidator.isValid(value);
  }
}
