package com.creditapi.application.credit.usecase.query;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetCreditsByNfseUseCase {
  Page<CreditResponseDTO> execute(String nfseNumber, Pageable pageable);
}
