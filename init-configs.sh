#!/bin/bash

echo "🔧 Inicializando estrutura de configuração do projeto..."

# Criação dos diretórios
mkdir -p src/main/resources
mkdir -p src/test/resources

# application.yml
cat <<EOF > src/main/resources/application.yml
server:
  port: 8080

spring:
  application:
    name: credit-api

  datasource:
    url: jdbc:postgresql://localhost:5432/creditdb
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

  data:
    redis:
      host: localhost
      port: 6379

  jackson:
    serialization:
      fail-on-empty-beans: false

  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-password-or-app-token
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true

jwt:
  secret: your_jwt_secret_key_here
  expiration: 86400000

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    root: INFO
    com.creditapi: DEBUG
EOF

# application-test.yml
cat <<EOF > src/test/resources/application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

  data:
    redis:
      host: localhost
      port: 6379
EOF

# prometheus.yml
cat <<EOF > prometheus.yml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'credit-api'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8080']
EOF

# docker-compose.yml
cat <<EOF > docker-compose.yml
version: '3.9'

services:
  postgres:
    image: postgres:15
    container_name: postgres-db
    environment:
      POSTGRES_DB: creditdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql  # Novo volume para garantir que init.sql seja executado na inicialização

  redis:
    image: redis:7
    container_name: redis-cache
    ports:
      - "6379:6379"

  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
    depends_on:
      - app

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    volumes:
      - grafana_data:/var/lib/grafana
    depends_on:
      - prometheus

  app:
    build: .
    container_name: credit-api
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/creditdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379

volumes:
  postgres_data:
  grafana_data:
EOF

chmod +x init.sh
echo "✅ Estrutura criada com sucesso!"
