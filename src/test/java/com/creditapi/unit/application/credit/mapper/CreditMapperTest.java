package com.creditapi.unit.application.credit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.creditapi.application.credit.mapper.CreditMapper;
import com.creditapi.domain.credit.model.Credit;
import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.credit.persistence.entity.CreditEntity;
import com.creditapi.infrastructure.user.persistence.entity.UserEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreditMapperTest {

  private final CreditMapper creditMapper = new CreditMapper();

  @Test
  @DisplayName("Deve converter CreditEntity para Credit (domínio) corretamente")
  void shouldMapEntityToDomain() {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setName("Fulano");
    userEntity.setEmail("fulano@email.com");
    userEntity.setDocument("12345678900");
    userEntity.setRole("USER");

    CreditEntity entity = new CreditEntity();
    entity.setId(1L);
    entity.setCreditNumber("123");
    entity.setNfseNumber("456");
    entity.setConstitutionDate(LocalDate.of(2024, 3, 1));
    entity.setIssqnAmount(new BigDecimal("100"));
    entity.setCreditType("ISSQN");
    entity.setSimpleNational(true);
    entity.setRate(new BigDecimal("5.0"));
    entity.setBilledAmount(new BigDecimal("10000"));
    entity.setDeductionAmount(new BigDecimal("1000"));
    entity.setCalculationBase(new BigDecimal("9000"));
    entity.setUploadedFileName("nota.pdf");
    entity.setUploadedFilePath("credits/1/nota.pdf");
    entity.setInvoiceUploaded(true);
    entity.setUser(userEntity);

    Credit credit = creditMapper.toDomain(entity);

    assertEquals("123", credit.getCreditNumber());
    assertEquals("nota.pdf", credit.getUploadedFileName());
    assertTrue(credit.isInvoiceUploaded());

    assertNotNull(credit.getUser());
    assertEquals(1L, credit.getUser().getId());
    assertEquals("Fulano", credit.getUser().getName());
    assertEquals("fulano@email.com", credit.getUser().getEmail());
    assertEquals("12345678900", credit.getUser().getDocument());
    assertEquals(Role.USER, credit.getUser().getRole());
  }

  @Test
  @DisplayName("Deve converter Credit (domínio) para CreditEntity corretamente")
  void shouldMapDomainToEntity() {
    User user =
        User.builder()
            .id(2L)
            .name("Beltrano")
            .email("beltrano@email.com")
            .document("98765432100")
            .role(Role.ADMIN)
            .build();

    Credit credit =
        Credit.builder()
            .id(2L)
            .creditNumber("987")
            .nfseNumber("654")
            .constitutionDate(LocalDate.of(2024, 2, 20))
            .issqnAmount(new BigDecimal("200"))
            .creditType("Outros")
            .simpleNational(false)
            .rate(new BigDecimal("2.0"))
            .billedAmount(new BigDecimal("8000"))
            .deductionAmount(new BigDecimal("1000"))
            .calculationBase(new BigDecimal("7000"))
            .uploadedFileName("xml.xml")
            .uploadedFilePath("credits/2/xml.xml")
            .invoiceUploaded(false)
            .user(user)
            .build();

    CreditEntity entity = creditMapper.toEntity(credit);

    assertEquals("987", entity.getCreditNumber());
    assertEquals("xml.xml", entity.getUploadedFileName());
    assertFalse(entity.isInvoiceUploaded());

    assertNotNull(entity.getUser());
    assertEquals(2L, entity.getUser().getId());
  }
}
