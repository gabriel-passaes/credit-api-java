package com.creditapi.application.credit.service.email;

import com.creditapi.application.credit.usecase.email.GenerateCreditEmailSubjectUseCase;
import com.creditapi.domain.credit.model.Credit;
import org.springframework.stereotype.Service;

@Service
public class GenerateCreditEmailSubjectService implements GenerateCreditEmailSubjectUseCase {

  @Override
  public String execute(Credit credit) {
    return String.format(
        "Nota Fiscal - Crédito %s (%s)", credit.getCreditNumber(), credit.getCreditType());
  }
}
