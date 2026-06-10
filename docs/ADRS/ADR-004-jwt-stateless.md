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

### Rotas públicas (sem autenticação)
- `POST /api/auth/login`
- `POST /api/auth/registro`
- `GET /api/ordens-servico/numero/{numero}/status` — Cliente consulta status da OS pelo número
- `GET /error` — Endpoint interno do Tomcat para tratamento de erros (**ver nota abaixo**)

### Rotas autenticadas com controle por perfil (RBAC)

A tabela `usuarios` armazena o campo `role` com um dos valores: `ATENDENTE`, `MECANICO` ou `GESTOR`. O token JWT inclui esse perfil como claim, e o `SecurityConfig` mapeia cada endpoint ao perfil autorizado via `AntPathRequestMatcher`:

| Perfil | Endpoints autorizados |
|---|---|
| **ATENDENTE** | Criar OS, gerenciar orçamentos, registrar pagamento, entregar veículo, clientes, veículos, **listar OS por cliente** |
| **MECANICO** | Iniciar diagnóstico, adicionar itens (peças/serviços), finalizar serviço |
| **GESTOR** | Tudo acima + listar/filtrar/métricas de OS + CRUD de peças e serviços |

A validação do perfil ocorre em dois níveis:
1. **`SecurityConfig`** — `requestMatchers` com `AntPathRequestMatcher.antMatcher()` (principal)
2. **`@PreAuthorize`** nos controllers — ativado por `@EnableMethodSecurity` (secundário)

O endpoint `POST /api/auth/registro` valida o campo `role` em dois níveis:
1. **`@NotBlank` (Bean Validation via `@Valid`)** — rejeita `role` nulo ou vazio com HTTP 400 antes de qualquer lógica de negócio.
2. **`PerfilUsuario.isValido(role)`** — rejeita valores desconhecidos (ex: `ADMIN`, `SUPERUSER`) com HTTP 400.

## Alternativas Consideradas

| Opção | Motivo da rejeição |
|-------|-------------------|
 Session-based (cookie) ->  Stateful, não escala horizontalmente
 OAuth2/OpenID Connect -> Overengineering para o escopo do projeto
 API Key -> Menos seguro, sem expiração padrão

## Consequências

### Positivas
- Stateless: não requer armazenamento de sessão no servidor
- Escalável horizontalmente (qualquer instância valida o token)
- Rotas públicas permitem interação do cliente sem login

### Negativas
- Token não pode ser revogado antes da expiração (sem blacklist)+
- **Revogação de usuário no banco não invalida tokens ativos:** como o `JwtAuthenticationFilter` lê o perfil diretamente da claim do token (sem consultar o banco a cada request), um usuário removido ou com perfil alterado no banco continuará autenticado com o token anterior até ele expirar (máximo 24 h). Para ambientes de produção com requisito de revogação imediata, seria necessário introduzir uma blacklist de tokens (ex: Redis) ou reduzir o tempo de expiração.
- Secret deve ser gerenciada com segurança em produção (variável de ambiente)

---

## Nota de Implementação — Tomcat Error Dispatch e o `/error` em `permitAll()`

### Problema
O `JwtAuthenticationFilter` estende `OncePerRequestFilter`. Ao processar um request normal (ex: `GET /api/pecas` com perfil insuficiente), o filtro JWT executa e autentica o usuário. O `AccessDeniedHandler` do Spring Security então chama `response.sendError(403)`, que faz o **Tomcat abrir um segundo dispatch interno** para `GET /error` (tipo `ERROR`).

Nesse segundo dispatch, o `OncePerRequestFilter` detecta que o request já foi filtrado (via atributo interno `FILTERED`) e **pula a execução do filtro JWT**, resultando em `SecurityContext` vazio (usuário anônimo). Se `/error` não estiver em `permitAll()`, a regra `anyRequest().authenticated()` rejeita o anônimo com um novo `sendError(401)` — e o cliente recebe **401 em vez de 403**.

### Solução
O `SecurityConfig` declara `/error` explicitamente em `permitAll()` antes de qualquer outra regra:

```java
.requestMatchers(AntPathRequestMatcher.antMatcher("/error")).permitAll()
```

Isso garante que o dispatch de erro do Tomcat seja processado sem autenticação, preservando o status HTTP original (403, 404, etc.) definido pelo handler.

