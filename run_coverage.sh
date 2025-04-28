#!/bin/bash

echo "🧪 Rodando testes e gerando cobertura com Jacoco..."

# Roda os testes com geração de cobertura
./gradlew clean test jacocoTestReport

# Verifica se o relatório foi gerado
REPORT_PATH="build/reports/jacoco/test/html/index.html"

if [ -f "$REPORT_PATH" ]; then
  echo "✅ Relatório gerado com sucesso em: $REPORT_PATH"
  echo "🌐 Abrindo no navegador..."

  # Detecta o sistema operacional e abre o navegador
  if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    xdg-open "$REPORT_PATH"
  elif [[ "$OSTYPE" == "darwin"* ]]; then
    open "$REPORT_PATH"
  elif [[ "$OSTYPE" == "msys" ]]; then
    start "$REPORT_PATH"
  else
    echo "⚠️ Não foi possível detectar o sistema para abrir o navegador automaticamente."
    echo "🧭 Acesse manualmente: file://$REPORT_PATH"
  fi
else
  echo "❌ O relatório não foi encontrado. Verifique se os testes passaram corretamente."
fi
