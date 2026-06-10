# ADR-007: Gateway de Pagamento via Port/Adapter (Mock)

**Status:** Aceito  
**Data:** 2026-05-18
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O fluxo de Ordem de Serviço termina com o registro de pagamento (`POST /api/ordens-servico/{id}/pagamento`). Em um cenário real, esse registro depende de um **provedor externo de pagamento** (Stripe, Mercado Pago, PagSeguro, Cielo, etc.) que autoriza ou recusa a cobrança.

Era necessário decidir como representar essa integração no projeto acadêmico, sem introduzir dependências externas (chaves de API, SDKs proprietários) nem flakiness em testes, mas mantendo a possibilidade de plugar um provedor real no futuro.

## Decisão

Adotar o padrão **port/adapter (hexagonal)** para a integração de pagamento, com uma **implementação mock** como adapter padrão:

- **Port (domínio):** `br.com.oficina.domain.financeiro.PagamentoGateway`
  - Interface neutra a tecnologia
  - Tipos auxiliares `GatewayRequest(ordemServicoId, valor, metodo)` e `GatewayResponse(aprovado, transactionId, mensagem)` como `record` no mesmo arquivo
- **Adapter mock (infra):** `br.com.oficina.infrastructure.gateway.MockPagamentoGateway`
  - Taxa de aprovação configurável (`oficina.pagamento.gateway.approval-rate`, default 0.9)
  - Latência simulada (`oficina.pagamento.gateway.latency-ms`, default 100)
  - `transactionId` no formato `MOCK-<uuid>`
- **Persistência:** colunas `transaction_id` e `gateway_mensagem` na tabela `pagamentos` (migration `V7`)
- **Comportamento na aprovação:** persiste `Pagamento` com status `APROVADO` e avança a OS para **`AGUARDANDO_RETIRADA`** — a entrega física do veículo e o encerramento são registrados em passo separado (`PATCH /api/ordens-servico/{id}/entregar`)
- **Comportamento na recusa:** persiste `Pagamento` com status `RECUSADO`, lança `NegocioException` (HTTP 422), e **mantém a OS em `FINALIZADA`** (cliente pode tentar novamente)

## Alternativas consideradas

| Alternativa | Por que descartada |
|---|---|
| **Sem gateway** | Não reflete a realidade; não demonstra padrão port/adapter explicitamente |
| **Stripe Test Mode (API real free)** | Acrescenta dependência externa, chave de API, SDK; flakiness se a API estiver fora |
| **Mercado Pago Sandbox** | Mesma desvantagem; foco do projeto é DDD, não integração financeira BR |
| **Mock interno (escolhida)** | Sem dependência externa, comportamento determinístico em testes, demonstra port/adapter |

## Consequências

### Positivas
- Domínio permanece **independente de tecnologia de pagamento** (port no domínio, adapter na infra)
- Substituir o mock por Stripe/Mercado Pago é tarefa local na infra (criar nova classe que implementa `PagamentoGateway` e marcá-la como `@Primary` ou `@ConditionalOnProperty`)
- Testes do `OrdemDeServicoService` cobrem **ambos os caminhos** (aprovação e recusa) sem chamadas HTTP reais
- Auditabilidade: `transaction_id` e `gateway_mensagem` ficam persistidos junto ao `Pagamento`

### Negativas
- Aprovação não corresponde a uma autorização real (esperado, é mock)
- Latência simulada por `Thread.sleep` consome thread do pool em testes manuais (desligável via `latency-ms: 0`)
- Pequena complexidade adicional (uma interface + um adapter + uma migration) em relação a chamar `pagamento.aprovar()` direto

## Configuração

```yaml
oficina:
  pagamento:
    gateway:
      approval-rate: 0.9   # 0.0 a 1.0
      latency-ms: 100      # ms
```

Para testes integrados que precisam aprovação garantida:
```properties
oficina.pagamento.gateway.approval-rate=1.0
oficina.pagamento.gateway.latency-ms=0
```

> **Ambiente Docker (docker-compose.yml):** o container da aplicação define `PAGAMENTO_GATEWAY_APPROVAL_RATE=1.0` e `PAGAMENTO_GATEWAY_LATENCY_MS=0` via variáveis de ambiente, garantindo que a suite de testes Newman rode de forma **100% determinística** sem rejeições aleatórias.
