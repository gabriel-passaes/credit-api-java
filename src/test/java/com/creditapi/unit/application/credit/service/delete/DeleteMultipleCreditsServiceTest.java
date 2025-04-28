package com.creditapi.unit.application.credit.service.delete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.creditapi.application.credit.service.delete.DeleteMultipleCreditsService;
import com.creditapi.domain.credit.gateway.repository.CreditRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("Testes para DeleteMultipleCreditsService")
class DeleteMultipleCreditsServiceTest {

  @Mock private CreditRepository creditRepository;

  @InjectMocks private DeleteMultipleCreditsService deleteMultipleCreditsService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Deve excluir todos os créditos existentes")
  void shouldDeleteAllCreditsSuccessfully_whenAllExist() {
    List<String> creditNumbers = List.of("CREDIT001", "CREDIT002");

    when(creditRepository.existsByCreditNumber("CREDIT001")).thenReturn(true);
    when(creditRepository.existsByCreditNumber("CREDIT002")).thenReturn(true);

    assertDoesNotThrow(() -> deleteMultipleCreditsService.execute(creditNumbers));

    verify(creditRepository).delete("CREDIT001");
    verify(creditRepository).delete("CREDIT002");
  }

  @Test
  @DisplayName("Deve pular exclusão de créditos inexistentes")
  void shouldSkipDeletion_whenCreditDoesNotExist() {
    List<String> creditNumbers = List.of("CREDIT001", "CREDIT002");

    when(creditRepository.existsByCreditNumber("CREDIT001")).thenReturn(true);
    when(creditRepository.existsByCreditNumber("CREDIT002")).thenReturn(false);

    assertDoesNotThrow(() -> deleteMultipleCreditsService.execute(creditNumbers));

    verify(creditRepository).delete("CREDIT001");
    verify(creditRepository, never()).delete("CREDIT002");
  }

  @Test
  @DisplayName("Deve lançar exceção se falhar ao deletar crédito existente")
  void shouldThrowException_whenDeleteFails() {
    List<String> creditNumbers = List.of("CREDIT001");

    when(creditRepository.existsByCreditNumber("CREDIT001")).thenReturn(true);
    doThrow(new RuntimeException("Falha na exclusão")).when(creditRepository).delete("CREDIT001");

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> deleteMultipleCreditsService.execute(creditNumbers));

    assertEquals("Falha na exclusão", exception.getMessage());
  }

  @Test
  @DisplayName("Deve ignorar execução se lista de créditos for vazia")
  void shouldDoNothing_whenCreditListIsEmpty() {
    List<String> emptyList = Collections.emptyList();

    assertDoesNotThrow(() -> deleteMultipleCreditsService.execute(emptyList));

    verifyNoInteractions(creditRepository);
  }

  @Test
  @DisplayName("Deve ignorar execução se lista de créditos for nula")
  void shouldDoNothing_whenCreditListIsNull() {
    assertDoesNotThrow(() -> deleteMultipleCreditsService.execute(null));

    verifyNoInteractions(creditRepository);
  }

  @Test
  @DisplayName("Deve processar corretamente mistura de existentes e inexistentes")
  void shouldDeleteOnlyExistingCredits_whenMixedExistence() {
    List<String> creditNumbers = List.of("CREDIT_EXIST", "CREDIT_MISSING");

    when(creditRepository.existsByCreditNumber("CREDIT_EXIST")).thenReturn(true);
    when(creditRepository.existsByCreditNumber("CREDIT_MISSING")).thenReturn(false);

    assertDoesNotThrow(() -> deleteMultipleCreditsService.execute(creditNumbers));

    verify(creditRepository).delete("CREDIT_EXIST");
    verify(creditRepository, never()).delete("CREDIT_MISSING");
  }

  @Test
  @DisplayName("Deve lançar exceção se existsByCreditNumber falhar para qualquer crédito")
  void shouldThrowException_whenExistsCheckFails() {
    List<String> creditNumbers = List.of("CREDIT001");

    when(creditRepository.existsByCreditNumber("CREDIT001"))
        .thenThrow(new RuntimeException("Erro no exists"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> deleteMultipleCreditsService.execute(creditNumbers));

    assertEquals("Erro no exists", exception.getMessage());
    verify(creditRepository, never()).delete(any());
  }
}
