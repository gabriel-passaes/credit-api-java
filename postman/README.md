# 🚀 Credit API – Postman Collection

Este diretório contém tudo que você precisa para testar a Credit API usando o Postman:

- ✅ **Coleção Completa**: Endpoints de Auth, Credit, Invoice e User
- ✅ **Payloads de exemplo** prontos para cada requisição
- ✅ **Configuração de Authorization** automática (Bearer Token)
- ✅ **BaseUrl dinâmico** para rodar localmente ou em produção
- ✅ **Environment separado** para configuração rápida de variáveis

---

## 📂 Arquivos disponíveis

```bash
credit-api/
├── postman/
│   ├── README.md                    # <- Esse que acabamos de criar
│   ├── credit-api-collection.json    # <- Todos os endpoints organizados
│   └── environments/
│       └── credit-api-environment.json  # <- Variáveis de ambiente

# 📥 Como Usar

## 1. Importe o Environment

- No Postman, clique em **Environments** (ícone de engrenagem ⚙️ no canto superior direito).
- Clique em **Import**.
- Selecione o arquivo `postman/environments/credit-api-environment.json`.
- Salve.

---

## 2. Importe a Collection

- No Postman, clique em **Collections**.
- Clique em **Import**.
- Selecione o arquivo `postman/credit-api-collection.json`.
- Pronto! A coleção será carregada.

---

# ⚙️ Configuração das Variáveis

Após importar, selecione o Environment **Credit API Environment** e configure:

| Variável        | Valor                     | Observação                    |
|:----------------|:---------------------------|:-------------------------------|
| `baseUrl`        | `http://localhost:8080`     | URL da API local               |
| `accessToken`    | (Preencher após login)      | Copiar token JWT de login      |
| `refreshToken`   | (Preencher após login)      | Copiar refresh token de login  |

---

# 🔑 Como Autenticar

- Realize uma requisição `POST /auth/login` com um usuário válido.
- Copie o `accessToken` e o `refreshToken` retornados.
- Cole o `accessToken` e `refreshToken` nas variáveis do Environment.
- Agora todos os endpoints protegidos estarão autenticados automaticamente! ✅

---

# 📌 Observações

- **Endpoints públicos** (ex: login, register) não precisam de token.
- **Endpoints privados** usam `Authorization: Bearer {{accessToken}}` automático.
- Se o `accessToken` expirar, use `/auth/refresh` para obter novos tokens.
- Para logout geral, use `/auth/logout` ou `/auth/logout/all`.

---

# 🛠️ Dicas Rápidas

- ⚡ Crie usuários rapidamente pelo endpoint `/auth/register` ou `/user`.
- 🔄 Atualize tokens sem precisar logar de novo usando `/auth/refresh`.
- 🛡️ Faça logout corretamente para limpar sessões.

---

# ✨ Bora pra cima!

Coleção pronta para testar todos os fluxos: autenticação, gestão de créditos, emissão de notas fiscais e usuários — tudo seguindo o padrão da **Credit API**. 🚀🔥

