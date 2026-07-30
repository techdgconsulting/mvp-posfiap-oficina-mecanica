# ADR-002: Arquitetura Clean Architecture com Ports e Adapters

**Status:** Aceito  
**Data:** 2026-05-04 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O projeto iniciou com uma organização mais próxima de uma arquitetura em camadas tradicional, com separação entre interfaces, serviços de aplicação, domínio e infraestrutura. Com a evolução funcional do sistema e a necessidade de explicitar melhor os limites arquiteturais, a estrutura foi migrada para uma abordagem baseada em Clean Architecture, mantendo elementos compatíveis com Ports and Adapters.

A migração buscou reduzir o acoplamento entre controllers, persistência e regras de negócio, além de tornar mais clara a direção das dependências: o domínio e os casos de uso não devem depender de frameworks, banco de dados, API REST ou detalhes técnicos de infraestrutura.

## Decisão

Adotar Clean Architecture como estilo arquitetural principal do projeto, organizada em torno de domínio, casos de uso, ports e adapters. A arquitetura atual substitui a leitura anterior de camadas genéricas por uma separação explícita entre núcleo de negócio, orquestração de aplicação e mecanismos externos:

```
domain/               → Modelos, Value Objects e exceções de domínio
application/          → Casos de uso, comandos, queries e ports
application/port/in   → Contratos de entrada consumidos pelos adapters inbound
application/port/out  → Contratos de saída implementados pelos adapters outbound
adapters/in/web       → Controllers REST, requests, responses e mappers web
adapters/out          → Persistência, pagamento, segurança e integrações externas
infrastructure/       → Configurações Spring, segurança JWT e clientes técnicos
```

A pasta `application` concentra os casos de uso e os contratos de entrada e saída. Os adapters inbound, como controllers REST, chamam ports de entrada. Os adapters outbound, como persistência, pagamento e integrações externas, implementam ports de saída definidos pela aplicação. A camada `infrastructure` permanece responsável por configuração técnica, segurança e composição do Spring.

### Regras de dependência
- `domain` não depende de nenhuma outra camada
- `application` depende apenas de `domain`
- `application.port.in` define os casos de uso expostos para entrada
- `application.port.out` define contratos exigidos pelos casos de uso para persistência, pagamento e integrações externas
- `adapters/in/web` depende dos ports de entrada da aplicação
- `adapters/out/*` implementa ports de saída definidos pela aplicação
- `infrastructure` concentra configuração técnica e não deve conter regra de negócio

### Migração da arquitetura anterior

A arquitetura anterior usava termos e pacotes mais próximos de `interfaces`, `application.service` e `infrastructure.gateway`. Na arquitetura atual, esses papéis foram redistribuídos:

- controllers, requests, responses e mappers REST foram concentrados em `adapters/in/web`
- serviços de aplicação foram evoluídos para casos de uso em `application/usecase`
- contratos de entrada passaram para `application/port/in`
- contratos de saída passaram para `application/port/out`
- implementações técnicas de persistência, pagamento e integrações foram concentradas em `adapters/out/*`
- modelos e regras centrais permaneceram no núcleo `domain`

### Evolução recente

O endpoint `POST /api/ordens-servico/completa` foi implementado como um fluxo independente na camada de aplicação, por meio de `CriarOrdemServicoCompletaInputPort` e `CriarOrdemServicoCompletaUseCase`. Essa opção preserva a compatibilidade do contrato REST existente (`POST /api/ordens-servico`, baseado em `clienteId` e `veiculoId`) e mantém a orquestração de criação/reaproveitamento de cliente, veículo, itens e OS fora dos controllers.

O endpoint `GET /api/ordens-servico/fila` foi adicionado como consulta operacional específica, por meio de `ListarFilaOrdensServicoInputPort` e do port de saída `OrdemDeServicoRepositoryPort.listarFilaOperacional`. A ordenação e o filtro da fila ficam fora do controller, preservando o controller como adapter de entrada e mantendo a regra de consulta no fluxo de aplicação/persistência.

## Consequências

### Positivas
- Domínio isolado e testável unitariamente
- Facilidade de trocar banco de dados ou framework web
- Separação clara entre regras de negócio, casos de uso e detalhes técnicos
- Evolução mais explícita para ports/adapters, reduzindo acoplamento entre controllers, persistência e regras de negócio
- Documentação alinhada à migração da arquitetura antiga para a Clean Architecture atual

### Negativas
- Duplicação de mapeamento entre camadas (Entity ↔ DTO)
- Para operações simples (CRUD de Serviço), as camadas podem parecer excessivas
- Mais interfaces e objetos de transferência do que em uma aplicação CRUD tradicional
