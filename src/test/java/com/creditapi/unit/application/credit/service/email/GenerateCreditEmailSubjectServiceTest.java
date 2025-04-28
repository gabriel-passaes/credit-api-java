package com.creditapi.unit.application.credit.service.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.creditapi.application.credit.service.email.GenerateCreditEmailSubjectService;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes para GenerateCreditEmailSubjectService")
class GenerateCreditEmailSubjectServiceTest {

  private final GenerateCreditEmailSubjectService service = new GenerateCreditEmailSubjectService();

  @Test
  @DisplayName("Deve gerar assunto corretamente com número e tipo do crédito")
  void shouldGenerateSubjectCorrectly() {
    Credit credit = buildCredit("CR123", "ISSQN");
    String subject = service.execute(credit);
    assertEquals("Nota Fiscal - Crédito CR123 (ISSQN)", subject);
  }

  @Test
  @DisplayName("Deve gerar assunto com número e tipo mesmo que sejam vazios")
  void shouldGenerateSubjectWithEmptyFields() {
    Credit credit = buildCredit("", "");
    String subject = service.execute(credit);
    assertEquals("Nota Fiscal - Crédito  ()", subject);
  }

  @Test
  @DisplayName("Deve gerar assunto com número nulo e tipo nulo")
  void shouldHandleNullValuesGracefully() {
    Credit credit = buildCredit(null, null);
    String subject = service.execute(credit);
    assertEquals("Nota Fiscal - Crédito null (null)", subject);
  }

  @Test
  @DisplayName("Deve gerar assunto mesmo com campos longos")
  void shouldGenerateSubjectWithLongValues() {
    String number = "CREDIT-2025-00000000000000000000000001";
    String type = "SUPER-ALÍQUOTA-ESPECIAL-TESTE";
    Credit credit = buildCredit(number, type);
    String subject = service.execute(credit);
    assertEquals("Nota Fiscal - Crédito " + number + " (" + type + ")", subject);
  }

  @Test
  @DisplayName("Não deve lançar exceção se crédito estiver incompleto")
  void shouldNotThrowExceptionWhenCreditIsIncomplete() {
    Credit credit = new Credit();
    assertDoesNotThrow(() -> service.execute(credit));
  }

  private Credit buildCredit(String number, String type) {
    Credit credit = new Credit();
    credit.setCreditNumber(number);
    credit.setCreditType(type);
    credit.setConstitutionDate(LocalDate.now());
    credit.setIssqnAmount(BigDecimal.ONE);
    credit.setRate(BigDecimal.TEN);
    credit.setBilledAmount(BigDecimal.valueOf(10000));
    credit.setDeductionAmount(BigDecimal.valueOf(500));
    credit.setCalculationBase(BigDecimal.valueOf(9500));
    credit.setUser(
        User.builder()
            .id(1L)
            .name("Fulano")
            .email("fulano@email.com")
            .document("12345678900")
            .role(Role.USER)
            .build());
    return credit;
  }
}
