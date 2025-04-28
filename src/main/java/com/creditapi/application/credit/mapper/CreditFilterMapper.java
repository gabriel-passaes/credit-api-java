package com.creditapi.application.credit.mapper;

import com.creditapi.application.credit.dto.request.CreditFilterRequestDTO;
import com.creditapi.domain.credit.model.CreditFilter;

public class CreditFilterMapper {

  public static CreditFilter toDomain(CreditFilterRequestDTO request) {
    CreditFilter filter = new CreditFilter();
    filter.setNfseNumber(request.getNfseNumber());
    filter.setCreditNumber(request.getCreditNumber());
    filter.setCreatedAfter(request.getCreatedAfter());
    filter.setCreatedBefore(request.getCreatedBefore());
    filter.setUserId(request.getUserId());
    return filter;
  }
}
