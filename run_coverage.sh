#!/bin/bash

echo "🧪 Rodando testes e gerando cobertura com Jacoco..."

./gradlew clean test jacocoTestReport

REPORT_PATH="build/reports/jacoco/test/html/index.html"

if [ -f "$REPORT_PATH" ]; then
  echo "✅ Relatório gerado com sucesso em: $REPORT_PATH"
  echo "🌐 Abrindo no navegador..."

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
