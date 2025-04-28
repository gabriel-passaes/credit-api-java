package com.creditapi.application.auth.service.email;

import com.creditapi.application.auth.dto.response.PasswordRecoveryResponseDTO;
import com.creditapi.application.auth.usecase.email.SendRecoverEmailUseCase;
import com.creditapi.domain.shared.email.EmailService;
import com.creditapi.infrastructure.shared.email.EmailTemplateBuilder;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendRecoverEmailService implements SendRecoverEmailUseCase {

  private static final Logger log = LoggerFactory.getLogger(SendRecoverEmailService.class);

  private final EmailService emailService;
  private final EmailTemplateBuilder templateBuilder;

  @Value("${app.frontend.reset-password-url}")
  private String resetUrlPrefix;

  public SendRecoverEmailService(EmailService emailService, EmailTemplateBuilder templateBuilder) {
    this.emailService = emailService;
    this.templateBuilder = templateBuilder;
  }

  @Override
  @RateLimiter(name = "emailService")
  public PasswordRecoveryResponseDTO execute(String to, String name, String token) {
    log.debug("Enviando e-mail de recuperação de senha para {}", to);

    String subject = "Recuperação de senha - Credit API";
    String resetLink = resetUrlPrefix + token;
    String body = templateBuilder.buildPasswordRecovery(name, resetLink);

    emailService.sendEmail(to, subject, body);
    log.info("E-mail de recuperação enviado para {}", to);

    return PasswordRecoveryResponseDTO.builder()
        .message("E-mail de recuperação enviado para " + to)
        .build();
  }
}
