package com.creditapi.application.credit.usecase.email;

import com.creditapi.domain.credit.model.Credit;

public interface GenerateCreditEmailSubjectUseCase {
  String execute(Credit credit);
}
