package com.creditapi.unit.infrastructure.credit.pdf.template;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.credit.pdf.template.CreditHtmlTemplateBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreditHtmlTemplateBuilderTest {

  private Credit credit;

  @BeforeEach
  void setUp() {
    credit = new Credit();
    credit.setCreditNumber("CREDIT123");
    credit.setNfseNumber("NF001");
    credit.setConstitutionDate(LocalDate.now());
    credit.setIssqnAmount(BigDecimal.valueOf(1500));
    credit.setCreditType("ISSQN");
    credit.setSimpleNational(true);
    credit.setRate(BigDecimal.valueOf(5));
    credit.setBilledAmount(BigDecimal.valueOf(10000));
    credit.setDeductionAmount(BigDecimal.valueOf(1000));
    credit.setCalculationBase(BigDecimal.valueOf(9000));
    credit.setUser(new User());
    credit.getUser().setId(1L);
    credit.getUser().setName("John Doe");
    credit.getUser().setEmail("johndoe@example.com");
  }

  @Test
  @DisplayName("Deve gerar HTML corretamente com valores válidos")
  void shouldGenerateHtmlWithValidValues() {
    String html = CreditHtmlTemplateBuilder.buildHtml(credit);

    assertTrue(html.contains("CREDIT123"));
    assertTrue(html.contains("NF001"));
    assertTrue(html.contains("ISSQN"));
    assertTrue(html.contains("Sim"));
    assertTrue(html.contains("1500"));
    assertTrue(html.contains("5"));
    assertTrue(html.contains("10000"));
    assertTrue(html.contains("1000"));
    assertTrue(html.contains("9000"));
    assertTrue(html.contains("1"));
    assertTrue(html.contains("John Doe"));
    assertTrue(html.contains("johndoe@example.com"));
  }

  @Test
  @DisplayName("Deve gerar HTML mesmo com valores nulos ou vazios")
  void shouldGenerateHtmlWithNullOrEmptyValues() {
    credit.setCreditNumber(null);
    credit.setNfseNumber(null);
    credit.setIssqnAmount(null);

    String html = CreditHtmlTemplateBuilder.buildHtml(credit);

    assertTrue(html.contains("null"));
  }
}
