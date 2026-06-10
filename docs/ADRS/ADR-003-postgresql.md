# ADR-003: PostgreSQL como Banco de Dados

**Status:** Aceito  
**Data:** 2026-05-05 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O sistema precisa de persistência relacional para entidades com relacionamentos complexos (OS → Itens → Peças/Serviços, Cliente → Veículos, etc.) e integridade referencial.

## Decisão

Adotar **PostgreSQL 16** como banco de dados relacional, executado via Docker (imagem `postgres:16-alpine`).

### Justificativas
- Suporte robusto a transações ACID (Atomicidade - Consistência - Isolamento - Durabilidade)
- Tipos de dados ricos (NUMERIC para valores monetários com precisão)
- Open source e amplamente utilizado na indústria
- Boa integração com Spring Data JPA/Hibernate
- Imagem Alpine reduz tamanho do container

## Alternativas Consideradas

| Opção | Motivo da rejeição |
|-------|-------------------|
| MySQL | Menos recursos avançados, tratamento de NUMERIC menos preciso |
| MongoDB | Modelo relacional é mais adequado para as entidades do domínio |
| H2 (embedded) | Apenas para testes; não adequado para produção |

## Consequências

### Positivas
- Integridade referencial garantida via foreign keys
- NUMERIC(12,2) para valores monetários sem perda de precisão
- Container leve (~80MB com Alpine)
- Healthcheck nativo via `pg_isready`

### Negativas
- Necessidade de Docker para desenvolvimento local
- Migrations obrigatórias para alterações de schema
