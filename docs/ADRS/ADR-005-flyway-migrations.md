# ADR-005: Flyway para Gerenciamento de Migrations

**Status:** Aceito  
**Data:** 2026-05-07
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O schema do banco de dados precisa ser versionado e evoluir de forma controlada, sem depender de `ddl-auto=create` do Hibernate que poderia causar perda de dados.

## Decisão

Adotar **Flyway** para migrations de banco de dados:

- **Modo:** `spring.jpa.hibernate.ddl-auto=validate` (apenas valida, não altera schema)
- **Localização:** `classpath:db/migration`
- **Convenção:** `V{n}__{descricao}.sql`
- **Execução:** Automática ao iniciar a aplicação nos perfis com PostgreSQL

### Escopo por perfil

| Perfil | Banco | Flyway | Hibernate DDL | Uso |
|--------|-------|--------|---------------|-----|
| Padrão | PostgreSQL | Habilitado | `validate` | Execução local com PostgreSQL externo |
| Docker Compose | PostgreSQL 16 | Habilitado | `validate` | Ambiente recomendado para avaliação e demonstração |
| `dev` | H2 em memória | Desabilitado | `create-drop` | Execução local rápida sem Docker |
| `test` | H2 em memória | Desabilitado | `create-drop` | Testes automatizados rápidos e isolados |

Assim, o Flyway é a fonte de verdade para o schema PostgreSQL usado no ambiente principal e no Docker. Os perfis `dev` e `test` usam H2 com schema recriado pelo Hibernate para reduzir atrito e tempo de execução, com a limitação conhecida de não validar as migrations nesses perfis.

### Migrations existentes
| Versão | Arquivo | Descrição |
|--------|---------|-----------|
| V1 | `V1__criar_tabelas.sql` | Criação das 10 tabelas principais: `clientes`, `veiculos`, `servicos`, `pecas`, `ordens_servico`, `itens_os`, `diagnosticos`, `execucoes`, `orcamentos`, `pagamentos` |
| V2 | `V2__criar_tabela_usuarios.sql` | Tabela `usuarios` para autenticação JWT (login, senha, role) |
| V3 | `V3__adicionar_endereco_clientes.sql` | Campos de endereço em `clientes` (`cep`, `logradouro`, `bairro`, `cidade`, `uf`) preenchidos via ViaCEP |
| V4 | `V4__criar_tabelas_entrega_encerramento.sql` | Tabelas `entregas` e `encerramentos` (BCs do ciclo final da OS) |
| V5 | `V5__adicionar_estoque_minimo_pecas.sql` | Campo `estoque_minimo INTEGER DEFAULT 5` em `pecas` — limite configurável por peça para alertas de estoque baixo |
| V6 | `V6__inserir_massa_de_dados.sql` | Massa de dados: 8 cenários completos (cliente + veículo + OS + itens + orçamento + execução + pagamento + entrega + encerramento) |
| V7 | `V7__adicionar_gateway_pagamento.sql` | Campos `transaction_id VARCHAR(60)` e `gateway_mensagem VARCHAR(255)` em `pagamentos` — rastreabilidade da resposta do gateway externo |
| V8 | `V8__adicionar_numero_os.sql` | Campo `numero VARCHAR(20)` em `ordens_servico` (formato `OS-AAAA-NNNNN`) + UPDATE para registros existentes |
| V9 | `V9__numero_os_nullable.sql` | Mantém `numero` como nullable — necessário pelo fluxo de dois passos: INSERT sem número → obter ID → UPDATE com `OS-{ano}-{id}` |
| V10 | `V10__adicionar_estoque_reduzido_itens_os.sql` | Campo `estoque_reduzido BOOLEAN DEFAULT false` em `itens_os` — evita dupla baixa de estoque no fluxo de "novo problema" identificado durante `EM_EXECUCAO` |
| V11 | `V11__adicionar_campos_kpi.sql` | `data_aprovacao TIMESTAMP` em `orcamentos` + `mecanico_nome VARCHAR(150)` em `execucoes` — dados para KPIs e rastreabilidade do serviço executado |
| V12 | `V12__adicionar_atendente_os.sql` | `atendente_nome VARCHAR(150)` em `ordens_servico` — registra o usuário autenticado (JWT) que abriu a OS, completando a rastreabilidade atendente ↔ mecânico |
| V13 | `V13__inserir_usuarios_perfis.sql` | Insere três usuários de demonstração, um por perfil: `atendente1`, `mecanico1`, `gestor1` — todos com senha `senha123` (BCrypt). Permite testar o RBAC sem criar usuários via `/api/auth/registro` |
| V14 | `V14__corrigir_status_execucao.sql` | Corrige inconsistência de dados: valor `'DIAGNOSTICO'` renomeado para `'EM_DIAGNOSTICO'` no enum `StatusExecucao` — alinha registros existentes com o enum atualizado |
| V15 | `V15__corrigir_documentos_clientes_seed.sql` | Corrige documentos CPF/CNPJ da massa seed de clientes para manter dados de demonstração válidos e consistentes com as validações de domínio |
| V16 | `V16__criar_orcamento_decisao_cliente.sql` | Cria tabela `orcamento_decisao_cliente` para decisão externa de orçamento por token opaco, com hash do token, expiração, status, e-mail de destino e vínculos com OS e orçamento |

## Consequências

### Positivas
- Schema versionado e rastreável (cada migration é um incremento)
- Reprodutibilidade: qualquer ambiente é construído na mesma sequência
- Validação de integridade (Hibernate valida que entidades batem com schema)
- Redução de schema drift entre ambiente Docker, PostgreSQL local e documentação
- Evita alterações automáticas destrutivas do Hibernate no banco principal

### Negativas
- Scripts SQL manuais (não auto-gerados pelo Hibernate)
- Necessário cuidado com ordem e dependências entre migrations
- Perfis `dev` e `test` não executam Flyway; divergências específicas de migration precisam ser capturadas no perfil PostgreSQL/Docker
