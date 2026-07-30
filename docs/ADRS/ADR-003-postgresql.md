# ADR-003: PostgreSQL como Banco de Dados

**Status:** Aceito  
**Data:** 2026-05-05 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O sistema precisa de persistência relacional para entidades com relacionamentos complexos (OS → Itens → Peças/Serviços, Cliente → Veículos, etc.) e integridade referencial.

## Decisão

Adotar **PostgreSQL 16** como banco de dados relacional principal, executado via Docker no ambiente recomendado (`postgres:16-alpine`).

No perfil padrão, a aplicação aponta para PostgreSQL em `localhost:5432` com `ddl-auto=validate` e Flyway habilitado. No Docker Compose, o PostgreSQL roda no container `oficina-db`, expõe a porta externa `5434` e atende a aplicação internamente em `db:5432`.

Os perfis `dev` e `test` usam H2 em memória para execução rápida sem Docker e para a suíte automatizada atual. H2 não substitui a decisão de banco principal; é um recurso auxiliar de desenvolvimento e teste.

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
| H2 (embedded) | Adequado para `dev`/`test` rápidos; não representa o banco principal nem valida integralmente dialeto, constraints e migrations PostgreSQL |

## Consequências

### Positivas
- Integridade referencial garantida via foreign keys
- NUMERIC(12,2) para valores monetários sem perda de precisão
- Container leve (~80MB com Alpine)
- Healthcheck nativo via `pg_isready`

### Negativas
- Necessidade de Docker para reproduzir o ambiente PostgreSQL recomendado
- Migrations obrigatórias para alterações de schema
- H2 em `dev`/`test` pode divergir de comportamentos específicos do PostgreSQL
