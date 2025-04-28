package com.creditapi.presentation.shared.exception;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class TestUtils {

  public static MethodArgumentNotValidException buildMockValidationException(
      String field, String message) {
    BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
    bindingResult.addError(new FieldError("target", field, message));
    return new MethodArgumentNotValidException(null, bindingResult);
  }
}
