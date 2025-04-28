package com.creditapi.infrastructure.user.validator;

import com.creditapi.infrastructure.user.validator.annotation.ValidCnpj;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class CnpjValidator implements ConstraintValidator<ValidCnpj, String>, DocumentValidator {

  private static final int[] FIRST_WEIGHTS = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
  private static final int[] SECOND_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

  @Override
  public boolean isValid(String cnpj, ConstraintValidatorContext context) {
    return isValid(cnpj);
  }

  @Override
  public boolean isValid(String cnpj) {
    if (cnpj == null || cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
      return false;
    }

    try {
      int firstCheckSum = calculateWeightedSum(cnpj, FIRST_WEIGHTS);
      int firstVerifierDigit = calculateVerifierDigit(firstCheckSum);

      int secondCheckSum = calculateWeightedSum(cnpj, SECOND_WEIGHTS);
      int secondVerifierDigit = calculateVerifierDigit(secondCheckSum);

      return firstVerifierDigit == Character.getNumericValue(cnpj.charAt(12))
          && secondVerifierDigit == Character.getNumericValue(cnpj.charAt(13));
    } catch (Exception e) {
      return false;
    }
  }

  private int calculateWeightedSum(String number, int[] weights) {
    int sum = 0;
    for (int i = 0; i < weights.length; i++) {
      int digit = Character.getNumericValue(number.charAt(i));
      sum += digit * weights[i];
    }
    return sum;
  }

  private int calculateVerifierDigit(int sum) {
    int result = 11 - (sum % 11);
    return (result >= 10) ? 0 : result;
  }
}
