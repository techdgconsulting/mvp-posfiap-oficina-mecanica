# Fluxo de deploy da infraestrutura e da aplicação

## Visão geral

Este fluxo representa as pipelines separadas do projeto, cobrindo:

- provisionamento da infraestrutura na AWS com Terraform
- execução de testes da aplicação
- build da imagem Docker
- publicação no Amazon ECR
- configuração do cluster EKS
- criação do Secret e namespace Kubernetes
- aplicação dos manifestos YAML
- rollout do deployment da API
- exposição da aplicação via LoadBalancer

## Ordem de execução

1. Um mantenedor dispara `.github/workflows/infra.yml` manualmente ou altera arquivos em `infra/**`.
2. A esteira de infraestrutura cria/garante o bucket S3 do Terraform state.
3. A esteira de infraestrutura executa `terraform init`, `validate`, `plan` e `apply`.
4. O Terraform provisiona VPC, EKS, RDS, ECR e IAM.
5. Um push de código da aplicação dispara `.github/workflows/app-cd.yml`.
6. A esteira da aplicação executa `mvn clean test`.
7. A esteira da aplicação lê `terraform output` do state remoto, sem executar `terraform apply`.
8. A esteira da aplicação faz login no ECR, builda a imagem Docker e publica no registro.
9. A esteira da aplicação atualiza o `kubeconfig` do EKS.
10. O job cria o namespace e o `Secret` com credenciais sensíveis.
11. O comando `kubectl apply -k k8s` aplica os manifestos do Kubernetes.
12. O deployment `oficina-api` recebe a imagem nova com `kubectl set image`.
13. O rollout aguarda a disponibilidade do pod.
14. O Service expõe a aplicação e gera o endpoint público da API.

## Componentes criados

### Infraestrutura AWS
- VPC
- Internet Gateway
- Subnets públicas e privadas
- Security Groups
- EKS cluster
- Managed Node Group
- RDS PostgreSQL
- ECR repository
- IAM roles e acessos

### Kubernetes
- Namespace `oficina`
- Secret `oficina-api-secret`
- ConfigMap
- Deployment `oficina-api`
- Service `oficina-api`
- HPA

### Aplicação
- imagem Docker no ECR
- aplicação Spring Boot em execução no cluster
- Swagger e endpoints REST disponíveis via LoadBalancer

## Observações importantes

- O Terraform não deve ser executado em cada commit de aplicação; ele depende do estado remoto do S3 e do mesmo ambiente.
- Para manutenção segura, o nome do projeto, ambiente e bucket do backend devem permanecer consistentes.
- A pipeline da aplicação não deve recriar a infraestrutura em cada push; ela deve apenas atualizar a imagem e o deployment da aplicação.
- A pipeline da aplicação consome `ecr_repository_url`, `eks_cluster_name` e `spring_datasource_url` dos outputs do Terraform.
- O banco e os recursos de rede ficam sob responsabilidade da infraestrutura provisionada pelo Terraform.

## Diagrama

O diagrama correspondente está em:

- [deploy-flow.puml](./deploy-flow.puml)

Para visualizar em VS Code, abra o arquivo `.puml` com suporte a PlantUML.
