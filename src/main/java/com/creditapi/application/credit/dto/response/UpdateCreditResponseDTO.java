package com.creditapi.application.credit.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCreditResponseDTO(
    String creditNumber,
    String nfseNumber,
    LocalDate constitutionDate,
    BigDecimal issqnAmount,
    String creditType,
    boolean simpleNational,
    BigDecimal rate,
    BigDecimal billedAmount,
    BigDecimal deductionAmount,
    BigDecimal calculationBase,
    BigDecimal calculatedTax,
    Long userId,
    String userName,
    String userEmail) {}
