# Changelog: 30 de abril de 2025 — Swagger & Seeding 🚀

## 📄 Documentação Swagger — Controllers Lapidados
- **AuthController**  
  - Inclusão de `@Operation` e respostas completas com `@ApiResponses`  
  - Uso de `@Content(schema = @Schema(...))` nos DTOs de sucesso  
  - Exemplos de erro (`401`, `400`, `404`) via `@ExampleObject`  

- **CreditController**  
  - `@Schema` aplicado em todos os DTOs de entrada/saída  
  - Tratamento de erros `400`, `404`, `422` com exemplos reais  
  - Estrutura original preservada, sem remoção de código  

- **InvoiceController**  
  - `@ApiResponses` detalhando `200` (PDF), `400`, `404`  
  - Documentação de cache (`@Cacheable`) e rate limiter (`@RateLimiter`)  
  - Mensagens de erro exemplificadas via `@ExampleObject`  

- **UserController**  
  - Endpoints CRUD e upload com `@Operation` e `@ApiResponses` completas  
  - Parâmetros documentados com `@Parameter`  
  - Exemplos de respostas de erro (`400`, `403`, `404`) em cada método   

---

## ✅ Resultado
- **Documentação Swagger** consistente e rica, pronta para UI de API  
- **Nenhuma alteração** na lógica existente—somente adições formais   
