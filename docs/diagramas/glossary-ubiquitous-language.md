# Glossário da Linguagem Ubíqua — Sistema Oficina Mecânica

## Atores

| Termo | Definição | Perfil (RBAC) |
|-------|-----------|---------------|
| **Cliente** | Pessoa física (CPF) ou jurídica (CNPJ) que solicita serviços na oficina. Pode consultar status da OS publicamente sem login. | Público (sem token) |
| **Atendente** | Profissional que recebe o cliente, cadastra clientes e veículos, cria ordens de serviço, gera e gerencia orçamentos, registra pagamentos e entrega o veículo. | `ATENDENTE` |
| **Mecânico** | Profissional que realiza diagnóstico técnico, adiciona peças/serviços à OS e finaliza a execução. | `MECANICO` |
| **Gestor** | Responsável por todas as operações do sistema: inclui tudo que Atendente e Mecânico fazem, mais CRUD de peças/serviços e KPIs (listagem, filtros, métricas). | `GESTOR` |

---

## Entidades (Aggregates)

| Termo | Tipo | Definição | Bounded Context |
|-------|------|-----------|-----------------|
| **Cliente** | Aggregate Root | Identificado por CPF/CNPJ. Deve ser identificado antes de criar uma OS. Pode possuir vários veículos. Não pode ser excluído enquanto possuir veículo vinculado ao sistema, nem se possuir Ordem de Serviço vinculada. | Atendimento |
| **Veículo** | Aggregate Root | Identificado por Placa (formato Mercosul). Vinculado a um único Cliente. Possui marca, modelo e ano. Não pode ser excluído enquanto possuir Ordem de Serviço vinculada. | Atendimento |
| **Ordem de Serviço (OS)** | Aggregate Root | Documento central do sistema. Registra todo o ciclo de atendimento desde a recepção até o encerramento. Contém itens (peças e serviços). | Ordem de Serviço |
| **Item da OS** | Entity | Linha da OS representando um serviço ou peça aplicado, com quantidade e valor unitário. Referencia o catálogo via `referenciaId`. | Ordem de Serviço |
| **Peça** | Aggregate Root | Material físico com controle de estoque (quantidade disponível e estoque mínimo configurável por peça). Permite baixa, reposição e alerta de estoque baixo. | Estoque |
| **Serviço** | Aggregate Root | Tipo de trabalho oferecido pela oficina com valor tabelado e tempo estimado em minutos. | Catálogo de Serviços |
| **Orçamento** | Aggregate Root | Proposta de valor gerada automaticamente a partir dos itens da OS. Enviada ao cliente para aprovação. Possui validade. | Orçamento |
| **Execução** | Aggregate Root | Fase em que os serviços aprovados são realizados no veículo. Contém diagnóstico e período de execução. | Execução |
| **Diagnóstico** | Entity | Avaliação técnica feita pelo mecânico. Pode identificar problemas adicionais que geram novo orçamento. | Execução |
| **Pagamento** | Aggregate Root | Registro financeiro vinculado à OS. Pode ser aprovado ou recusado pelo Gateway de Pagamento. Suporta 5 métodos. Armazena `transactionId` e `gatewayMensagem` retornados pelo provedor. | Financeiro |
| **Gateway de Pagamento** | Port (DDD) | Interface no domínio (`PagamentoGateway`) que abstrai o provedor externo de autorização de cobranças. Implementação padrão é `MockPagamentoGateway` (taxa de aprovação e latência configuráveis). Substituível por Stripe, Mercado Pago, etc. | Financeiro |
| **Entrega** | Aggregate Root | Liberação e devolução do veículo ao cliente após pagamento aprovado. | Entrega |
| **Encerramento** | Aggregate Root | Fechamento definitivo da OS após a entrega do veículo. | Encerramento |

---

## Value Objects

