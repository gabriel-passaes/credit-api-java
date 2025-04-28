# 💳 Credit API – Clean Architecture, Modular, Kafka, Redis, Observability, Testes Fortes

API completa para gestão de créditos fiscais, usuários, autenticação e emissão de notas fiscais.  
Desenvolvida em **Java 17 + Spring Boot 3.2+**, seguindo **Clean Architecture**, com **Kafka**, **Redis**, **PostgreSQL**, **Micrometer**, **Docker** e **Testes Avançados**.

---

## 📂 Estrutura de Pastas

```bash
credit-api/
├── src/
│   ├── main/
│   │   ├── java/com/creditapi/
│   │   │
│   │   │── modules/
│   │   │   ├── auth/                    # Autenticação (login, registro, refresh, social login)
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/            # Entidades (UserAuth, AuthToken)
│   │   │   │   │   ├── gateway/          # Interfaces de serviço (UserAuthRepository, TokenService)
│   │   │   │   │   └── exception/        # Exceções específicas de auth
│   │   │   │   ├── application/
│   │   │   │   │   ├── usecase/          # Interfaces de casos de uso
│   │   │   │   │   └── service/          # Implementações de casos de uso
│   │   │   │   ├── infrastructure/
│   │   │   │   │   ├── security/         # JWT, filtros e configuração de segurança
│   │   │   │   │   ├── provider/         # Login social (Google, Facebook)
│   │   │   │   │   └── presentation/     # Controllers de autenticação (REST API)
│   │   │
│   │   │   ├── credit/                   # Gestão de créditos fiscais
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/            # Entidade Credit
│   │   │   │   │   ├── gateway/          # CreditRepository
│   │   │   │   │   └── exception/        # Exceções de crédito
│   │   │   │   ├── application/
│   │   │   │   │   ├── usecase/          # Casos de uso: criação, atualização, exclusão, consulta
│   │   │   │   │   └── service/          # Serviços de implementação
│   │   │   │   ├── infrastructure/
│   │   │   │   │   ├── persistence/      # JPA Repository (PostgreSQL)
│   │   │   │   │   ├── messaging/        # Produtor Kafka
│   │   │   │   │   ├── cache/            # Cache Redis
│   │   │   │   │   └── presentation/     # Controllers de crédito (REST API)
│   │   │
│   │   │   ├── invoice/                  # Emissão e gestão de notas fiscais
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/            # Entidade Invoice
│   │   │   │   │   ├── gateway/          # InvoiceProvider
│   │   │   │   │   └── exception/        # Exceções específicas de invoice
│   │   │   │   ├── application/
│   │   │   │   │   ├── usecase/          # Consultar status, baixar nota
│   │   │   │   │   └── service/          # Serviços de implementação
│   │   │   │   ├── infrastructure/
│   │   │   │   │   ├── provider/         # Integração com API externa (ex: TiraNota)
│   │   │   │   │   └── presentation/     # Controllers de notas fiscais (REST API)
│   │   │
│   │   │   ├── user/                     # Gestão de usuários
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/            # Entidade User
│   │   │   │   │   ├── gateway/          # UserRepository
│   │   │   │   │   └── exception/        # Exceções específicas de user
│   │   │   │   ├── application/
│   │   │   │   │   ├── usecase/          # Casos de uso: criar, atualizar, deletar, buscar usuário
│   │   │   │   │   └── service/          # Serviços de implementação
│   │   │   │   ├── infrastructure/
│   │   │   │   │   ├── persistence/      # UserRepository JPA
│   │   │   │   │   └── presentation/     # Controllers de usuários (REST API)
│   │   │
│   │   └── shared/
│   │       ├── exception/                # GlobalExceptionHandler + BaseCustomExceptions
│   │       ├── email/                    # Serviço genérico de envio de emails
│   │       ├── external/                 # Integrações externas genéricas (ex: ApiClient)
│   │       └── config/                   # Configurações globais (Swagger, Kafka, Redis, Observability)
│   │
│   └── resources/
│       ├── application.yml               # Configurações da aplicação
│       ├── logback-spring.xml             # Configuração de logs estruturados
│       └── db/
│           └── migration/                 # Scripts de migração (Flyway)
│
├── build.gradle                           # Build script (Gradle)
├── docker-compose.yml                     # Docker Compose (Postgres + Redis + Kafka)
├── Dockerfile                              # Dockerfile da aplicação
├── Makefile                                # Comandos build/start/test
├── README.md                               # Este arquivo!
├── .gitignore                              # Ignorados no Git
└── changelog/
    └── 2025-04-17/
        └── CHANGELOG.md                    # Histórico de alterações

## 🚀 Tecnologias Principais

- ☕ **Java 17**
- ⚡ **Spring Boot 3.2+**
- 🛢️ **PostgreSQL** (banco de dados)
- 📨 **Apache Kafka** (eventos de crédito)
- 🚀 **Redis** (cache de consultas)
- 🔥 **Docker + Docker Compose** (infraestrutura local)
- 📖 **Swagger / OpenAPI** (documentação)
- 📈 **Micrometer + Prometheus** (métricas)
- 🧪 **JUnit 5**, **Mockito**, **Testcontainers**, **Cucumber** (testes)
- 🎯 **JaCoCo + SonarQube** (cobertura e qualidade)

---

## ✅ Funcionalidades Implementadas

- 🔐 **Autenticação** (Login, Registro, Refresh Token, Social Login Google/Facebook)
- 💳 **Gestão de Créditos** (criar, buscar, atualizar, excluir, cache, eventos Kafka)
- 🧾 **Notas Fiscais** (consulta de status e download integrando API externa)
- 👤 **Usuários** (CRUD completo, upload de avatar, validações CPF/CNPJ)
- 📈 **Observabilidade Total** (MDC Logs + Prometheus Metrics)
- 🚀 **Cache Redis** em buscas
- 📖 **Documentação via Swagger** (atualizada automaticamente)
- 🧪 **Cobertura de Testes** com JUnit 5, Cucumber e Testcontainers

---

## 🧪 Rodando o Projeto

### 🐳 Com Docker + Docker Compose

```bash
make start

## 💻 Localmente (sem Docker)

1. Suba **Postgres**, **Redis** e **Kafka** manualmente.
2. Ajuste o arquivo `application.yml` para apontar para seus serviços locais.
3. Execute o projeto:

```bash
./gradlew bootRun

## 📚 Documentação Swagger

- Acesse: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📈 Observabilidade

- 📊 **Métricas** via `/actuator/prometheus`
- 📝 **Logs estruturados** (MDC) para trace completo de requisições
- 📡 **Exportação automática** para Prometheus + Grafana

---

## 🛠️ Testes e Qualidade

- 🔍 **Testes Unitários**: `src/test/java/com/creditapi/unit`
- 🔎 **Testes de Integração**: `src/test/java/com/creditapi/integration`
- 🧪 **Testes BDD (Cucumber)**: `src/test/java/com/creditapi/bdd`

### 📄 Relatório de Cobertura

Para gerar o relatório de cobertura:

```bash
make coverage

## 🛡️ Segurança

- 🔐 **JWT Tokens** protegendo todas as rotas privadas
- 🌎 **CORS** configurado
- 🚦 **Rate Limiter** em endpoints sensíveis
- 🔑 **Login Social** com validação externa (Google/Facebook)

---

## ✨ Filosofia

> **"Escalabilidade desde o primeiro commit. Clean Architecture como padrão, não como luxo."**

