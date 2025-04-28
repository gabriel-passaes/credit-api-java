package com.creditapi.unit.application.auth.service.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;
import com.creditapi.application.auth.service.email.SendRecoverEmailService;
import com.creditapi.domain.shared.email.EmailService;
import com.creditapi.infrastructure.shared.email.EmailTemplateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SendRecoverEmailServiceTest {

  private EmailService emailService;
  private EmailTemplateBuilder templateBuilder;
  private SendRecoverEmailService service;

  @BeforeEach
  void setup() {
    emailService = mock(EmailService.class);
    templateBuilder = mock(EmailTemplateBuilder.class);
    service = new SendRecoverEmailService(emailService, templateBuilder);
  }

  @Test
  @DisplayName("Deve enviar e-mail de recuperação com sucesso")
  void givenValidInput_whenExecute_thenSendEmailSuccessfully() {
    String to = "user@example.com";
    String name = "User";
    String token = "token123";

    when(templateBuilder.buildPasswordRecovery(
            name, "https://app.seudominio.com/reset-password?token=" + token))
        .thenReturn("Conteúdo do e-mail");

    PasswordRecoveryResponseDTO response = service.execute(to, name, token);

    verify(emailService).sendEmail(eq(to), contains("Recuperação"), anyString());
    assertNotNull(response);
    assertTrue(response.getMessage().contains(to));
  }

  @Test
  @DisplayName("Deve lançar exceção se template estiver vazio")
  void givenEmptyTemplate_whenExecute_thenThrowException() {
    String to = "user@example.com";
    String name = "User";
    String token = "token123";

    when(templateBuilder.buildPasswordRecovery(any(), any())).thenReturn("");

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> service.execute(to, name, token));

    assertEquals("Conteúdo do e-mail de recuperação está vazio", exception.getMessage());
    verify(emailService, never()).sendEmail(any(), any(), any());
  }

  @Test
  @DisplayName("Deve capturar erro inesperado no envio do e-mail")
  void givenEmailFailure_whenExecute_thenThrowException() {
    String to = "fail@example.com";
    String name = "User";
    String token = "fail-token";

    when(templateBuilder.buildPasswordRecovery(any(), any())).thenReturn("email content");
    doThrow(new RuntimeException("Falha no SMTP"))
        .when(emailService)
        .sendEmail(any(), any(), any());

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> service.execute(to, name, token));

    assertEquals("Erro ao enviar e-mail de recuperação: Falha no SMTP", exception.getMessage());
  }
}
