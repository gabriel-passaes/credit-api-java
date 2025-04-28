package com.creditapi.infrastructure.credit.pdf.template;

import com.creditapi.domain.credit.model.Credit;

public class CreditHtmlTemplateBuilder {

  public static String buildHtml(Credit credit) {
    return """
            <!DOCTYPE html>
            <html lang='pt-BR'>
            <head>
                <meta charset='UTF-8'>
                <title>Nota Fiscal - Crédito</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    h1 { color: #2c3e50; }
                    table { width: 100%%; border-collapse: collapse; margin-top: 20px; }
                    th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }
                    th { background-color: #f4f4f4; }
                    .footer { margin-top: 40px; font-size: 12px; color: #666; text-align: center; }
                </style>
            </head>
            <body>
                <h1>Nota Fiscal - Crédito Gerado</h1>

                <table>
                    <tr><th>Número do Crédito</th><td>%s</td></tr>
                    <tr><th>Número da NFS-e</th><td>%s</td></tr>
                    <tr><th>Data de Constituição</th><td>%s</td></tr>
                    <tr><th>Tipo de Crédito</th><td>%s</td></tr>
                    <tr><th>Simples Nacional</th><td>%s</td></tr>
                    <tr><th>Valor ISSQN</th><td>R$ %s</td></tr>
                    <tr><th>Alíquota</th><td>%s%%%%</td></tr>
                    <tr><th>Valor Faturado</th><td>R$ %s</td></tr>
                    <tr><th>Valor Dedução</th><td>R$ %s</td></tr>
                    <tr><th>Base de Cálculo</th><td>R$ %s</td></tr>
                    <tr><th>ID do Usuário</th><td>%s</td></tr>
                    <tr><th>Nome do Usuário</th><td>%s</td></tr>
                    <tr><th>Email do Usuário</th><td>%s</td></tr>
                </table>

                <div class='footer'>
                    Documento gerado eletronicamente - Credit API
                </div>
            </body>
            </html>
            """
        .formatted(
            credit.getCreditNumber(),
            credit.getNfseNumber(),
            credit.getConstitutionDate(),
            credit.getCreditType(),
            credit.isSimpleNational() ? "Sim" : "Não",
            credit.getIssqnAmount(),
            credit.getRate(),
            credit.getBilledAmount(),
            credit.getDeductionAmount(),
            credit.getCalculationBase(),
            credit.getUser().getId(),
            credit.getUser().getName(),
            credit.getUser().getEmail());
  }
}
