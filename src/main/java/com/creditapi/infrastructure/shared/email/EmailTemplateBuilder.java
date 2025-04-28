package com.creditapi.infrastructure.shared.email;

public interface EmailTemplateBuilder {

  String buildWelcome(String name);

  String buildPasswordRecovery(String name, String resetLink);
}
