# ========================
# 🚀 COMANDOS DE EXECUÇÃO
# ========================

# Sobe os containers com rebuild das imagens (Postgres, Redis, etc)
start:
	docker-compose up --build

# Derruba os containers
stop:
	docker-compose down

# Acompanha os logs dos containers em tempo real
logs:
	docker-compose logs -f

# Roda a aplicação localmente via Gradle (sem Docker)
run-local:
	./gradlew bootRun

# ========================
# 🧪 TESTES E QUALIDADE
# ========================

# Executa os testes unitários e de integração + cobertura Jacoco
test:
	./gradlew clean test jacocoTestReport

# Executa apenas os testes do Cucumber (BDD)
test-bdd:
	./gradlew test --tests "*CucumberRunnerTest"

# Executa os testes + cobertura Jacoco + análise no SonarQube
quality:
	./gradlew clean test jacocoTestReport sonarqube

# Executa o linter (Spotless ou Checkstyle)
lint:
	./gradlew spotlessCheck || ./gradlew checkstyleMain checkstyleTest

# ========================
# 📦 BUILD & CLEAN
# ========================

# Limpa os builds antigos
clean:
	./gradlew clean

# Compila e empacota sem rodar testes
rebuild:
	./gradlew clean build -x test

# Compila, testa e empacota o projeto
build:
	./gradlew clean build

# ========================
# 🧬 FLYWAY (MIGRAÇÕES DB)
# ========================

# Aplica as migrations do Flyway
migrate:
	./gradlew flywayMigrate

# Verifica o status das migrations
flyway-info:
	./gradlew flywayInfo

# ========================
# 📖 SWAGGER / DOC
# ========================

# Abre a documentação OpenAPI no navegador
swagger:
	open http://localhost:8080/swagger-ui.html

# ========================
# 🐳 DOCKER
# ========================

# Constrói imagem Docker local
docker-build:
	docker build -t credit-api:latest .

# Executa container local da app
docker-run:
	docker run -p 8080:8080 credit-api:latest

# Sobe app no container + dependências (modo rápido)
up:
	docker-compose up

# Derruba todos os serviços
down:
	docker-compose down

# ========================
# ☁️ PUSH PRA REGISTRY (EX: Docker Hub)
# ========================

# Tag e push da imagem Docker para o repositório remoto
docker-push:
	docker tag credit-api:latest youruser/credit-api:latest
	docker push youruser/credit-api:latest
