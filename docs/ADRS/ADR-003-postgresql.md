# ADR-003: PostgreSQL como Banco de Dados

**Status:** Aceito  
**Data:** 2026-05-05 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O sistema precisa de persistência relacional para entidades com relacionamentos complexos (OS → Itens → Peças/Serviços, Cliente → Veículos, etc.) e integridade referencial.

## Decisão

Adotar **PostgreSQL 16** como banco de dados relacional principal do projeto.

O ambiente local/de demonstração usa PostgreSQL via Docker Compose (`postgres:16-alpine`). Nesse modo, o banco roda no container `oficina-db`, expõe a porta externa `5434` e atende a aplicação internamente em `db:5432`.

O ambiente AWS usa **Amazon RDS PostgreSQL**, provisionado por Terraform e acessado pela aplicação no Amazon EKS por meio da URL JDBC publicada nos outputs da infraestrutura. O RDS permanece em subnets privadas e recebe tráfego somente do security group associado ao EKS.

No perfil padrão local, a aplicação aponta para PostgreSQL em `localhost:5432` com `ddl-auto=validate` e Flyway habilitado.

Os perfis `dev` e `test` usam H2 em memória para execução rápida sem Docker e para a suíte automatizada atual. H2 não substitui a decisão de banco principal; é um recurso auxiliar de desenvolvimento e teste.

### Justificativas
- Suporte robusto a transações ACID (Atomicidade - Consistência - Isolamento - Durabilidade)
- Tipos de dados ricos (NUMERIC para valores monetários com precisão)
- Open source e amplamente utilizado na indústria
- Boa integração com Spring Data JPA/Hibernate
- Imagem Alpine reduz tamanho do container
- Amazon RDS PostgreSQL permite execução gerenciada no ambiente AWS, mantendo compatibilidade com o mesmo dialeto e migrations Flyway usados localmente

## Alternativas Consideradas

| Opção | Motivo da rejeição |
|-------|-------------------|
| MySQL | Menos recursos avançados, tratamento de NUMERIC menos preciso |
| MongoDB | Modelo relacional é mais adequado para as entidades do domínio |
| H2 (embedded) | Adequado para `dev`/`test` rápidos; não representa o banco principal nem valida integralmente dialeto, constraints e migrations PostgreSQL |
| PostgreSQL apenas em Docker | Adequado para local/demo, mas não oferece operação gerenciada, isolamento de rede e integração nativa com a infraestrutura AWS |

## Consequências

### Positivas
- Integridade referencial garantida via foreign keys
- NUMERIC(12,2) para valores monetários sem perda de precisão
- Ambiente local reprodutível com container leve (`postgres:16-alpine`) e healthcheck nativo via `pg_isready`
- Ambiente AWS com banco gerenciado em Amazon RDS PostgreSQL, integrado à VPC, subnets privadas e security groups

### Negativas
- Necessidade de Docker para reproduzir o ambiente PostgreSQL local/de demonstração
- Necessidade de Terraform/AWS para reproduzir o ambiente em nuvem com RDS
- Migrations obrigatórias para alterações de schema
- H2 em `dev`/`test` pode divergir de comportamentos específicos do PostgreSQL
