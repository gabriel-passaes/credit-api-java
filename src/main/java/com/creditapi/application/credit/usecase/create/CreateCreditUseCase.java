package com.creditapi.application.credit.usecase.create;

import com.creditapi.application.credit.dto.request.CreateCreditRequestDTO;
import com.creditapi.application.credit.dto.response.CreateCreditResponseDTO;

public interface CreateCreditUseCase {
  CreateCreditResponseDTO execute(CreateCreditRequestDTO requestDTO);
}
