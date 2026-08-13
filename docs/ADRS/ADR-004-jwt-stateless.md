# ADR-004: Autenticação JWT Stateless

**Status:** Aceito  
**Data:** 2026-05-07
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O sistema precisa de autenticação para proteger endpoints de gestão (CRUD de peças, serviços, ordens de serviço) enquanto mantém endpoints públicos para o cliente (consulta de OS e consulta de Status de OS).

## Decisão

Adotar **JWT (JSON Web Tokens)** com sessão stateless:

- **Algoritmo:** HMAC-SHA256
- **Biblioteca:** jjwt 0.12.6
- **Expiração:** 24 horas (86.400.000 ms)
- **Transporte:** Header `Authorization: Bearer <token>`
- **Armazenamento de usuários:** Tabela `usuarios` (username, password bcrypt, role)
- **Port de aplicação:** `br.com.oficina.application.port.out.TokenProviderPort`
- **Adapter JWT:** `br.com.oficina.adapters.out.security.JwtTokenProviderAdapter`

### Rotas públicas (sem autenticação)
- `POST /api/auth/login`
- `POST /api/auth/registro`
- `GET /api/ordens-servico/numero/{numero}/status` — Cliente consulta status da OS pelo número
- `GET /error` — Endpoint interno do Tomcat para tratamento de erros (**ver nota abaixo**)

Na implementação, login e registro são liberados pela regra `PathPatternRequestMatcher` para `/api/auth/**`.

### Rotas autenticadas com controle por perfil (RBAC)

A tabela `usuarios` armazena o campo `role` com um dos valores: `ATENDENTE`, `MECANICO` ou `GESTOR`. O token JWT inclui esse perfil como claim, e o `SecurityConfig` mapeia cada endpoint ao perfil autorizado via `PathPatternRequestMatcher`:

| Perfil | Endpoints autorizados |
|---|---|
| **ATENDENTE** | Criar OS, gerenciar orçamentos, registrar pagamento, entregar veículo, clientes, veículos, **listar OS por cliente** |
| **MECANICO** | Iniciar diagnóstico, adicionar itens (peças/serviços), finalizar serviço |
| **GESTOR** | Tudo acima + listar/filtrar/métricas de OS + CRUD de peças e serviços |

A validação do perfil ocorre em dois níveis:
1. **`SecurityConfig`** — `requestMatchers` com `PathPatternRequestMatcher.withDefaults()` (principal)
2. **`@PreAuthorize`** nos controllers — ativado por `@EnableMethodSecurity` (secundário)

O endpoint `POST /api/auth/registro` valida o campo `role` em dois níveis:
1. **`@NotBlank` (Bean Validation via `@Valid`)** — rejeita `role` nulo ou vazio com HTTP 400 antes de qualquer lógica de negócio.
2. **`Usuario.criar(..., role)` + `PerfilUsuario.from(role)`** — rejeita valores desconhecidos (ex: `ADMIN`, `SUPERUSER`) ao construir o modelo de domínio.

### Componentes de implementação

| Componente | Papel |
|---|---|
| `TokenProviderPort` | Contrato de aplicação para gerar, validar e extrair claims do token |
| `JwtTokenProviderAdapter` | Adapter de saída que usa JJWT para assinar e validar tokens |
| `JwtAuthenticationFilter` | Filtro `OncePerRequestFilter` que lê o Bearer token, valida o JWT e popula o `SecurityContext` |
| `SecurityConfig` | Define política stateless, rotas públicas, RBAC por endpoint e handlers 401/403 com JSON sanitizado |
| `SanitizedErrorController` | Substitui o erro padrão do Spring Boot em `/error`, preservando status HTTP e removendo campos sensíveis do payload |
| `PasswordHasherPort` / `PasswordHasherAdapter` | Contrato e adapter BCrypt para hash de senha |

## Alternativas Consideradas

| Opção | Motivo da rejeição |
|-------|-------------------|
| Session-based (cookie) | Stateful, não escala horizontalmente |
| OAuth2/OpenID Connect | Overengineering para o escopo do projeto |
| API Key | Menos seguro, sem expiração padrão |

## Consequências

### Positivas
- Stateless: não requer armazenamento de sessão no servidor
- Escalável horizontalmente (qualquer instância valida o token)
- Rotas públicas permitem interação do cliente sem login

### Negativas
- Token não pode ser revogado antes da expiração (sem blacklist)
- **Revogação de usuário no banco não invalida tokens ativos:** como o `JwtAuthenticationFilter` lê o perfil diretamente da claim do token (sem consultar o banco a cada request), um usuário removido ou com perfil alterado no banco continuará autenticado com o token anterior até ele expirar (máximo 24 h). Para ambientes de produção com requisito de revogação imediata, seria necessário introduzir uma blacklist de tokens (ex: Redis) ou reduzir o tempo de expiração.
- Secret deve ser gerenciada com segurança em produção (variável de ambiente)

---

## Nota de Implementação — Respostas de erro sanitizadas e o `/error` em `permitAll()`

### Problema
O `JwtAuthenticationFilter` estende `OncePerRequestFilter`. Em respostas de segurança e erro, o erro padrão do Spring Boot/Tomcat pode expor metadados como `path`, `error`, `exception`, `trace` ou `message`. Esses campos não são necessários para o consumidor da API e podem gerar alertas de divulgação de informações em varreduras OWASP/ZAP.

Também existe um cuidado específico com o dispatch interno para `/error`: se esse endpoint não estiver liberado, um erro originalmente `403` pode ser reprocessado como request anônimo e acabar sobrescrito por `401`.

### Solução
O `SecurityConfig` escreve diretamente respostas JSON para `401` e `403`, sem `response.sendError`, usando o contrato sanitizado:

```json
{
  "timestamp": "2026-08-13T09:29:26",
  "status": 403,
  "erro": "Acesso negado"
}
```

O endpoint `/error` continua explicitamente em `permitAll()` antes de qualquer outra regra:

```java
var paths = PathPatternRequestMatcher.withDefaults();

.requestMatchers(paths.matcher("/error")).permitAll()
```

O `SanitizedErrorController` trata o fallback `/error` e retorna somente `timestamp`, `status` e `erro`. Campos como `path`, `error`, `exception`, `trace` e `message` não são expostos ao cliente. Detalhes técnicos permanecem restritos aos logs internos.

