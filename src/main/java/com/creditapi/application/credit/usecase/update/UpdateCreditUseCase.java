package com.creditapi.application.credit.usecase.update;

import com.creditapi.application.credit.dto.request.UpdateCreditRequestDTO;
import com.creditapi.application.credit.dto.response.CreditResponseDTO;

public interface UpdateCreditUseCase {
  CreditResponseDTO execute(String creditNumber, UpdateCreditRequestDTO requestDTO);
}
