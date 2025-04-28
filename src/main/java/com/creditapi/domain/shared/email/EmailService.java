package com.creditapi.domain.shared.email;

/**
 * Interface genérica para envio de e-mails. Pode ser implementada por diferentes serviços de envio
 * (SMTP, SES, SendGrid).
 */
public interface EmailService {

  /**
   * Envia um e-mail simples.
   *
   * @param to destinatário
   * @param subject assunto do e-mail
   * @param body corpo do e-mail (texto simples ou HTML)
   */
  void sendEmail(String to, String subject, String body);
}