| Termo | Definição | Regra de Validação |
|-------|-----------|-------------------|
| **CPF/CNPJ** | Documento de identificação do cliente. | Validado algoritmicamente (dígitos verificadores). Classificado em tipo CPF ou CNPJ. |
| **Placa** | Identificador do veículo no formato Mercosul. | Regex: 3 letras + 1 dígito + 1 letra + 2 dígitos (ABC1D23). |
| **Quantidade** | Número de unidades em estoque. Imutável. | Deve ser >= 0. Operações retornam nova instância. `subtrair` exige disponibilidade. |
| **Período de Execução** | Intervalo de tempo (início e fim) da execução do serviço. | Início obrigatório. Fim preenchido ao finalizar. Calcula duração. |

---

## Status (Ciclo de Vida)

### Status da Ordem de Serviço

| Status | Significado | Transição |
|--------|-------------|-----------|
| `RECEBIDA` | OS criada, veículo recepcionado | → EM_DIAGNOSTICO |
| `EM_DIAGNOSTICO` | Mecânico avaliando o veículo | → AGUARDANDO_APROVACAO |
| `AGUARDANDO_APROVACAO` | Orçamento gerado, aguardando cliente | → EM_EXECUCAO / CANCELADA |
| `EM_EXECUCAO` | Serviços sendo realizados | → FINALIZADA |
| `FINALIZADA` | Serviços concluídos, aguarda pagamento | → AGUARDANDO_RETIRADA |
| `AGUARDANDO_RETIRADA` | Pagamento aprovado; veículo pronto, aguardando retirada pelo cliente (pode ocorrer em outro dia) | → ENTREGUE |
| `ENTREGUE` | Veículo devolvido ao cliente | (estado final) |
| `CANCELADA` | OS cancelada (orçamento rejeitado) | (estado final) |

### Status do Orçamento

| Status | Significado |
|--------|-------------|
| `PENDENTE` | Recém-criado, não enviado |
| `ENVIADO` | Disponibilizado ao cliente |
| `APROVADO` | Cliente aceitou — dispara execução e baixa no estoque |
| `REJEITADO` | Cliente recusou — OS é cancelada |
| `EXPIRADO` | Prazo de validade venceu |

### Status da Execução

| Status | Significado |
|--------|-------------|
| `AGUARDANDO` | Criada, aguardando aprovação do orçamento |
| `EM_DIAGNOSTICO` | Mecânico realizando diagnóstico |
| `EM_ANDAMENTO` | Serviço em execução |
| `SERVICO_FINALIZADO` | Trabalho concluído |
| `CANCELADA` | Execução cancelada |

### Status do Diagnóstico

| Status | Significado |
|--------|-------------|
| `PENDENTE` | Aguardando início |
| `EM_ANDAMENTO` | Mecânico investigando |
| `CONCLUIDO` | Avaliação finalizada |

### Status do Pagamento

| Status | Significado |
|--------|-------------|
| `PENDENTE` | Aguardando processamento |
| `APROVADO` | Pagamento confirmado — libera entrega |
| `RECUSADO` | Pagamento negado — nova tentativa possível |

### Status da Entrega

| Status | Significado |
|--------|-------------|
| `AGUARDANDO_LIBERACAO` | Pagamento não confirmado |
| `VEICULO_LIBERADO` | Autorizado para retirada |
| `VEICULO_ENTREGUE` | Devolvido ao cliente |
| `VEICULO_EM_PATIO` | Cliente não retirou (em pátio) — *modelado no domínio mas não orquestrado pelo fluxo atual; ponto de extensão futuro* |

### Status do Encerramento

| Status | Significado |
|--------|-------------|
| `PENDENTE` | Aguardando entrega |
| `ENCERRADA` | OS formalmente fechada |

---

## Métodos de Pagamento

| Método | Descrição |
|--------|-----------|
| `DINHEIRO` | Pagamento em espécie |
| `CARTAO_CREDITO` | Cartão de crédito |
| `CARTAO_DEBITO` | Cartão de débito |
| `PIX` | Transferência instantânea |
| `BOLETO` | Boleto bancário |

---

## Tipos de Item

