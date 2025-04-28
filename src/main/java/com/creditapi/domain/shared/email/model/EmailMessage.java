package com.creditapi.domain.shared.email.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Classe genérica que representa uma mensagem de e-mail. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {

  private String to; // destinatário
  private String subject; // assunto
  private String body; // corpo do email (texto simples ou HTML)
}
