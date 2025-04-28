package com.creditapi.unit.application.credit.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.creditapi.application.credit.dto.request.CreditFilterRequestDTO;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreditFilterRequestDTOTest {

  @Test
  @DisplayName("Deve criar DTO de filtro com todos os campos preenchidos")
  void shouldCreateFilterDTOWithAllFields() {
    CreditFilterRequestDTO dto = new CreditFilterRequestDTO();
    dto.setNfseNumber("NF001");
    dto.setCreditNumber("CREDIT001");
    dto.setCreatedAfter(LocalDate.now().minusDays(10));
    dto.setCreatedBefore(LocalDate.now());
    dto.setUserId(1L);

    assertEquals("NF001", dto.getNfseNumber());
    assertEquals("CREDIT001", dto.getCreditNumber());
    assertEquals(1L, dto.getUserId());
  }

  @Test
  @DisplayName("Deve criar DTO de filtro com campos nulos")
  void shouldCreateFilterDTOWithNullFields() {
    CreditFilterRequestDTO dto = new CreditFilterRequestDTO();

    assertNull(dto.getNfseNumber());
    assertNull(dto.getCreditNumber());
    assertNull(dto.getCreatedAfter());
    assertNull(dto.getCreatedBefore());
    assertNull(dto.getUserId());
  }
}
