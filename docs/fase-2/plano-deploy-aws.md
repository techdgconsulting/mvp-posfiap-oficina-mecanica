# Plano de Deploy AWS - Fase 2

Este documento descreve o passo a passo para provisionar a infraestrutura AWS, publicar a imagem Docker e executar a API Oficina Mecanica DGCar em Amazon EKS com PostgreSQL gerenciado por Amazon RDS.

## Objetivo

Implantar a aplicacao Java Spring Boot em uma arquitetura conteinerizada, versionavel e automatizavel, usando:

- Docker para empacotamento da aplicacao.
- Amazon ECR para armazenar imagens.
- Amazon EKS para orquestracao Kubernetes.
- Amazon RDS PostgreSQL para banco de dados.
- Terraform para infraestrutura como codigo.
- GitHub Actions para CI/CD.

## Ordem De Execucao

1. Validar execucao local com Docker Compose.
2. Criar backend remoto do Terraform em S3.
3. Configurar a infra principal para usar o backend remoto.
4. Provisionar VPC, ECR, EKS e RDS com Terraform.
5. Atualizar `k8s/configmap.yaml` com `spring_datasource_url`.
6. Configurar GitHub Secrets.
7. Executar pipeline CI/CD.
8. Validar aplicacao no Kubernetes.
9. Demonstrar APIs via Swagger/Postman.
10. Destruir recursos apos a demonstracao.

## Execucao Local

```bash
cp .env.example .env
docker compose up --build -d
docker compose logs -f app
```

Validar:

```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/api-docs
```

## Backend Remoto Terraform

```bash
cd infra/backend-bootstrap
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform plan
terraform apply
terraform output backend_config_example
```

Use um bucket S3 globalmente unico em `state_bucket_name`.

## Infraestrutura Principal

```bash
cd ../
cp backend.tf.example backend.tf
terraform init
cp terraform.tfvars.example terraform.tfvars
terraform plan
terraform apply
terraform output
```

## Configurar Kubernetes

Atualize `k8s/configmap.yaml`:

```yaml
SPRING_DATASOURCE_URL: "jdbc:postgresql://endpoint-rds:5432/oficina"
```

Crie o secret manualmente ou deixe a pipeline criar:

```bash
kubectl create secret generic oficina-api-secret -n oficina \
  --from-literal=SPRING_DATASOURCE_USERNAME=oficina \
  --from-literal=SPRING_DATASOURCE_PASSWORD=senha-do-rds \
  --from-literal=JWT_SECRET=chave-forte-com-no-minimo-32-caracteres \
  --from-literal=SMTP_USERNAME= \
  --from-literal=SMTP_PASSWORD= \
  --dry-run=client -o yaml | kubectl apply -f -
```

Aplicar manifestos:

```bash
kubectl apply -k k8s
kubectl rollout status deployment/oficina-api -n oficina
```

## Validacao

```bash
kubectl get pods -n oficina
kubectl get svc -n oficina
kubectl get hpa -n oficina
kubectl logs deployment/oficina-api -n oficina
```

## Destruicao

```bash
kubectl delete namespace oficina
cd infra
terraform destroy
cd backend-bootstrap
terraform destroy
```

Antes de destruir o backend remoto, confirme que o state nao sera mais necessario.
