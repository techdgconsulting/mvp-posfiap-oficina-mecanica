# Fluxo de deploy da infraestrutura e da aplicação

## Visão geral

Este fluxo representa a pipeline unificada do projeto, cobrindo:

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

1. O desenvolvedor faz push ou dispara o workflow manualmente.
2. A GitHub Actions inicia a pipeline.
3. O job `infra` executa `terraform init`, `plan` e `apply`.
4. O job `build-test` executa `mvn clean test`.
5. O job `deploy` faz o login no ECR, builda a imagem Docker e publica no registro.
6. O job `deploy` atualiza o `kubeconfig` do EKS.
7. O job cria o namespace e o `Secret` com credenciais sensíveis.
8. O comando `kubectl apply -k k8s` aplica os manifestos do Kubernetes.
9. O deployment `oficina-api` recebe a imagem nova com `kubectl set image`.
10. O rollout aguarda a disponibilidade do pod.
11. O Service expõe a aplicação e gera o endpoint público da API.

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

- O Terraform não deve ser executado em cada commit sem controle; ele depende do estado remoto do S3 e do mesmo ambiente.
- Para manutenção segura, o nome do projeto, ambiente e bucket do backend devem permanecer consistentes.
- A pipeline da aplicação não deve recriar a infraestrutura em cada push; ela deve apenas atualizar a imagem e o deployment da aplicação.
- O banco e os recursos de rede ficam sob responsabilidade da infraestrutura provisionada pelo Terraform.

## Diagrama

O diagrama correspondente está em:

- [deploy-flow.puml](./deploy-flow.puml)

Para visualizar em VS Code, abra o arquivo `.puml` com suporte a PlantUML.
