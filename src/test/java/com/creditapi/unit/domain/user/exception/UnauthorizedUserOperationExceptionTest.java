package com.creditapi.unit.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditapi.domain.shared.exception.GlobalDomainException;
import com.creditapi.domain.user.exception.UnauthorizedUserOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnauthorizedUserOperationExceptionTest {

  @Test
  @DisplayName("Deve herdar de GlobalDomainException e conter mensagem")
  void shouldExtendGlobalDomainExceptionAndContainMessage() {
    String message = "Apenas SUPER_ADMIN pode executar esta ação";

    UnauthorizedUserOperationException exception = new UnauthorizedUserOperationException(message);

    assertThat(exception).isInstanceOf(GlobalDomainException.class);
    assertThat(exception.getMessage()).isEqualTo(message);
  }
}
