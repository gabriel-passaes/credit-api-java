package com.creditapi.infrastructure.user.validator;

import com.creditapi.infrastructure.user.validator.annotation.ValidCpf;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class CpfValidator implements ConstraintValidator<ValidCpf, String>, DocumentValidator {

  @Override
  public boolean isValid(String cpf, ConstraintValidatorContext context) {
    return isValid(cpf);
  }

  @Override
  public boolean isValid(String cpf) {
    if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
      return false;
    }

    try {
      int firstCheckSum = 0;
      int secondCheckSum = 0;

      for (int i = 0; i < 9; i++) {
        int digit = Character.getNumericValue(cpf.charAt(i));
        firstCheckSum += digit * (10 - i);
        secondCheckSum += digit * (11 - i);
      }

      int firstVerifierDigit = calculateVerifierDigit(firstCheckSum);
      secondCheckSum += firstVerifierDigit * 2;
      int secondVerifierDigit = calculateVerifierDigit(secondCheckSum);

      return firstVerifierDigit == Character.getNumericValue(cpf.charAt(9))
          && secondVerifierDigit == Character.getNumericValue(cpf.charAt(10));
    } catch (Exception e) {
      return false;
    }
  }

  private int calculateVerifierDigit(int sum) {
    int result = 11 - (sum % 11);
    return (result >= 10) ? 0 : result;
  }
}
