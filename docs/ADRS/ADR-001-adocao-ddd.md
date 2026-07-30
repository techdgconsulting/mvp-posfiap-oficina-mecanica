# ADR-001: Adoção de Domain-Driven Design (DDD)

**Status:** Aceito  
**Data:** 2026-05-01 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O sistema da oficina mecânica envolve diferentes áreas de negócio, como atendimento, estoque, financeiro e execução de serviços. Cada uma com suas próprias regras e processos. Por conta dessa complexidade é importante adotar uma abordagem focada na compreensão e modelagem do negócio.

## Decisão

Adotar **Domain-Driven Design** como abordagem de modelagem e organização do código, preservando o domínio independente de frameworks e integrando-o à arquitetura ports/adapters do projeto:

- **Bounded Contexts** separados por subdomínio (Atendimento, Ordem de Serviço, Diagnóstico, Orçamento , Execução, Financeiro, Entrega, Encerramento)
- **Modelos de domínio** em `domain/model` como ponto de consistência das regras de negócio (Cliente, Veiculo, OrdemDeServico, ItemOS, Diagnostico, Peca, Servico, Orcamento, Execucao, Pagamento, Entrega, Encerramento, Usuario)
- **Value Objects** em `domain/valueobject` para conceitos imutáveis com validação (CpfCnpj, Placa, Quantidade, PeriodoExecucao, PerfilUsuario, status e enums do fluxo)
- **Casos de uso** em `application/usecase`, responsáveis por orquestrar comandos, consultas, transições de estado e chamadas aos ports
- **Ports de entrada e saída** em `application/port/in` e `application/port/out`, mantendo o domínio livre de dependências de persistência, segurança, HTTP ou gateways externos
- **Adapters** em `adapters/in` e `adapters/out`, implementando entrada REST, persistência, segurança, ViaCEP e gateway de pagamento mock
- **Linguagem Ubíqua** documentada e refletida no código

## Consequências

### Positivas
- Código expressivo e alinhado com o negócio
- Boundaries claros entre contextos facilitam manutenção
- Value Objects encapsulam validações e previnem estados inválidos
- Testabilidade do domínio sem dependência de infraestrutura
- Ports na camada de aplicação permitem trocar persistência, gateway de pagamento, token JWT e integrações externas sem alterar regras de domínio
- Adapters isolam detalhes técnicos e mantêm controllers, JPA e clientes HTTP fora do núcleo de negócio

### Negativas
- Overhead de abstrações para operações simples (ex: Serviço é basicamente CRUD)
- Mais classes e interfaces comparado a uma abordagem anêmica
- Curva de aprendizado para desenvolvedores não familiarizados com DDD
- Necessidade de mapeamentos entre requests/responses, commands/results, modelos de domínio e entidades JPA
