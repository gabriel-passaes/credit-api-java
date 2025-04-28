package com.creditapi.unit.application.credit.service.email;

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

import com.creditapi.application.credit.service.email.SendCreditByEmailService;
import com.creditapi.application.credit.usecase.email.GenerateCreditEmailSubjectUseCase;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.shared.email.EmailService;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes para SendCreditByEmailService")
class SendCreditByEmailServiceTest {

  private CreditRepository creditRepository;
  private EmailService emailService;
  private GenerateCreditEmailSubjectUseCase subjectBuilder;
  private SendCreditByEmailService useCase;

  @BeforeEach
  void setup() {
    creditRepository = mock(CreditRepository.class);
    emailService = mock(EmailService.class);
    subjectBuilder = mock(GenerateCreditEmailSubjectUseCase.class);
    useCase = new SendCreditByEmailService(creditRepository, emailService, subjectBuilder);
  }

  @Test
  @DisplayName("Deve enviar e-mail com HTML corretamente quando crédito existe")
  void shouldSendEmailWithHtmlSuccessfully() {
    String creditNumber = "CRED123";
    String toEmail = "cliente@teste.com";

    Credit credit = buildCredit(creditNumber);

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(subjectBuilder.execute(credit)).thenReturn("Assunto: Nota Fiscal");

    useCase.execute(creditNumber, toEmail);

    verify(emailService).sendEmail(eq(toEmail), eq("Assunto: Nota Fiscal"), contains("<html>"));
  }

  @Test
  @DisplayName("Deve lançar CreditNotFoundException quando crédito não existir")
  void shouldThrowExceptionWhenCreditNotFound() {
    when(creditRepository.findByCreditNumber("NOT_FOUND")).thenReturn(Optional.empty());

    assertThrows(
        CreditNotFoundException.class, () -> useCase.execute("NOT_FOUND", "cliente@teste.com"));

    verify(emailService, never()).sendEmail(any(), any(), any());
  }

  @Test
  @DisplayName("Deve lançar exceção se emailService falhar")
  void shouldThrowExceptionIfEmailFails() {
    String creditNumber = "CRED123";
    String toEmail = "cliente@teste.com";
    Credit credit = buildCredit(creditNumber);

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(subjectBuilder.execute(credit)).thenReturn("Nota");
    doThrow(new RuntimeException("Falha no envio"))
        .when(emailService)
        .sendEmail(any(), any(), any());

    assertThrows(RuntimeException.class, () -> useCase.execute(creditNumber, toEmail));
  }

  @Test
  @DisplayName("Deve tratar email vazio sem lançar exceção inesperada")
  void shouldHandleEmptyEmailGracefully() {
    String creditNumber = "CRED124";
    String emptyEmail = "";

    Credit credit = buildCredit(creditNumber);
    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(subjectBuilder.execute(credit)).thenReturn("Assunto Padrão");

    useCase.execute(creditNumber, emptyEmail);

    verify(emailService).sendEmail(eq(emptyEmail), eq("Assunto Padrão"), anyString());
  }

  @Test
  @DisplayName("Deve gerar HTML mesmo com dados incompletos do crédito")
  void shouldSendEmailEvenWithIncompleteCredit() {
    Credit credit = new Credit();
    credit.setCreditNumber("INCOMP");
    when(creditRepository.findByCreditNumber("INCOMP")).thenReturn(Optional.of(credit));
    when(subjectBuilder.execute(credit)).thenReturn("Assunto incompleto");

    useCase.execute("INCOMP", "destinatario@xpto.com");

    verify(emailService)
        .sendEmail(eq("destinatario@xpto.com"), eq("Assunto incompleto"), anyString());
  }

  private Credit buildCredit(String creditNumber) {
    User user =
        User.builder()
            .id(1L)
            .name("Fulano")
            .email("fulano@email.com")
            .document("12345678900")
            .role(Role.USER)
            .build();

    Credit credit = new Credit();
    credit.setId(1L);
    credit.setCreditNumber(creditNumber);
    credit.setNfseNumber("NF001");
    credit.setConstitutionDate(LocalDate.now());
    credit.setIssqnAmount(BigDecimal.TEN);
    credit.setCreditType("ISSQN");
    credit.setSimpleNational(true);
    credit.setRate(BigDecimal.ONE);
    credit.setBilledAmount(BigDecimal.valueOf(1000));
    credit.setDeductionAmount(BigDecimal.ZERO);
    credit.setCalculationBase(BigDecimal.valueOf(1000));
    credit.setUser(user);
    return credit;
  }
}
