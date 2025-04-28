package com.creditapi.unit.application.credit.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.service.query.GetCreditsByNfseService;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.credit.strategy.StrategyContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("Testes para GetCreditsByNfseService")
class GetCreditsByNfseServiceTest {

  @Mock private CreditRepository creditRepository;

  @Mock private StrategyContext strategyContext;

  @InjectMocks private GetCreditsByNfseService getCreditsByNfseService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve retornar créditos paginados para NFS-e com imposto calculado corretamente")
  void shouldReturnPaginatedCreditsWithCalculatedTax() {
    String nfseNumber = "NF001";
    Pageable pageable = PageRequest.of(0, 2);

    Credit firstCredit = createCredit("CREDIT001", nfseNumber, "ISSQN");
    Credit secondCredit = createCredit("CREDIT002", nfseNumber, "SERVICE");

    Page<Credit> creditPage = new PageImpl<>(List.of(firstCredit, secondCredit), pageable, 2);

    when(creditRepository.findByNfseNumber(nfseNumber, pageable)).thenReturn(creditPage);
    when(strategyContext.calculate(eq("ISSQN"), eq(firstCredit)))
        .thenReturn(BigDecimal.valueOf(100));
    when(strategyContext.calculate(eq("SERVICE"), eq(secondCredit)))
        .thenReturn(BigDecimal.valueOf(25));

    Page<CreditResponseDTO> result = getCreditsByNfseService.execute(nfseNumber, pageable);

    assertEquals(2, result.getTotalElements());

    CreditResponseDTO firstDTO = result.getContent().get(0);
    assertEquals("CREDIT001", firstDTO.creditNumber());
    assertEquals(BigDecimal.valueOf(100), firstDTO.calculatedTax());

    CreditResponseDTO secondDTO = result.getContent().get(1);
    assertEquals("CREDIT002", secondDTO.creditNumber());
    assertEquals(BigDecimal.valueOf(25), secondDTO.calculatedTax());
  }

  @Test
  @DisplayName("Deve lançar CreditNotFoundException se nenhum crédito for encontrado")
  void shouldThrowExceptionWhenNoCreditsFound() {
    String nfseNumber = "NF000";
    Pageable pageable = PageRequest.of(0, 1);

    when(creditRepository.findByNfseNumber(nfseNumber, pageable)).thenReturn(Page.empty());

    assertThrows(
        CreditNotFoundException.class, () -> getCreditsByNfseService.execute(nfseNumber, pageable));
  }

  @Test
  @DisplayName("Deve calcular imposto para cada crédito individualmente")
  void shouldCalculateTaxForEachCredit() {
    String nfseNumber = "NF010";
    Pageable pageable = PageRequest.of(0, 2);

    Credit creditAlpha = createCredit("CREDIT_ALPHA", nfseNumber, "TYPE_A");
    Credit creditBeta = createCredit("CREDIT_BETA", nfseNumber, "TYPE_B");

    Page<Credit> page = new PageImpl<>(List.of(creditAlpha, creditBeta), pageable, 2);

    when(creditRepository.findByNfseNumber(nfseNumber, pageable)).thenReturn(page);
    when(strategyContext.calculate("TYPE_A", creditAlpha)).thenReturn(BigDecimal.valueOf(12));
    when(strategyContext.calculate("TYPE_B", creditBeta)).thenReturn(BigDecimal.valueOf(30));

    Page<CreditResponseDTO> result = getCreditsByNfseService.execute(nfseNumber, pageable);

    assertEquals(2, result.getContent().size());
    assertEquals(BigDecimal.valueOf(12), result.getContent().get(0).calculatedTax());
    assertEquals(BigDecimal.valueOf(30), result.getContent().get(1).calculatedTax());
  }

  @Test
  @DisplayName("Deve lançar RuntimeException se cálculo de imposto falhar para algum crédito")
  void shouldThrowExceptionIfTaxCalculationFails() {
    String nfseNumber = "NFERR";
    Pageable pageable = PageRequest.of(0, 1);

    Credit credit = createCredit("CREDIT_FAIL", nfseNumber, "TYPE_ERR");

    when(creditRepository.findByNfseNumber(nfseNumber, pageable))
        .thenReturn(new PageImpl<>(List.of(credit)));
    when(strategyContext.calculate("TYPE_ERR", credit))
        .thenThrow(new RuntimeException("Erro no cálculo de imposto"));

    RuntimeException ex =
        assertThrows(
            RuntimeException.class, () -> getCreditsByNfseService.execute(nfseNumber, pageable));

    assertEquals("Erro no cálculo de imposto", ex.getMessage());
  }

  private Credit createCredit(String creditNumber, String nfseNumber, String creditType) {
    User user =
        User.builder()
            .id(1L)
            .name("User Name")
            .email("user@email.com")
            .document("00000000000")
            .role(Role.USER)
            .build();

    return Credit.builder()
        .id(1L)
        .creditNumber(creditNumber)
        .nfseNumber(nfseNumber)
        .constitutionDate(LocalDate.now())
        .issqnAmount(BigDecimal.valueOf(200))
        .creditType(creditType)
        .simpleNational(false)
        .rate(BigDecimal.valueOf(3))
        .billedAmount(BigDecimal.valueOf(1000))
        .deductionAmount(BigDecimal.valueOf(100))
        .calculationBase(BigDecimal.valueOf(900))
        .user(user)
        .build();
  }
}
