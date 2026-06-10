# ADR-001: Adoção de Domain-Driven Design (DDD)

**Status:** Aceito  
**Data:** 2026-05-01 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O sistema da oficina mecânica envolve diferentes áreas de negócio, como atendimento, estoque, financeiro e execução de serviços. Cada uma com suas próprias regras e processos. Por conta dessa complexidade é importante adotar uma abordagem focada na compreensão e modelagem do negócio.

## Decisão

Adotar **Domain-Driven Design** como abordagem de modelagem e organização do código, com:

- **Bounded Contexts** separados por subdomínio (Atendimento, Ordem de Serviço, Diagnóstico, Orçamento , Execução, Financeiro, Entrega, Encerramento)
- **Aggregate Roots** como ponto de consistência (Cliente, Veículo, OrdemDeServico, ItemOS, Diagnostico, Peca, Servico, Orcamento, Execucao, Pagamento, Entrega, Encerramento)
- **Value Objects** para conceitos imutáveis com validação (CpfCnpj, Placa, Quantidade, PeriodoExecucao)
- **Repository Interfaces** no domínio (ports) com implementações na infraestrutura (adapters)
- **Linguagem Ubíqua** documentada e refletida no código

## Consequências

### Positivas
- Código expressivo e alinhado com o negócio
- Boundaries claros entre contextos facilitam manutenção
- Value Objects encapsulam validações e previnem estados inválidos
- Testabilidade do domínio sem dependência de infraestrutura

### Negativas
- Overhead de abstrações para operações simples (ex: Serviço é basicamente CRUD)
- Mais classes e interfaces comparado a uma abordagem anêmica
- Curva de aprendizado para desenvolvedores não familiarizados com DDD
