package com.creditapi.application.auth.usecase.email;

public interface SendWelcomeEmailUseCase {
  void execute(String to, String name);
}
