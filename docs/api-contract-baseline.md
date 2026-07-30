# API Contract Baseline

**Status:** baseline de compatibilidade  
**Fonte de verdade:** [`postman/OficinaMecanicaDGCar - Suite Completa.postman_collection.json`](../postman/OficinaMecanicaDGCar%20%E2%80%94%20Suite%20Completa.postman_collection.json)  
**Objetivo:** preservar o comportamento publico da API durante a reestruturacao para Clean Architecture.

## Regra De Compatibilidade

A mudanca de arquitetura interna nao deve alterar o contrato observado pelo consumidor da API.

Isso inclui:

- mesmos paths HTTP;
- mesmos metodos HTTP;
- mesmos payloads aceitos;
- mesmos campos nas respostas;
- mesmos status codes esperados;
- mesma estrutura de erro, especialmente o campo `erro`;
- mensagens de erro relevantes quando a collection valida conteudo textual;
- mesmas regras de autorizacao RBAC;
- mesmo fluxo funcional ponta a ponta da Ordem de Servico.

Se alguma mudanca de contrato for necessaria no futuro, ela deve ser tratada como mudanca explicita de API, com versao, documentacao e ajuste coordenado da collection.

## Criterio De Aceite Da Fase

A collection Postman deve ser executada contra o ambiente Docker local sem regressao de contrato.

Baseline esperado:

- total de testes da collection: `351` (contagem local de chamadas `pm.test`, incluindo fila operacional e decisão externa de orçamento por token);
- falhas esperadas apos estabilizacao: `0`;
- skipped/errors esperados: `0`, salvo cenarios explicitamente marcados como opcionais.

O resultado observado antes desta baseline foi:

- passed: `300`;
- failed: `31`.

Essas 31 falhas devem ser analisadas como regressao potencial da aplicacao, nao como contrato novo.

## Fluxos Que Devem Permanecer Compativeis

| Area | Contrato esperado |
|---|---|
| Autenticacao | `POST /api/auth/login` retorna token JWT valido para os perfis da collection. |
| Clientes | Criacao, busca por ID, busca por documento e listagem retornam os mesmos status e campos. |
| Veiculos | Criacao, busca, listagem geral e listagem por cliente preservam payload e resposta. |
| Servicos | Criacao, busca e listagem preservam payload e resposta. |
| Pecas | Criacao, busca, listagem, estoque baixo e operacoes de estoque preservam payload e resposta. |
| Ordens de Servico | Criacao, busca por ID, busca por numero, listagem geral, fila operacional, listagem por cliente e status preservam contrato. |
| Orcamentos | Geracao, notificacao ao cliente, aprovacao/recusa interna e decisao externa por token preservam status codes e payloads esperados. |
| Transicoes de OS | Diagnostico, itens, orcamento, aprovacao, rejeicao, finalizacao, pagamento e entrega preservam status codes e mensagens esperadas. |
| RBAC | Perfis `GESTOR`, `ATENDENTE`, `MECANICO` e `CLIENTE` mantem os mesmos acessos permitidos e negados. |
| KPIs | Endpoints de metricas mantem payload, formato e permissoes. |

## Pontos De Regressao Observados

Esta lista registra sintomas para orientar as proximas fases. Ela nao redefine o contrato.

| Sintoma | Contrato esperado | Resultado observado | Hipotese inicial |
|---|---|---|---|
| `GET /api/clientes` | `200` com array | `400 CPF invalido` | Validacao de CPF/CNPJ em leitura de dados seedados. |
| `GET /api/veiculos` | `200` com array | `400 CPF invalido` | Listagem depende de cliente/documento ao montar resposta. |
| `GET /api/ordens-servico` | `200` com array | `400 CPF invalido` | Mapper/use case busca cliente e revalida documento. |
| `GET /api/ordens-servico/status/ENTREGUE` | `200` com array | `400 CPF invalido` | Mesma causa da listagem de OS. |
| `GET /api/ordens-servico/fila` | `200` com array filtrado e ordenado | Novo contrato validado na collection | Retorna apenas OS ativas: `EM_EXECUCAO`, `AGUARDANDO_APROVACAO`, `EM_DIAGNOSTICO`, `RECEBIDA`. |
| `POST /api/ordens-servico/{id}/orcamento/notificar-cliente` | `200` com links de aprovacao/recusa | Novo contrato validado na collection | Gera token opaco, persiste hash e envia e-mail pelo adapter configurado (`LOG` ou `SMTP`). |
| `POST /api/orcamentos/decisoes-cliente/{token}/aprovar` | `200` com decisao `APROVADA` | Novo contrato validado na collection | Endpoint publico sem JWT, protegido por token opaco valido. |
| `POST /api/orcamentos/decisoes-cliente/{token}/recusar` | `200` com decisao `RECUSADA` | Novo contrato validado na collection | Endpoint publico sem JWT, protegido por token opaco valido. |
| Transicoes invalidas de OS | `422` em regra de negocio | `409` em alguns cenarios | `IllegalStateException` mapeada para `409`. |
| Mensagem de transicao invalida | Texto esperado pela collection | Texto sem acento ou variacao | Mensagem do dominio divergiu do contrato validado. |
| Fluxo completo de OS | Criacao `201` e proximos passos `200` | Criacao `400`, depois varios `404` | Falha inicial impede populacao de variaveis da collection. |

## Acoes Fora Do Escopo Desta Fase

Esta fase nao altera:

- controllers;
- use cases;
- dominio;
- mappers;
- migrations;
- dados seedados;
- testes unitarios ou de cobertura.

As correcoes devem ser feitas nas proximas fases, uma causa raiz por vez, sempre validando novamente a collection.
