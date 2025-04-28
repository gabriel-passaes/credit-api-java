## ✅ 🗓️ Changelog: 23 de abril de 2025 — Dia de Ogum 🛡️⚔️

### 🔐 Autenticação e segurança
- ✅ Testes **BDD com Cucumber** finalizados para o módulo `auth`
  - Login válido, inválido, usuário inexistente, refresh token
  - Testes com `RestTemplate`, `assertEquals`, `assertTrue`
- ✅ Testes BDD aplicados ao módulo `invoice` com:
  - Download de nota fiscal existente
  - Download com ID inválido (404)
  - Upload de PDF válido e rejeição de TXT inválido (400)

---

### 🛠️ Observabilidade e qualidade
- ✅ Configurado **Jacoco** com `run_coverage.sh` para gerar e abrir relatório HTML automaticamente
- ✅ Configurado **SonarQube** para análise contínua de qualidade via token + cobertura
- ✅ Configuração do **Prometheus** finalizada (`prometheus.yml`) com scraping a cada 5s
- ✅ Log central ativado com `logback-spring.xml`:
  - `credit-api.log` (geral) e `credit-api-error.log` (apenas erros)
  - Formato unificado e rota diária dos arquivos
  - Padrão `logs/` na raiz, tudo funcionando 100%

---

### 🐳 Docker e pipeline
- ✅ Criado e testado `Dockerfile` multi-stage otimizado
  - Build separado em estágio `build`, aplicação empacotada no final
- ✅ `.dockerignore` revisado e fortalecido para evitar lixo de IDE, logs, build e node_modules
- ✅ Arquivo `.gitignore` ajustado para garantir projeto limpo

---

### ⚙️ CI/CD completo (GitHub Actions)
- ✅ **CI** (`build.yml`) com:
  - Checkout, build, testes unitários, testes BDD, cobertura Jacoco, artefato JAR e SonarQube
- ✅ **CD** via Docker (`docker.yml`) com:
  - `docker build` e `docker push` automático pro Docker Hub
- ✅ **CD real com SSH** (`deploy.yml`) com:
  - Deploy automático via `push de tag vX.X.X`  
  - `docker pull`, `stop`, `rm` e `run` da nova imagem na VPS
  - Login seguro via `appleboy/ssh-action` + secrets criptografados

---

### 🔐 Secrets GitHub configurados
- ✅ `SONAR_TOKEN`
- ✅ `DOCKER_HUB_USERNAME`
- ✅ `DOCKER_HUB_TOKEN`
- ✅ `VPS_HOST`, `VPS_USER`, `VPS_SSH_KEY`

---

### ⟳ Organização final
- ✅ Estrutura `.github/workflows/` agora contém:
  - `build.yml` → CI com testes e qualidade
  - `docker.yml` → CD de imagem Docker
  - `deploy.yml` → CD com SSH para produção

---

> **Resultado:** pipeline completo, logs separados, testes Cucumber ativos, deploy automático em produção com GitHub Actions — tudo rodando como Ogum gosta: **limpo, forte e afiado.** 🛡️⚔️  
 