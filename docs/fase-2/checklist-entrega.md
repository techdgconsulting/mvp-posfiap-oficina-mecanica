# Checklist De Entrega - Fase 2

Use este checklist antes da entrega academica.

## Repositorio

- [ ] Codigo-fonte atualizado.
- [ ] Dockerfile revisado.
- [ ] docker-compose revisado.
- [ ] `.dockerignore` criado.
- [ ] `.env.example` criado.
- [ ] Segredos reais fora do repositorio.
- [ ] Manifestos Kubernetes em `k8s/`.
- [ ] Terraform em `infra/`.
- [ ] Backend S3 do Terraform documentado.
- [ ] Pipeline GitHub Actions criada.
- [ ] README principal atualizado.
- [ ] Documentacao adicional em `docs/fase-2/`.

## Validacoes Locais

- [ ] `mvn clean test` executado com sucesso.
- [ ] `docker compose up --build -d` executado com sucesso.
- [ ] Swagger acessivel em `http://localhost:8080/swagger-ui.html`.
- [ ] Collection Postman importada.
- [ ] Fluxo de notificacao por e-mail validado em modo LOG ou SMTP.

## Terraform

- [ ] `infra/backend-bootstrap/terraform.tfvars` criado localmente.
- [ ] Backend S3 criado.
- [ ] DynamoDB lock table criada.
- [ ] `infra/backend.tf` criado localmente.
- [ ] `infra/terraform.tfvars` criado localmente.
- [ ] `terraform fmt -recursive` executado.
- [ ] `terraform validate` executado.
- [ ] `terraform plan` revisado.
- [ ] `terraform apply` executado somente quando autorizado.

## Kubernetes

- [ ] `kubectl kustomize k8s` executado.
- [ ] `SPRING_DATASOURCE_URL` atualizado no ConfigMap.
- [ ] `oficina-api-secret` criado no cluster.
- [ ] `kubectl apply -k k8s` executado.
- [ ] `kubectl rollout status deployment/oficina-api -n oficina` concluido.
- [ ] Service `oficina-api` com endpoint externo.
- [ ] HPA criado.

## CI/CD

- [ ] GitHub Secrets configurados.
- [ ] Pull request executa testes e build Docker.
- [ ] Push na `main` publica imagem no ECR.
- [ ] Deploy no EKS concluido.
- [ ] Rollout validado pela pipeline.

## Demonstracao

- [ ] API consumida via Swagger.
- [ ] API consumida via Postman.
- [ ] Deploy demonstrado.
- [ ] Pipeline demonstrada.
- [ ] HPA ou simulacao de escalabilidade demonstrada.
- [ ] Link do video adicionado ao README.

## Custos

- [ ] Load Balancer removido apos demonstracao.
- [ ] Namespace Kubernetes removido.
- [ ] `terraform destroy` da infra principal executado se ambiente nao for mais usado.
- [ ] Backend remoto destruido somente se o state nao for mais necessario.
