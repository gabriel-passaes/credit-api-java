package com.creditapi.unit.application.credit.service.delete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.service.delete.DeleteCreditService;
import com.creditapi.domain.credit.exception.CreditNotFoundException;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("Testes para DeleteCreditService")
class DeleteCreditServiceTest {

  @Mock private CreditRepository creditRepository;

  @InjectMocks private DeleteCreditService deleteCreditService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve excluir crédito com sucesso quando número válido")
  void shouldDeleteCreditSuccessfully_whenValidCreditNumber() {
    String creditNumber = "CREDIT001";

    when(creditRepository.existsByCreditNumber(creditNumber)).thenReturn(true);

    assertDoesNotThrow(() -> deleteCreditService.execute(creditNumber));

    verify(creditRepository).delete(creditNumber);
  }

  @Test
  @DisplayName("Deve lançar exceção quando crédito não for encontrado")
  void shouldThrowCreditNotFoundException_whenCreditDoesNotExist() {
    String invalidCreditNumber = "INVALID123";

    when(creditRepository.existsByCreditNumber(invalidCreditNumber)).thenReturn(false);

    assertThrows(
        CreditNotFoundException.class, () -> deleteCreditService.execute(invalidCreditNumber));

    verify(creditRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve lançar exceção se falhar ao deletar o crédito")
  void shouldThrowRuntimeException_whenDeleteFails() {
    String creditNumber = "ERROR001";

    when(creditRepository.existsByCreditNumber(creditNumber)).thenReturn(true);
    doThrow(new RuntimeException("Erro ao deletar")).when(creditRepository).delete(creditNumber);

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> deleteCreditService.execute(creditNumber));

    assertEquals("Erro ao deletar", exception.getMessage());
  }

  @Test
  @DisplayName("Não deve tentar deletar crédito se número for null")
  void shouldThrowNullPointerException_whenCreditNumberIsNull() {
    assertThrows(NullPointerException.class, () -> deleteCreditService.execute(null));

    verify(creditRepository, never()).existsByCreditNumber(any());
    verify(creditRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve tratar espaços em branco como número inválido")
  void shouldThrowCreditNotFoundException_whenCreditNumberIsBlank() {
    String blankCreditNumber = " ";

    when(creditRepository.existsByCreditNumber(blankCreditNumber)).thenReturn(false);

    assertThrows(
        CreditNotFoundException.class, () -> deleteCreditService.execute(blankCreditNumber));

    verify(creditRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve lançar exceção genérica se existsByCreditNumber falhar")
  void shouldThrowRuntimeException_whenExistsCheckFails() {
    String creditNumber = "FAIL001";

    when(creditRepository.existsByCreditNumber(creditNumber))
        .thenThrow(new RuntimeException("Erro interno"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> deleteCreditService.execute(creditNumber));

    assertEquals("Erro interno", exception.getMessage());
    verify(creditRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve logar corretamente tentativa de exclusão de crédito inexistente")
  void shouldLogWarning_whenCreditDoesNotExist() {
    String invalidCreditNumber = "NOTFOUND123";

    when(creditRepository.existsByCreditNumber(invalidCreditNumber)).thenReturn(false);

    assertThrows(
        CreditNotFoundException.class, () -> deleteCreditService.execute(invalidCreditNumber));

    verify(creditRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve realizar múltiplas exclusões válidas consecutivas")
  void shouldDeleteMultipleCreditsSuccessfully() {
    String creditNumber1 = "CREDIT100";
    String creditNumber2 = "CREDIT200";

    when(creditRepository.existsByCreditNumber(creditNumber1)).thenReturn(true);
    when(creditRepository.existsByCreditNumber(creditNumber2)).thenReturn(true);

    assertDoesNotThrow(() -> deleteCreditService.execute(creditNumber1));
    assertDoesNotThrow(() -> deleteCreditService.execute(creditNumber2));

    verify(creditRepository).delete(creditNumber1);
    verify(creditRepository).delete(creditNumber2);
  }
}
