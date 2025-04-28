package com.creditapi.unit.application.credit.service.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.dto.request.UpdateCreditRequestDTO;
import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.service.update.UpdateCreditService;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.exception.UnauthorizedCreditUpdateException;
import com.creditapi.domain.credit.gateway.event.CreditEventPublisher;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("Testes para UpdateCreditService")
class UpdateCreditServiceTest {

  @Mock private CreditRepository creditRepository;

  @Mock private CreditEventPublisher eventPublisher;

  @InjectMocks private UpdateCreditService useCase;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve atualizar crédito existente com sucesso")
  void shouldUpdateCreditSuccessfully() {
    String creditNumber = "CREDIT_OK";
    Credit credit = createCreditWithUser(creditNumber, 1L, Role.USER);
    UpdateCreditRequestDTO dto = createUpdateRequest(1L);

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(creditRepository.save(credit)).thenReturn(credit);

    CreditResponseDTO result = useCase.execute(creditNumber, dto);

    assertNotNull(result);
    assertEquals(creditNumber, result.creditNumber());
    verify(creditRepository).save(credit);
    verify(eventPublisher).publishCreditUpdated(credit);
  }

  @Test
  @DisplayName("Deve lançar exceção se crédito não for encontrado")
  void shouldThrowIfCreditNotFound() {
    when(creditRepository.findByCreditNumber("NOT_FOUND")).thenReturn(Optional.empty());

    assertThrows(
        CreditNotFoundException.class, () -> useCase.execute("NOT_FOUND", createUpdateRequest(1L)));

    verify(creditRepository, never()).save(any());
    verify(eventPublisher, never()).publishCreditUpdated(any());
  }

  @Test
  @DisplayName("Deve lançar exceção se repositório falhar ao salvar")
  void shouldThrowIfSaveFails() {
    String creditNumber = "C_ERR";
    Credit credit = createCreditWithUser(creditNumber, 1L, Role.USER);
    UpdateCreditRequestDTO dto = createUpdateRequest(1L);

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(creditRepository.save(credit)).thenThrow(new RuntimeException("Save error"));

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> useCase.execute(creditNumber, dto));

    assertEquals("Save error", ex.getMessage());
    verify(eventPublisher, never()).publishCreditUpdated(any());
  }

  @Test
  @DisplayName("Deve permitir troca de usuário se for SUPER_ADMIN")
  void shouldAllowUserChangeForSuperAdmin() {
    String creditNumber = "C_SUPER";
    Credit credit = createCreditWithUser(creditNumber, 1L, Role.SUPER_ADMIN);
    UpdateCreditRequestDTO dto = createUpdateRequest(2L); // trocando usuário

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(creditRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    CreditResponseDTO result = useCase.execute(creditNumber, dto);

    assertEquals(2L, result.userId());
    verify(eventPublisher).publishCreditUpdated(any());
  }

  @Test
  @DisplayName("Deve lançar exceção se USER tentar trocar o dono do crédito")
  void shouldThrowIfNonAdminTriesToChangeUser() {
    String creditNumber = "C_DENIED";
    Credit credit = createCreditWithUser(creditNumber, 1L, Role.USER);
    UpdateCreditRequestDTO dto = createUpdateRequest(2L); // trocando userId

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));

    assertThrows(UnauthorizedCreditUpdateException.class, () -> useCase.execute(creditNumber, dto));

    verify(creditRepository, never()).save(any());
    verify(eventPublisher, never()).publishCreditUpdated(any());
  }

  @Test
  @DisplayName("Deve atualizar mesmo se campos forem iguais aos anteriores")
  void shouldUpdateEvenWithSameValues() {
    String creditNumber = "C_SAME";
    Credit credit = createCreditWithUser(creditNumber, 1L, Role.USER);
    UpdateCreditRequestDTO dto = createUpdateRequest(1L);

    when(creditRepository.findByCreditNumber(creditNumber)).thenReturn(Optional.of(credit));
    when(creditRepository.save(any())).thenReturn(credit);

    CreditResponseDTO result = useCase.execute(creditNumber, dto);

    assertEquals("C_SAME", result.creditNumber());
    verify(creditRepository).save(any());
  }

  private Credit createCreditWithUser(String creditNumber, Long userId, Role role) {
    User user =
        User.builder()
            .id(userId)
            .name("Usuário")
            .email("user@email.com")
            .document("12345678900")
            .role(role)
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
    credit.setDeductionAmount(BigDecimal.valueOf(100));
    credit.setCalculationBase(BigDecimal.valueOf(900));
    credit.setUser(user);

    return credit;
  }

  private UpdateCreditRequestDTO createUpdateRequest(Long userId) {
    return new UpdateCreditRequestDTO(
        1L,
        "NF001",
        LocalDate.now(),
        BigDecimal.valueOf(1500),
        "ISSQN",
        true,
        BigDecimal.valueOf(3.0),
        BigDecimal.valueOf(20000),
        BigDecimal.valueOf(2000),
        BigDecimal.valueOf(18000),
        userId);
  }
}
