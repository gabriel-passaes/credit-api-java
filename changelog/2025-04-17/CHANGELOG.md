# 📦 Changelog – Credit API

Todas as alterações significativas registradas por versão.

---

## [v1.0.0] – 2025-04-17

### 🚀 Primeira entrega funcional (MVP robusto)

#### ✅ Funcionalidades
- Criação de crédito com validação e publicação de evento Kafka
- Consulta por número de crédito com cache (Redis) e log estruturado
- Consulta por NFS-e com paginação, cache e cálculo por estratégia
- Atualização de crédito com evento publicado em tópico Kafka
- Exclusão com validação de existência e log
- Listagem paginada geral de créditos
- Observabilidade via Micrometer (Actuator + Prometheus)
- Documentação da API via Swagger (OpenAPI 3)
- Logs estruturados com traceId, requestId e MDC configurado
- Exceções centralizadas com `GlobalExceptionHandler`

#### 🧪 Testes
- Testes unitários com cobertura total dos Use Cases
- Testes com Mockito, Testcontainers e Cucumber
- Cobertura de exceções (happy + sad path)
- Cobertura de cache, eventos, logs e repositórios

#### 🔧 Infraestrutura
- Integração com Redis (via Spring Cache)
- Integração com Kafka (Publisher de eventos)
- PostgreSQL via JPA
- Dockerfile e docker-compose com Redis + Postgres
- Makefile com comandos padrão de build, test, logs

#### 🧹 Qualidade de Código
- Checkstyle com regras de lint e validação
- Spotless com formatação automática padrão Google
- Logs 100% rastreáveis com MDC + traceId + thread
- Separação clara por camadas: application, domain, infrastructure, config

---

