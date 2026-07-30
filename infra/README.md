# Infraestrutura AWS - Oficina Mecanica DGCar

Esta pasta contem a infraestrutura minima para executar a API Spring Boot em AWS com Amazon EKS, Amazon ECR e Amazon RDS PostgreSQL.

O desenho prioriza custo academico:

- Regiao padrao: `us-east-1` (N. Virginia).
- Um unico cluster EKS.
- Node group pequeno com `t3.small`.
- RDS PostgreSQL `db.t4g.micro`.
- Banco sem IP publico.
- Sem NAT Gateway para reduzir custo.
- Service Kubernetes do tipo `LoadBalancer` como unico ponto externo da API.

## Recursos Criados

| Recurso | Finalidade |
|---|---|
| VPC | Rede isolada do projeto. |
| Public Subnets | Subnets para EKS managed nodes e LoadBalancers. |
| Private Subnets | Subnets para o RDS PostgreSQL, sem IP publico. |
| Internet Gateway | Saida e entrada publica para subnets publicas. |
| Route Tables | Rotas publicas e privadas. |
| Security Group EKS Cluster | Controle de rede do control plane EKS. |
| EKS Cluster Security Group | Security group associado ao cluster e aos nodes gerenciados pelo EKS. |
| Security Group RDS | Permite PostgreSQL apenas a partir do security group do EKS. |
| Amazon ECR | Repositorio Docker da API. |
| Amazon EKS | Cluster Kubernetes gerenciado. |
| EKS Managed Node Group | Nodes EC2 gerenciados para executar os pods. |
| Amazon RDS PostgreSQL | Banco de dados gerenciado usado pela aplicacao. |
| IAM Roles | Permissoes necessarias para EKS cluster e node group. |

## Arquitetura Economica

Para evitar NAT Gateway, os worker nodes ficam em subnets publicas com IP publico. O RDS fica em subnets privadas e `publicly_accessible=false`.

O banco aceita conexao somente do security group associado ao EKS:

```text
EKS cluster/node security group -> RDS security group -> TCP 5432
```

Em producao real, a recomendacao seria usar nodes em subnets privadas, NAT Gateway, endpoints privados para ECR/S3 e maior separacao de ambientes. Para o projeto academico, esta versao reduz custo mantendo o banco protegido.

## Pre-requisitos

- Terraform `>= 1.6`.
- AWS CLI configurado.
- Credenciais AWS com permissao para criar VPC, IAM, EKS, ECR e RDS.
- `kubectl` para acesso ao cluster depois do provisionamento.

## Como Usar

### 1. Criar Backend Remoto S3

Antes de criar a infraestrutura principal, crie o backend remoto do Terraform:

```bash
cd infra/backend-bootstrap
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars` e escolha um bucket S3 globalmente unico:

```hcl
state_bucket_name = "oficina-dgcar-academic-tfstate-seu-sufixo-unico"
```

Crie o bucket e a tabela de lock:

```bash
terraform init
terraform plan
terraform apply
```

Veja os valores gerados:

```bash
terraform output backend_config_example
```

### 2. Configurar A Infra Principal Para Usar S3

Volte para a pasta `infra`:

```bash
cd ..
cp backend.tf.example backend.tf
```

Edite `backend.tf` com o bucket criado no bootstrap. O arquivo real `backend.tf` nao deve ser versionado, pois referencia recursos especificos da sua conta AWS.

Inicialize o backend remoto:

```bash
terraform init
```

Se ja existir state local da infra principal, use:

```bash
terraform init -migrate-state
```

### 3. Criar Infraestrutura Principal

Copie o arquivo de exemplo:

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars` e troque pelo menos:

```hcl
db_password = "uma-senha-forte"
```

Por padrao, `eks_cluster_version = null`, deixando a AWS escolher a versao padrao suportada do EKS no momento da criacao. Para travar uma versao especifica, informe o valor explicitamente no `terraform.tfvars`.

Nao versionar `terraform.tfvars`, pois contem credenciais.

Inicialize o Terraform caso ainda nao tenha feito:

```bash
terraform init
```

Revise o plano:

```bash
terraform plan
```

Crie a infraestrutura:

```bash
terraform apply
```

Configure o acesso ao cluster EKS:

```bash
aws eks update-kubeconfig --region us-east-1 --name oficina-dgcar-academic-eks
```

Consulte os outputs:

```bash
terraform output
```

Use o output `spring_datasource_url` no `k8s/configmap.yaml` antes de aplicar os manifestos Kubernetes. A variável `OFICINA_PUBLIC_BASE_URL` só deve ser trocada pela URL real depois que o `Service` do tipo `LoadBalancer` existir e o comando `kubectl get svc oficina-api -n oficina` mostrar um `EXTERNAL-IP`.

Use o output `ecr_repository_url` para publicar a imagem Docker no ECR e apontar o `Deployment` para a imagem real. No deploy manual, a imagem precisa existir no ECR antes de validar o rollout; na pipeline CI/CD, esse build/push e o `kubectl set image` sao executados automaticamente.

## Destruicao do Ambiente

Para evitar custos quando a demonstracao terminar:

```bash
terraform destroy
```

Antes de destruir, remova servicos `LoadBalancer` criados pelo Kubernetes se necessario:

```bash
kubectl delete service oficina-api -n oficina
```

## Observacoes de Custo

- O EKS tem custo por cluster/hora mesmo com poucos nodes.
- O Load Balancer criado pelo Service Kubernetes tambem gera custo.
- O RDS gera custo por hora e armazenamento.
- Para ambiente academico, desligue/destrua a infraestrutura apos a demonstracao.
