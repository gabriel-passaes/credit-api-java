#!/bin/bash

echo "📦 Gerando JAR Versionado..."

PROJECT_NAME="credit-api"
VERSION=$(grep "^version" build.gradle | cut -d '"' -f2)

./gradlew clean build -x test

mv build/libs/${PROJECT_NAME}-${VERSION}.jar ${PROJECT_NAME}-${VERSION}.jar

echo "✅ Gerado: ${PROJECT_NAME}-${VERSION}.jar"
