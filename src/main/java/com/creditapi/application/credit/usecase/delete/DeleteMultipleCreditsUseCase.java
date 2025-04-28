package com.creditapi.application.credit.usecase.delete;

import java.util.List;

public interface DeleteMultipleCreditsUseCase {
  void execute(List<String> creditNumbers);
}
