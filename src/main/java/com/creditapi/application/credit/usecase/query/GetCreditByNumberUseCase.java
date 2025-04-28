package com.creditapi.application.credit.usecase.query;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;

public interface GetCreditByNumberUseCase {
  CreditResponseDTO execute(String creditNumber);
}
