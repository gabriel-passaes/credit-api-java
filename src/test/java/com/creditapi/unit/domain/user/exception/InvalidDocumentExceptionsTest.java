package com.creditapi.unit.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.domain.shared.exception.GlobalDomainException;
import com.creditapi.domain.user.exception.InvalidCnpjException;
import com.creditapi.domain.user.exception.InvalidCpfException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class InvalidDocumentExceptionsTest {

  private static Stream<GlobalDomainException> exceptions() {
    return Stream.of(
        new InvalidCpfException("CPF inválido"), new InvalidCnpjException("CNPJ inválido"));
  }

  @ParameterizedTest(name = "{index} => exception={0}")
  @MethodSource("exceptions")
  @DisplayName("Deve herdar de GlobalDomainException e conter mensagem")
  void shouldExtendGlobalDomainExceptionAndContainMessage(GlobalDomainException exception) {
    assertThat(exception).isInstanceOf(GlobalDomainException.class);
    assertThat(exception.getMessage()).isNotBlank();
  }
}
