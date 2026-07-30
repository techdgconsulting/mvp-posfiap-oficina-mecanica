# ADR-006: Containerização com Docker e Docker Compose

**Status:** Aceito  
**Data:** 2026-05-01 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O projeto precisa ser facilmente executável por avaliadores sem necessidade de instalar Java, Maven ou PostgreSQL localmente.
Também é necessário reduzir diferenças entre ambientes, já que o comportamento de persistência, migrations e constraints deve ser observado em PostgreSQL real, e não apenas em banco em memória.

## Decisão

Containerizar a aplicação com **Docker** e orquestrar com **Docker Compose**:

### Justificativas
- **Paridade com o ambiente principal:** a aplicação sobe com PostgreSQL 16, o mesmo banco usado como referência arquitetural do projeto.
- **Onboarding do avaliador:** um único comando (`docker-compose up --build -d`) compila a aplicação, sobe o banco e expõe a API na porta 8080.
- **Reprodutibilidade:** dependências de runtime, versão do JDK, banco, rede e variáveis de ambiente ficam declaradas no repositório.
- **Isolamento:** a rede `oficina-net` mantém a comunicação aplicação-banco isolada do restante da máquina.
- **Inicialização ordenada:** o healthcheck `pg_isready` evita que a aplicação tente conectar antes de o PostgreSQL estar pronto.
- **Persistência controlada:** o volume `pgdata` preserva dados entre reinícios e pode ser removido com `docker-compose down -v` quando for necessário resetar o ambiente.
- **Segurança mínima no runtime:** o container da aplicação executa com usuário não-root (`appuser`).

### Dockerfile (multi-stage)
- **Build stage:** `maven:3.9-eclipse-temurin-17` — compila o projeto
- **Runtime stage:** `eclipse-temurin:17-jre-alpine` — imagem mínima (~180MB)
- **Segurança:** Executa como usuário não-root (`appuser`)

### Docker Compose
- **oficina-mecanica-dgcar:** Aplicação Spring Boot (porta 8080)
- **oficina-db:** PostgreSQL 16 Alpine (porta interna 5432, externa 5434)
- **Rede:** `oficina-net` (bridge isolada)
- **Volume:** `pgdata` (persistência entre restarts)
- **Healthcheck:** `pg_isready` no PostgreSQL antes de iniciar a aplicação

## Consequências

### Positivas
- Um comando (`docker-compose up --build -d`) sobe todo o ambiente
- Isolamento de dependências (sem conflito com versões locais)
- Reprodutibilidade: funciona em qualquer máquina com Docker
- Multi-stage build reduz tamanho da imagem final
- PostgreSQL real exercita Flyway, constraints e dialeto usados no ambiente principal

### Negativas
- Docker Desktop necessário no Windows/Mac
- Build inicial mais lento (download de dependências Maven)
- Debug remoto requer configuração adicional
- Volume persistente pode manter dados antigos; quando a intenção for recriar a base do zero, é necessário executar `docker-compose down -v`
