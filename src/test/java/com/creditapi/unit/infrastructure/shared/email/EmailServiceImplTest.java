package com.creditapi.unit.infrastructure.shared.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.infrastructure.shared.email.EmailServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

  @Mock private JavaMailSender mailSender;

  @InjectMocks private EmailServiceImpl emailService;

  @Test
  @DisplayName("Deve enviar e-mail com sucesso")
  void shouldSendEmailSuccessfully() {
    String to = "teste@exemplo.com";
    String subject = "Assunto";
    String body = "<h1>Mensagem</h1>";

    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    assertDoesNotThrow(() -> emailService.sendEmail(to, subject, body));
    verify(mailSender).send(mimeMessage);
  }

  @Test
  @DisplayName("Deve lançar exceção ao falhar no envio")
  void shouldThrowExceptionWhenSendFails() {
    String to = "fail@teste.com";
    String subject = "Erro";
    String body = "Falha";

    when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Falha"));

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> emailService.sendEmail(to, subject, body));

    assertTrue(ex.getMessage().contains("Erro ao enviar e-mail"));
  }
}
