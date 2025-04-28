package com.creditapi.unit.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.domain.shared.exception.GlobalDomainException;
import com.creditapi.domain.user.exception.CnpjAlreadyRegisteredException;
import com.creditapi.domain.user.exception.CpfAlreadyRegisteredException;
import com.creditapi.domain.user.exception.EmailAlreadyRegisteredException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AlreadyRegisteredExceptionsTest {

  private static Stream<GlobalDomainException> exceptions() {
    return Stream.of(
        new EmailAlreadyRegisteredException("E‑mail já registrado"),
        new CpfAlreadyRegisteredException("CPF já registrado"),
        new CnpjAlreadyRegisteredException("CNPJ já registrado"));
  }

  @ParameterizedTest(name = "{index} => exception={0}")
  @MethodSource("exceptions")
  @DisplayName("Deve herdar de GlobalDomainException e conter mensagem")
  void shouldExtendGlobalDomainExceptionAndContainMessage(GlobalDomainException exception) {
    assertThat(exception).isInstanceOf(GlobalDomainException.class);
    assertThat(exception.getMessage()).isNotBlank();
  }
}
