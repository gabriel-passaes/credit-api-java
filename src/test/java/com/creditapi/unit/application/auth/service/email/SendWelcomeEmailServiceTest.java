package com.creditapi.unit.application.auth.service.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.auth.service.email.SendWelcomeEmailService;
import com.creditapi.domain.shared.email.EmailService;
import com.creditapi.infrastructure.shared.email.EmailTemplateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SendWelcomeEmailServiceTest {

  private EmailService emailService;
  private EmailTemplateBuilder templateBuilder;
  private SendWelcomeEmailService service;

  @BeforeEach
  void setup() {
    emailService = mock(EmailService.class);
    templateBuilder = mock(EmailTemplateBuilder.class);
    service = new SendWelcomeEmailService(emailService, templateBuilder);
  }

  @Test
  @DisplayName("Deve enviar e-mail de boas-vindas com sucesso")
  void givenValidInput_whenExecute_thenSendWelcomeEmail() {
    when(templateBuilder.buildWelcome("Carlos")).thenReturn("Bem-vindo Carlos!");

    service.execute("carlos@dev.com", "Carlos");

    verify(emailService).sendEmail(eq("carlos@dev.com"), contains("Bem-vindo"), anyString());
  }

  @Test
  @DisplayName("Deve lançar exceção se template de boas-vindas for vazio")
  void givenEmptyTemplate_whenExecute_thenThrowException() {
    when(templateBuilder.buildWelcome(any())).thenReturn("");

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> service.execute("x@x.com", "x"));

    assertEquals("Conteúdo do e-mail de boas-vindas está vazio", ex.getMessage());
    verify(emailService, never()).sendEmail(any(), any(), any());
  }

  @Test
  @DisplayName("Deve capturar erro inesperado ao enviar e-mail de boas-vindas")
  void givenFailureOnSend_whenExecute_thenThrowException() {
    when(templateBuilder.buildWelcome(any())).thenReturn("Bem-vindo!");

    doThrow(new RuntimeException("Erro geral")).when(emailService).sendEmail(any(), any(), any());

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> service.execute("z@z.com", "Zé"));

    assertEquals("Erro ao enviar e-mail de boas-vindas: Erro geral", ex.getMessage());
  }
}