| Tipo | Descrição |
|------|-----------|
| `PECA` | Item físico do estoque (baixa automática ao aprovar orçamento) |
| `SERVICO` | Mão de obra do catálogo de serviços |

---

## Comandos (Ações do Sistema)

| Comando | Ator | Descrição | Endpoint |
|---------|------|-----------|----------|
| Identificar Cliente | Atendente | Cadastrar/localizar cliente por CPF/CNPJ com endereço via CEP | `POST /api/clientes` |
| Excluir Cliente | Atendente | Remover cliente sem veículos vinculados | `DELETE /api/clientes/{id}` |
| Cadastrar Veículo | Atendente | Registrar veículo com placa Mercosul | `POST /api/veiculos` |
| Criar OS | Atendente | Abrir OS com itens (peças + serviços) | `POST /api/ordens-servico` |
| Iniciar Diagnóstico | Mecânico | Mecânico inicia avaliação técnica do veículo → OS: EM_DIAGNOSTICO. O nome do mecânico autenticado é registrado em `execucoes.mecanico_nome` e exposto em `OrdemServicoResponse.mecanicoNome` e no tracking público. | `PATCH /api/ordens-servico/{id}/iniciar-diagnostico` |
| Gerar Orçamento | Atendente | Calcular valor total a partir dos itens da OS | `POST /api/ordens-servico/{id}/orcamento` |
| Aprovar Orçamento | Atendente | Registrar aprovação do cliente → baixa estoque + OS: EM_EXECUCAO | `PATCH /api/ordens-servico/{id}/aprovar` (autenticado) |
| Rejeitar Orçamento | Atendente | Registrar rejeição do cliente → OS: CANCELADA | `PATCH /api/ordens-servico/{id}/rejeitar` (autenticado) |
| Gerar Novo Orçamento | Atendente | Novo problema identificado durante execução → novo orçamento; OS retorna para AGUARDANDO_APROVACAO. O mecânico comunica o problema ao atendente, que chama o endpoint. | `POST /api/ordens-servico/{id}/orcamento` (a partir de EM_EXECUCAO) |
| Finalizar Serviço | Mecânico | Marcar execução como concluída → OS: FINALIZADA | `PATCH /api/ordens-servico/{id}/finalizar` |
| Adicionar Itens à OS | Mecânico / Gestor | Adicionar peças e serviços a uma OS nos status RECEBIDA, EM_DIAGNOSTICO ou EM_EXECUCAO | `POST /api/ordens-servico/{id}/itens` |
| Registrar Pagamento | Atendente | Processar pagamento via gateway → OS: AGUARDANDO_RETIRADA | `POST /api/ordens-servico/{id}/pagamento` |
| Entregar Veículo | Atendente | Cliente comparece e retira veículo → OS: ENTREGUE; Encerramento criado automaticamente | `PATCH /api/ordens-servico/{id}/entregar` |
| Cadastrar Peça | Gestor | Adicionar peça ao catálogo/estoque | `POST /api/pecas` |
| Repor Estoque | Gestor | Adicionar quantidade à peça existente | `PATCH /api/pecas/{id}/repor-estoque` |
| Cadastrar Serviço | Gestor | Adicionar serviço ao catálogo | `POST /api/servicos` |

---

## Eventos de Domínio

