# 🔧 Oficina Mecânica API

> **Tech Challenge — Pós-Graduação FIAP**
> MVP de back-end para gestão completa de uma oficina mecânica.

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Coverage](https://img.shields.io/badge/coverage-99%25-brightgreen)
![Tests](https://img.shields.io/badge/testes-340-blue)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)

---

## 📋 Sobre o projeto

API REST que cobre o ciclo completo de atendimento de uma oficina mecânica: do cadastro de clientes e veículos até a entrega do veículo após pagamento. Desenvolvido com arquitetura **DDD (Domain-Driven Design)** em camadas, autenticação **JWT** e controle de acesso por perfil (**RBAC**).

**Funcionalidades principais:**
- Cadastro de clientes (CPF/CNPJ) com busca automática de endereço via **ViaCEP**
- Cadastro de veículos vinculados a clientes
- Catálogo de serviços e controle de estoque de peças (com alerta de estoque mínimo)
- Ordens de serviço com fluxo completo de 7 estados
- Geração e aprovação de orçamentos
- Registro de pagamento via gateway (mock configurável)
- KPIs de tempo médio de execução e atendimento

---

## 🚀 Stack

| Camada | Tecnologia |
|---|---|
| Linguagem / Framework | Java 17 + Spring Boot 3.3 |
| Banco de dados | PostgreSQL 16 (produção) · H2 (dev/test) |
| Migrations | Flyway |
| Segurança | Spring Security + JWT (HMAC-SHA256) |
| Documentação | SpringDoc OpenAPI (Swagger) |
| Testes | JUnit 5 + Mockito · JaCoCo · Allure Report |
| Infraestrutura | Docker + Docker Compose |

---

## ⚙️ Como rodar

### Pré-requisitos
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

### Com Docker (recomendado)

```bash
# Sobe aplicação + banco PostgreSQL
docker-compose up --build -d

# Acompanhar logs
docker logs oficina-mecanica-dgcar -f

# Parar
docker-compose down

# Parar e resetar o banco
docker-compose down -v
```

### Sem Docker (perfil dev com H2)

```bash
# Linux / macOS
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Windows (PowerShell)
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

> ⚠️ No perfil `dev` o banco é em memória — os dados são perdidos ao parar a aplicação.

Após subir, acesse:
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **API Base:** http://localhost:8080/api

---

## 🔐 Autenticação

A API usa **JWT Bearer Token**. Faça login para obter o token e envie-o no header de cada requisição:

```
Authorization: Bearer <token>
```

### Usuários de demonstração

| Usuário | Senha | Perfil | Acesso |
|---|---|---|---|
| `atendente1` | `senha123` | ATENDENTE | Clientes, veículos, orçamentos, pagamentos |
| `mecanico1` | `senha123` | MECANICO | Diagnóstico, itens da OS, execução |
| `gestor1` | `senha123` | GESTOR | Acesso completo + KPIs + catálogo |

```bash
# Exemplo de login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "gestor1", "password": "senha123"}'
```

---

## 🔄 Fluxo da Ordem de Serviço

```
RECEBIDA → EM_DIAGNOSTICO → AGUARDANDO_APROVACAO → EM_EXECUCAO → FINALIZADA → AGUARDANDO_RETIRADA → ENTREGUE
                                      └─────────────────────────────────────────────────────────────→ CANCELADA
```

| Etapa | Endpoint | Perfil |
|---|---|---|
| Abrir OS | `POST /api/ordens-servico` | ATENDENTE |
| Iniciar diagnóstico | `PATCH /{id}/iniciar-diagnostico` | MECANICO |
| Gerar orçamento | `POST /{id}/orcamento` | ATENDENTE |
| Aprovar / Rejeitar | `PATCH /{id}/aprovar` · `/{id}/rejeitar` | ATENDENTE |
| Finalizar serviço | `PATCH /{id}/finalizar` | MECANICO |
| Registrar pagamento | `POST /{id}/pagamento` | ATENDENTE |
| Entregar veículo | `PATCH /{id}/entregar` | ATENDENTE |

---

## 🗂️ Estrutura do projeto

```
src/main/java/br/com/oficina/
├── domain/           # Regras de negócio puras — entidades, VOs, enums
│   ├── atendimento/  #   Cliente, Veículo
│   ├── ordemservico/ #   OS, máquina de estados
│   ├── orcamento/    #   Orçamento, aprovação
│   ├── execucao/     #   Diagnóstico, execução
│   ├── financeiro/   #   Pagamento, gateway (port)
│   ├── estoque/      #   Peças, controle de estoque
│   ├── entrega/      #   Liberação e entrega do veículo
│   └── encerramento/ #   Encerramento formal da OS
├── application/      # Casos de uso — services, DTOs, exceptions
├── infrastructure/   # JPA, segurança JWT, ViaCEP, gateway mock
└── interfaces/       # Controllers REST, GlobalExceptionHandler
```

---

## 🧪 Testes

```bash
# Rodar todos os testes
mvn clean test

# Gerar relatório de cobertura (JaCoCo)
mvn clean test jacoco:report
# → target/site/jacoco/index.html

# Gerar relatório Allure
mvn test allure:report
# → target/site/allure-maven-plugin/index.html

# No Windows rodar o comando
.\allure-report.ps1 # roda testes + gera report + abre no browser (porta 9090)
# Script criado para subir um servidor Python HTTP para evitar bloqueio de CORS no browser.

```
---

## 📁 Documentação adicional

| Recurso | Localização |
|---|---|
| Diagramas DDD (PlantUML) | [`/docs`](./docs/diagramas) |
| ADRs (decisões de arquitetura) | [`/docs/diagramas`](./docs/ADRS) |
| Collection Postman (278 requests) | [`/postman`](./postman) |
| Requisições HTTP (VS Code) | [`api-requests.http`](./api-requests.http) |
| Requisitos Funcionais e Não Funcionais | [`/docs`](./docs/requisitos) |
| Relatório de Vulnerabilidades OWASP | [`/docs`](./docs/ReportOWASP) |
| BrainStorming | Miro link abaixo |
| Domain Storytelling | Miro link abaixo |
| Diagrama de Linguagem Ubíqua | Miro link abaixo |
| EventStorming | Miro link abaixo |
| Diagrama de Contexto Limitado | Miro link abaixo |

https://miro.com/app/board/uXjVHc0alo8=/?share_link_id=611826904943

---

## 📄 Licença

Projeto desenvolvido para fins acadêmicos — **Pós-Graduação FIAP**.
