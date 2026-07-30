# Arquitetura Da Fase 2

## Visao Geral

A Fase 2 evolui o projeto para uma arquitetura conteinerizada e preparada para execucao em Kubernetes na AWS.

```text
Usuario / Postman / Swagger
   |
   v
AWS Load Balancer
   |
   v
Service Kubernetes
   |
   v
Pods Spring Boot no Amazon EKS
   |
   v
Amazon RDS PostgreSQL
```

## Componentes

| Componente | Responsabilidade |
|---|---|
| Spring Boot API | Expor endpoints REST da oficina. |
| Docker | Empacotar a aplicacao em imagem reproduzivel. |
| Amazon ECR | Armazenar imagens Docker versionadas. |
| Amazon EKS | Executar os pods da aplicacao. |
| Kubernetes Deployment | Controlar replicas, rolling update e probes. |
| Kubernetes Service | Expor a API por LoadBalancer. |
| Kubernetes ConfigMap | Armazenar configuracoes nao sensiveis. |
| Kubernetes Secret | Armazenar credenciais e tokens. |
| HPA | Escalar pods por CPU e memoria. |
| Amazon RDS PostgreSQL | Banco de dados gerenciado. |
| Flyway | Versionar e aplicar migrations. |
| Terraform | Provisionar infraestrutura AWS. |
| GitHub Actions | Executar testes, build, push e deploy. |

## Decisoes De Custo

- Regiao padrao: `us-east-1`.
- Node group pequeno: `t3.small`.
- RDS pequeno: `db.t4g.micro`.
- Sem NAT Gateway para reduzir custo.
- RDS sem IP publico.
- Apenas um Load Balancer para exposicao da API.

## Diferenca Para Producao Real

Em producao real, recomenda-se:

- nodes em subnets privadas;
- NAT Gateway ou VPC Endpoints;
- RDS Multi-AZ;
- backups com retencao maior;
- secrets em AWS Secrets Manager ou External Secrets Operator;
- IAM via OIDC no GitHub Actions;
- Ingress Controller com TLS;
- observabilidade com metricas, logs centralizados e alarmes.

## Banco De Dados

O RDS e criado pelo Terraform. A aplicacao recebe a URL por `SPRING_DATASOURCE_URL` e credenciais por `Secret`.

O ciclo de schema e:

```text
Spring Boot inicia
   |
   v
Flyway aplica migrations
   |
   v
Hibernate valida schema com ddl-auto=validate
```

Nao executar alteracoes manuais de schema fora do Flyway, salvo excecao documentada.
