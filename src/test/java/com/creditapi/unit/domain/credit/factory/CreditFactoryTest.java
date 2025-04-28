package com.creditapi.unit.domain.credit.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.creditapi.application.credit.dto.request.CreateCreditRequestDTO;
import com.creditapi.application.credit.dto.request.UpdateCreditRequestDTO;
import com.creditapi.domain.credit.factory.CreditFactory;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreditFactoryTest {

  private final CreditFactory creditFactory = new CreditFactory();

  @Test
  @DisplayName("Deve criar Credit a partir de CreateCreditRequestDTO")
  void shouldCreateCreditFromCreateRequest() {
    CreateCreditRequestDTO requestDTO =
        new CreateCreditRequestDTO(
            "CREDIT001",
            "NF001",
            LocalDate.of(2024, 1, 1),
            BigDecimal.valueOf(1000),
            "ISSQN",
            true,
            BigDecimal.valueOf(5.0),
            BigDecimal.valueOf(10000),
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(9500),
            1L);

    User user =
        User.builder()
            .id(1L)
            .name("Fulano")
            .email("fulano@email.com")
            .document("12345678900")
            .role(Role.USER)
            .build();

    Credit credit = creditFactory.fromRequest(requestDTO, user);

    assertNotNull(credit);
    assertEquals("CREDIT001", credit.getCreditNumber());
    assertEquals("NF001", credit.getNfseNumber());
    assertEquals(LocalDate.of(2024, 1, 1), credit.getConstitutionDate());
    assertEquals(BigDecimal.valueOf(1000), credit.getIssqnAmount());
    assertEquals("ISSQN", credit.getCreditType());
    assertEquals(1L, credit.getUser().getId());
  }

  @Test
  @DisplayName("Deve atualizar Credit a partir de UpdateCreditRequestDTO mantendo creditNumber")
  void shouldUpdateCreditFromUpdateRequest() {
    User existingUser =
        User.builder()
            .id(1L)
            .name("Fulano")
            .email("fulano@email.com")
            .document("12345678900")
            .role(Role.USER)
            .build();

    Credit existingCredit =
        Credit.builder()
            .id(1L)
            .creditNumber("CREDIT001")
            .nfseNumber("NF001")
            .constitutionDate(LocalDate.of(2024, 1, 1))
            .issqnAmount(BigDecimal.valueOf(1000))
            .creditType("ISSQN")
            .simpleNational(true)
            .rate(BigDecimal.valueOf(5.0))
            .billedAmount(BigDecimal.valueOf(10000))
            .deductionAmount(BigDecimal.valueOf(500))
            .calculationBase(BigDecimal.valueOf(9500))
            .user(existingUser)
            .build();

    UpdateCreditRequestDTO updateDTO =
        new UpdateCreditRequestDTO(
            1L,
            "NF002",
            LocalDate.of(2024, 2, 2),
            BigDecimal.valueOf(1500),
            "Outros",
            false,
            BigDecimal.valueOf(4.5),
            BigDecimal.valueOf(12000),
            BigDecimal.valueOf(800),
            BigDecimal.valueOf(11200),
            2L);

    Credit updatedCredit = creditFactory.updateFromRequest(existingCredit, updateDTO);

    assertNotNull(updatedCredit);
    assertEquals(1L, updatedCredit.getId());
    assertEquals("CREDIT001", updatedCredit.getCreditNumber());
    assertEquals("NF002", updatedCredit.getNfseNumber());
    assertEquals(LocalDate.of(2024, 2, 2), updatedCredit.getConstitutionDate());
    assertEquals(BigDecimal.valueOf(1500), updatedCredit.getIssqnAmount());
    assertEquals("Outros", updatedCredit.getCreditType());
    assertEquals(2L, updatedCredit.getUser().getId());
  }
}
