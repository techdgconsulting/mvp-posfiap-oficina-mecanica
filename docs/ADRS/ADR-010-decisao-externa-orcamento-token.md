# ADR-010: Decisão Externa de Orçamento por Token

**Status:** Aceito  
**Data:** 2026-08-07  
**Autor:** Diego Gonzalez

## Contexto

O fluxo interno de orçamento já permitia registrar aprovação ou rejeição por usuários autenticados (`ATENDENTE` ou `GESTOR`) nos endpoints `PATCH /api/ordens-servico/{id}/aprovar` e `PATCH /api/ordens-servico/{id}/rejeitar`.

Entretanto, o requisito de aprovação de orçamento cita recebimento de notificações externas de aprovação ou recusa do cliente. Expor os endpoints internos sem autenticação não seria adequado, pois IDs internos de OS não devem funcionar como fator de autorização.

## Decisão

Criar um fluxo externo complementar baseado em token opaco, expiração e uso único:

- `POST /api/ordens-servico/{id}/orcamento/notificar-cliente`: endpoint interno, autenticado para `ATENDENTE` e `GESTOR`, que gera a solicitação de decisão e envia a notificação ao e-mail cadastrado.
- `POST /api/orcamentos/decisoes-cliente/{token}/aprovar`: endpoint público de aprovação por token.
- `POST /api/orcamentos/decisoes-cliente/{token}/recusar`: endpoint público de recusa por token.

O token em texto claro aparece apenas nos links enviados ao cliente. A aplicação persiste somente o hash SHA-256 do token na tabela `orcamento_decisao_cliente`, criada por Flyway. A validade padrão é configurável por `oficina.notificacao.orcamento.expiracao-horas`, com valor padrão de 48 horas.

O envio de e-mail usa a porta `EmailNotificacaoPort`. Em ambiente local, o modo padrão é `LOG`, registrando destinatário, assunto e links sem envio real. Quando `OFICINA_EMAIL_MODE=SMTP`, a mesma porta é implementada por `SmtpEmailNotificacaoAdapter`, permitindo envio real via Spring Mail sem alterar o caso de uso.

## Consequências

### Positivas

- Atende ao requisito de decisão externa sem expor endpoints internos nem exigir JWT do cliente.
- Mantém a regra de aprovação/rejeição em um único ponto, delegando para os fluxos internos já existentes.
- Evita armazenar token em texto puro.
- Permite evolução para AWS SES, API Gateway ou frontend de confirmação sem alterar o núcleo de aplicação.
- Mantém o desenho Clean Architecture: controller chama input port, use case usa output ports, adapters implementam detalhes técnicos.

### Negativas

- O modo `LOG` não envia mensagem real ao cliente; envio real depende da configuração SMTP correta.
- Botões de e-mail normalmente abrem links GET, enquanto a API define POST como contrato correto para alteração de estado.
- A operação requer armazenamento adicional e controle de expiração/uso único.

## Alternativas Consideradas

| Alternativa | Motivo de não adoção |
|-------------|----------------------|
| Tornar `PATCH /api/ordens-servico/{id}/aprovar` público | Exporia decisão sensível baseada em ID interno, sem autorização adequada |
| Usar GET direto para aprovar/recusar | Mais simples para e-mail, mas altera estado por método HTTP de leitura |
| Envio SMTP/SES obrigatório | Aumentaria configuração, dependência externa e complexidade local |
| Assinatura HMAC por payload externo | Adequada para webhooks de terceiros, mas mais complexa que o necessário para link único de cliente |

## Evolução Futura

- Criar página de confirmação que recebe o token via GET e confirma a decisão via POST.
- Implementar `EmailNotificacaoPort` com AWS SES na infraestrutura AWS.
- Registrar metadados de auditoria, como IP, user-agent e quantidade de tentativas inválidas.
