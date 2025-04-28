package com.creditapi.application.auth.service.email;

import com.creditapi.application.auth.usecase.email.SendWelcomeEmailUseCase;
import com.creditapi.domain.shared.email.EmailService;
import com.creditapi.infrastructure.shared.email.EmailTemplateBuilder;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendWelcomeEmailService implements SendWelcomeEmailUseCase {

  private static final Logger log = LoggerFactory.getLogger(SendWelcomeEmailService.class);

  private final EmailService emailService;
  private final EmailTemplateBuilder templateBuilder;

  public SendWelcomeEmailService(EmailService emailService, EmailTemplateBuilder templateBuilder) {
    this.emailService = emailService;
    this.templateBuilder = templateBuilder;
  }

  @Override
  @RateLimiter(name = "emailService")
  public void execute(String to, String name) {
    log.debug("Enviando e-mail de boas-vindas para {}", to);
    String subject = "Bem-vindo ao Credit API!";
    String body = templateBuilder.buildWelcome(name);
    emailService.sendEmail(to, subject, body);
    log.info("E-mail de boas-vindas enviado para {}", to);
  }
}
