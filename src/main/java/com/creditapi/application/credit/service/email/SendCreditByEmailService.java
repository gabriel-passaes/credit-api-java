package com.creditapi.application.credit.service.email;

import com.creditapi.application.credit.usecase.email.GenerateCreditEmailSubjectUseCase;
import com.creditapi.application.credit.usecase.email.SendCreditByEmailUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.shared.email.EmailService;
import com.creditapi.infrastructure.credit.pdf.template.CreditHtmlTemplateBuilder;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendCreditByEmailService implements SendCreditByEmailUseCase {

  private static final Logger logger = LoggerFactory.getLogger(SendCreditByEmailService.class);

  private final CreditRepository creditRepository;
  private final EmailService emailService;
  private final GenerateCreditEmailSubjectUseCase subjectBuilder;

  public SendCreditByEmailService(
      CreditRepository creditRepository,
      EmailService emailService,
      GenerateCreditEmailSubjectUseCase subjectBuilder) {
    this.creditRepository = creditRepository;
    this.emailService = emailService;
    this.subjectBuilder = subjectBuilder;
  }

  @Override
  @Observed(name = "credit.email.send")
  public void execute(String creditNumber, String toEmail) {
    logger.info(
        "📤 Enviando nota fiscal por e-mail para crédito {} e destinatário {}",
        creditNumber,
        toEmail);

    Credit credit =
        creditRepository
            .findByCreditNumber(creditNumber)
            .orElseThrow(
                () -> new CreditNotFoundException("Crédito não encontrado: " + creditNumber));

    String html = CreditHtmlTemplateBuilder.buildHtml(credit);
    String subject = subjectBuilder.execute(credit);

    emailService.sendEmail(toEmail, subject, html);
    logger.info("✅ E-mail enviado com sucesso para {} com crédito {}", toEmail, creditNumber);
  }
}
