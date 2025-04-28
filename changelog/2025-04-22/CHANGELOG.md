## ✅ 🗓️ Changelog: 21 e 22 de abril

### 🧱️ Estrutura e arquitetura
- ✅ Padronizado **módulo `user`** no mesmo formato do `credit`
- ✅ Repositório dividido em **domain / application / infrastructure / shared**
- ✅ Nomenclatura unificada (**camelCase**, SOLID, Clean Architecture)
- ✅ `UserJpaRepository` espelhando o contrato de domínio e aceitando JPQL dinâmica
- ✅ `UserRepositoryImpl` com logs, eventos e delegação correta ao JPA

---

### 📦 Domínio
- ✅ `User.java`: agregado com regra “apenas CPF **ou** CNPJ”
- ✅ `Role.java`: enum de papéis (`SUPER_ADMIN`, `ADMIN`, `USER`)
- ✅ Exceções específicas  
  - `EmailAlreadyRegisteredException`  
  - `CpfAlreadyRegisteredException`  
  - `CnpjAlreadyRegisteredException`  
  - `InvalidCpfException`, `InvalidCnpjException`  
  - `UnauthorizedUserOperationException`
- ✅ **Testes**: builder, `equals/hashCode`, hierarquia de todas as exceções

---

### 🧠 Application
- ✅ Mapper: `UserMapper.java`
- ✅ DTOs com Bean‑Validation  
  - `CreateUserRequestDTO`, `UpdateUserRequestDTO`, `UserSearchDTO`
- ✅ Use Cases completos  
  - Create, Update, Delete, **DeleteMultiple**, GetById, GetByEmail, Pagination, Advanced Search, Upload Avatar
- ✅ Logs, validação de duplicidade e regras de permissão
- ✅ **Testes unitários** para cada Use Case + DTO validation

---

### 🌐 Infrastructure
- ✅ `UserJpaRepository.java` com métodos `findByEmail`, `exists*`, `search(...)`
- ✅ `UserRepositoryImpl.java` (save, find, exists, delete, pagination, search)
- ✅ Validadores: `CpfValidator`, `CnpjValidator`, `EmailValidator`
- ✅ `UserController.java`  
  - Endpoints CRUD, search avançado, upload de avatar  
  - Swagger/OpenAPI, logs, Bean‑Validation
- ✅ **Testes unitários**  
  - Repositório (Mockito)  
  - Validadores de documento/e‑mail  
  - Controller stand‑alone (MockMvc) cobrindo happy‑paths, 400, 404, payload vazio, duplicidade

---

### 🧪 Testes
- ✅ Mais de **40 casos de teste** novos em `src/test/java`  
  - Domínio, DTO, Use Cases, Mapper, Validators, Repository, Controller
- ✅ Cobrem cenários **felizes, exceções e bordas**  
  - Payload inválido, duplicidade, não‑encontrado, permissões, arquivo vazio
- 🕒 **Integrações (JPA + JWT)** planejadas para etapa final

---

### ⚙️ Shared
- ✅ `GlobalExceptionHandler` ajustado para mapear todas as novas exceções  
  - 400 (Bad Request) para duplicidades/validação  
  - 404 (Not Found) para `EntityNotFoundException`  
  - 403 (Forbidden) para operações não autorizadas

---

> **Resultado:** módulo `user` completamente alinhado ao padrão do projeto, com cobertura unitária robusta e pronto para testes de integração e segurança JWT na próxima fase.

