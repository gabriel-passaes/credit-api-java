package com.creditapi.application.credit.dto.search;

import java.time.LocalDate;

public record CreditSearchCriteria(
    String creditNumber,
    String nfseNumber,
    String creditType,
    LocalDate startDate,
    LocalDate endDate) {}
