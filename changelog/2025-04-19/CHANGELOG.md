## ✅ 🗓️ Changelog: 18 e 19 de abril

### 🧱️ Estrutura e arquitetura
- ✅ Definida estrutura Clean Architecture para o módulo `invoice`
- ✅ Separação por **domínio, aplicação, infraestrutura e compartilhados**
- ✅ Suporte a **múltiplos providers** (TiraNota e futuros)
- ✅ Criado diretório modular `tiranota` com `client`, `config` e `implementation`
- ✅ Adicionado `shared/external/ApiClient` genérico e desacoplado
- ✅ Nome de arquivos, pacotes e estruturas padronizados (`camelCase`, `SOLID`)

---

### 📦 Domínio
- ✅ `Invoice.java`: entidade de domínio com dados da nota fiscal
- ✅ `InvoiceProvider.java`: interface para desacoplamento de provider
- ✅ `InvoiceNotFoundException.java`: exceção personalizada herdando de `GlobalDomainException`

---

### 🧠 Application
- ✅ `DownloadInvoiceUseCase.java`: lógica de download de PDF da nota
- ✅ `ConsultInvoiceStatusUseCase.java`: lógica de consulta de status
- ✅ DTOs:
  - `DownloadInvoiceRequestDTO.java` (entrada)
  - `InvoiceStatusResponseDTO.java` (resposta com status, mensagem e data)

---

### 🌐 Infrastructure
- ✅ `InvoiceController.java`: endpoints `/invoice/status` e `/invoice/download` com log, Swagger, retorno completo
- ✅ `TiraNotaInvoiceProvider.java`: provider com `ApiClient`, log, fallback seguro
- ✅ `TiraNotaProperties.java`: mapeamento do `application.yml`
- ✅ `InvoiceProviderConfig.java`: seleção dinâmica de provider (via `@Bean`)
- ✅ `ExternalInvoiceStatusDTO.java`: DTO da resposta externa
- ✅ `RestTemplateApiClient.java`: implementação default genérica do client HTTP

---

### ⚙️ Shared
- ✅ `ApiClient.java`: interface genérica de chamadas externas
- ✅ `GlobalExceptionHandler.java`: atualizado para tratar todos os novos erros
- ✅ Exceptions do sistema ajustadas para herdar de `GlobalDomainException`

---

