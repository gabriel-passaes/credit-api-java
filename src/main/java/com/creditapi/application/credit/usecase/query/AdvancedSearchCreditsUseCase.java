package com.creditapi.application.credit.usecase.query;

import com.creditapi.application.credit.dto.response.CreditResponseDTO;
import com.creditapi.application.credit.dto.search.CreditSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdvancedSearchCreditsUseCase {
  Page<CreditResponseDTO> execute(CreditSearchCriteria criteria, Pageable pageable);
}
