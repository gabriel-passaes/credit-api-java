package com.creditapi.infrastructure.shared.email.implementation;

import com.creditapi.infrastructure.shared.email.EmailTemplateBuilder;
import org.springframework.stereotype.Component;

@Component
public class DefaultEmailTemplateBuilder implements EmailTemplateBuilder {

  @Override
  public String buildWelcome(String name) {
    return """
               <html>
                 <body>
                   <h1>Olá, %s!</h1>
                   <p>Seu cadastro foi realizado com sucesso no Credit API.</p>
                 </body>
               </html>
               """
        .formatted(name);
  }

  @Override
  public String buildPasswordRecovery(String name, String resetLink) {
    return """
               <html>
                 <body>
                   <h1>Olá, %s!</h1>
                   <p>Você solicitou recuperação de senha.</p>
                   <p>Clique <a href="%s">aqui</a> para redefinir sua senha.</p>
                   <p>Se não foi você, simplesmente ignore este e-mail.</p>
                 </body>
               </html>
               """
        .formatted(name, resetLink);
  }
}