| Evento | Trigger | Efeito |
|--------|---------|--------|
| `ClienteIdentificado` | Cadastro/localização de cliente | Cliente disponível para vinculação com OS |
| `ClienteExcluido` | Exclusão de cliente (sem veículos vinculados) | Cliente removido do sistema |
| `VeiculoCadastrado` | Cadastro de veículo | Veículo disponível para OS |
| `OrdemDeServicoCriada` | Criação da OS | Itens vinculados, status RECEBIDA |
| `ItensAdicionadosAOS` | Adição de peças/serviços à OS | Itens registrados, valor total atualizado |
| `OSEncaminhadaAOMecanico` | OS em RECEBIDA encaminhada | Mecânico ciente do trabalho a realizar |
| `DiagnosticoIniciado` | Mecânico inicia avaliação | OS avança para EM_DIAGNOSTICO; Execucao avança para EM_DIAGNOSTICO |
| `OrcamentoGerado` | Geração do orçamento | Valor calculado, OS aguarda aprovação |
| `OrcamentoEnviado` | Orçamento disponibilizado ao cliente | Cliente pode aprovar ou rejeitar |
| `OrcamentoAprovado` | Cliente aprova | Baixa no estoque; Execucao avança para EM_ANDAMENTO |
| `OrcamentoRejeitado` | Cliente rejeita | OS cancelada |
| `OrdemDeServicoCancelada` | Rejeição do orçamento | OS entra em status terminal CANCELADA |
| `ServicoIniciado` | OS entra em EM_EXECUCAO | Mecânico executa os serviços aprovados |
| `NovoProblemaIdentificado` | Mecânico encontra problema adicional durante execução | Gera novo orçamento; OS retorna para AGUARDANDO_APROVACAO |
| `NovoOrcamentoGerado` | Novo problema identificado | Orçamento complementar criado para aprovação |
| `ServicoFinalizado` | Mecânico finaliza todos os itens | OS marcada como FINALIZADA; Execucao como SERVICO_FINALIZADO |
| `PagamentoIniciado` | Atendente registra método de pagamento | Gateway externo é acionado para processar |
| `PagamentoAprovado` | Gateway aprova | OS avança para AGUARDANDO_RETIRADA; cliente pode buscar veículo |
| `PagamentoRecusado` | Gateway recusa | OS permanece FINALIZADA; nova tentativa possível |
| `VeiculoEntregue` | Cliente busca o veículo na oficina | OS avança para ENTREGUE; Encerramento criado |
| `OrdemDeServicoEncerrada` | Entrega do veículo | OS entra em estado terminal ENTREGUE |
| `EstoqueBaixado` | Aprovação do orçamento | Quantidade decrementada para cada peça nova da OS |
| `EstoqueReposto` | Reposição manual | Quantidade incrementada |

---

## Regras de Negócio

| # | Regra | Implementação |
|---|-------|---------------|
| RN01 | CPF/CNPJ deve ser válido algoritmicamente | Construtor `new CpfCnpj(valor)` lança `IllegalArgumentException` se inválido (dígitos verificadores CPF/CNPJ) |
| RN02 | Placa deve seguir formato Mercosul | Construtor `new Placa(valor)` lança `IllegalArgumentException` se não bater com regex `^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$` (ex: ABC1D23) |
| RN03 | Quantidade em estoque nunca pode ser negativa | `Quantidade(valor >= 0)` |
| RN04 | Baixa de estoque só permitida com disponibilidade | `Peca.verificarDisponibilidade()` → `IllegalStateException` |
| RN05 | Orçamento só pode ser aprovado se não estiver expirado | `Orcamento.estaExpirado()` |
| RN06 | Status da OS segue máquina de estados (transições definidas) | Métodos `avancarParaDiagnostico()`, `aguardarAprovacao()`, `aprovarEIniciarExecucao()`, `finalizar()`, `aguardarRetirada()`, `entregar()`, `cancelar()` chamam `validarTransicao()` internamente → `IllegalStateException` se transição inválida |
| RN07 | Pagamento recusado pelo gateway permite nova tentativa (OS permanece em FINALIZADA) | `OrdemDeServicoService.registrarPagamento` + `MockPagamentoGateway` |
| RN08 | Valor unitário do item é resolvido automaticamente do catálogo | `OrdemDeServicoService` resolve via referenciaId |
| RN09 | Cliente não pode ser excluído se possuir veículos ou ordens de serviço vinculadas | `ClienteService.excluir()` → `NegocioException` (HTTP 422) |
| RN10 | Cada peça possui estoque mínimo configurável; estoque é considerado baixo quando `quantidadeEstoque <= estoqueMinimo` | `Peca.estaComEstoqueBaixo()` |
