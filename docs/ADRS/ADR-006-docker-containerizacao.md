# ADR-006: Containerização com Docker e Docker Compose

**Status:** Aceito  
**Data:** 2026-05-01 
**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O projeto precisa ser facilmente executável por avaliadores sem necessidade de instalar Java, Maven ou PostgreSQL localmente.

## Decisão

Containerizar a aplicação com **Docker** e orquestrar com **Docker Compose**:

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

### Negativas
- Docker Desktop necessário no Windows/Mac
- Build inicial mais lento (download de dependências Maven)
- Debug remoto requer configuração adicional
