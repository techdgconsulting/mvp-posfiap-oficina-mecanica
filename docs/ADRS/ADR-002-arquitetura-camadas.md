# ADR-002: Arquitetura em Camadas (Hexagonal-Inspired)

**Status:** Aceito  
**Data:** 2026-05-04 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

Necessidade de uma estrutura que separe claramente as responsabilidades e permita que o domínio não dependa de frameworks ou infraestrutura.

## Decisão

Adotar uma arquitetura em 4 camadas inspirada em Hexagonal Architecture:

```
interfaces/     → Controllers REST (entrada)
application/    → Services, DTOs, Exceptions (orquestração)
domain/         → Entities, VOs, Enums, Repository Ports (regras de negócio)
infrastructure/ → JPA Adapters, Security, Clients (implementações externas) 
```

### Regras de dependência
- `domain` não depende de nenhuma outra camada
- `application` depende apenas de `domain`
- `infrastructure` implementa interfaces definidas em `domain`
- `interfaces` depende de `application`

## Consequências

### Positivas
- Domínio isolado e testável unitariamente
- Facilidade de trocar banco de dados ou framework web
- Separação clara entre "o que" (domínio) e "como" (infraestrutura)

### Negativas
- Duplicação de mapeamento entre camadas (Entity ↔ DTO)
- Para operações simples (CRUD de Serviço), as camadas podem parecer excessivas
