package com.creditapi.application.credit.usecase.email;

public interface SendCreditByEmailUseCase {
  void execute(String creditNumber, String toEmail);
}
