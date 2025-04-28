package com.creditapi.unit.application.credit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.creditapi.application.credit.dto.request.CreditFilterRequestDTO;
import com.creditapi.application.credit.mapper.CreditFilterMapper;
import com.creditapi.domain.credit.model.CreditFilter;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreditFilterMapperTest {

  @Test
  @DisplayName("Deve mapear CreditFilterRequestDTO para CreditFilter corretamente")
  void shouldMapRequestToDomain() {
    CreditFilterRequestDTO request = new CreditFilterRequestDTO();
    request.setNfseNumber("NF123");
    request.setCreditNumber("CREDIT001");
    request.setCreatedAfter(LocalDate.of(2023, 1, 1));
    request.setCreatedBefore(LocalDate.of(2023, 12, 31));
    request.setUserId(99L);

    CreditFilter filter = CreditFilterMapper.toDomain(request);

    assertEquals("NF123", filter.getNfseNumber());
    assertEquals("CREDIT001", filter.getCreditNumber());
    assertEquals(LocalDate.of(2023, 1, 1), filter.getCreatedAfter());
    assertEquals(LocalDate.of(2023, 12, 31), filter.getCreatedBefore());
    assertEquals(99L, filter.getUserId());
  }

  @Test
  @DisplayName("Deve lidar com campos nulos no DTO")
  void shouldHandleNullFields() {
    CreditFilterRequestDTO request = new CreditFilterRequestDTO();
    CreditFilter filter = CreditFilterMapper.toDomain(request);

    assertNull(filter.getNfseNumber());
    assertNull(filter.getCreditNumber());
    assertNull(filter.getCreatedAfter());
    assertNull(filter.getCreatedBefore());
    assertNull(filter.getUserId());
  }
}
