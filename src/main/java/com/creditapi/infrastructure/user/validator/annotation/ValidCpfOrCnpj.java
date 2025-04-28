package com.creditapi.infrastructure.user.validator.annotation;

import com.creditapi.infrastructure.user.validator.CpfOrCnpjValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CpfOrCnpjValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCpfOrCnpj {
  String message() default "Documento inválido (CPF ou CNPJ)";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
