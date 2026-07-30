# Roteiro Do Video Demonstrativo

Duracao maxima sugerida: 15 minutos.

## 1. Abertura

Tempo sugerido: 1 minuto.

Mostrar:

- nome do projeto;
- objetivo da Fase 2;
- tecnologias usadas: Docker, Kubernetes, EKS, Terraform, RDS, ECR e GitHub Actions.

## 2. Estrutura Do Repositorio

Tempo sugerido: 2 minutos.

Mostrar:

- `Dockerfile`;
- `docker-compose.yml`;
- `k8s/`;
- `infra/`;
- `.github/workflows/ci-cd.yml`;
- `docs/fase-2/`;
- README com a sequencia de infraestrutura.

## 3. Execucao Local

Tempo sugerido: 2 minutos.

Comandos:

```bash
cp .env.example .env
docker compose up --build -d
docker compose logs -f app
```

Mostrar:

- aplicacao subindo;
- PostgreSQL local;
- Swagger em `http://localhost:8080/swagger-ui.html`.

## 4. Terraform E AWS

Tempo sugerido: 3 minutos.

Mostrar:

- bootstrap do backend remoto;
- `terraform plan`;
- recursos criados: ECR, EKS e RDS;
- outputs principais.

Comandos:

```bash
terraform output
aws eks update-kubeconfig --region us-east-1 --name oficina-dgcar-academic-eks
```

## 5. Kubernetes

Tempo sugerido: 2 minutos.

Mostrar:

```bash
kubectl get pods -n oficina
kubectl get svc -n oficina
kubectl get hpa -n oficina
kubectl rollout status deployment/oficina-api -n oficina
```

Explicar:

- readiness/liveness probes;
- HPA;
- Service LoadBalancer;
- Secrets fora do Git.

## 6. CI/CD

Tempo sugerido: 2 minutos.

Mostrar no GitHub Actions:

- job `test`;
- job `docker-build`;
- job `deploy`;
- push da imagem no ECR;
- rollout no EKS.

## 7. Consumo Das APIs

Tempo sugerido: 2 minutos.

Mostrar:

- Swagger;
- Postman collection;
- login;
- abertura de OS;
- consulta de status;
- notificacao por e-mail, se SMTP/Mailtrap estiver configurado.

## 8. Escalabilidade

Tempo sugerido: 1 minuto.

Mostrar:

```bash
kubectl get hpa -n oficina
kubectl top pods -n oficina
```

Se nao houver carga real, explicar a configuracao:

- `minReplicas: 1`;
- `maxReplicas: 3`;
- CPU alvo `70%`;
- memoria alvo `75%`.

## 9. Encerramento

Tempo sugerido: 30 segundos.

Mostrar checklist final e lembrar a remocao dos recursos para evitar custos:

```bash
kubectl delete namespace oficina
terraform destroy
```
