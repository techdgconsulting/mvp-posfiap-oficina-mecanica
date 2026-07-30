# 🔧 Oficina Mecânica API

> **Tech Challenge — Pós-Graduação FIAP**
> MVP de back-end para gestão completa de uma oficina mecânica.

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.16-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Coverage](https://img.shields.io/badge/coverage-96.23%25-brightgreen)
![Tests](https://img.shields.io/badge/testes-361-blue)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)

---

## 📋 Sobre o projeto

API REST que cobre o ciclo completo de atendimento de uma oficina mecânica: do cadastro de clientes e veículos até a entrega do veículo após pagamento. Desenvolvido com arquitetura **DDD (Domain-Driven Design)** em camadas, autenticação **JWT** e controle de acesso por perfil (**RBAC**).

**Funcionalidades principais:**
- Cadastro de clientes (CPF/CNPJ) com busca automática de endereço via **ViaCEP**
- Cadastro de veículos vinculados a clientes
- Catálogo de serviços e controle de estoque de peças (com alerta de estoque mínimo)
- Ordens de serviço com fluxo completo de 7 estados
- Geração de orçamentos, aprovação interna e decisão externa segura por token
- Notificação informativa por e-mail ao cliente a cada mudança relevante de status da OS
- Registro de pagamento via gateway (mock configurável)
- KPIs de tempo médio de execução e atendimento


## 🚀 Stack

| Camada | Tecnologia |
|---|---|
| Linguagem / Framework | Java 17 + Spring Boot 3.5.16 |
| Banco de dados | PostgreSQL 16 (produção) · H2 (dev/test) |
| Migrations | Flyway |
| Segurança | Spring Security + JWT (HMAC-SHA256) |
| Documentação | SpringDoc OpenAPI 2.8.17 (Swagger) |
| Testes | JUnit 5 + Mockito · JaCoCo · Allure Report |
| Infraestrutura | Docker + Docker Compose |
| Notificações | Spring Mail + SMTP configurável |


---



## ---------------------------------------------------------------- Evoluções do Projeto - Fase 2 Tech Challenge -------------------------------------------------------------

### Objetivo da Fase 2

A Fase 2 prepara o projeto para implantação conteinerizada e automatizada em AWS, mantendo o foco acadêmico em custo controlado e aproximando a solução de práticas produtivas. O escopo inclui Docker, Docker Compose, Kubernetes, Amazon EKS, Amazon ECR, Amazon RDS PostgreSQL, Terraform, backend remoto de state em S3, CI/CD com GitHub Actions, secrets externos ao repositório e documentação operacional.

### Arquitetura

O projeto iniciou com uma organização mais próxima de uma arquitetura em camadas tradicional, com separação entre interfaces, serviços de aplicação, domínio e infraestrutura. Com a evolução funcional do sistema e a necessidade de explicitar melhor os limites arquiteturais, a estrutura foi migrada para uma abordagem baseada em Clean Architecture, mantendo elementos compatíveis com Ports and Adapters.

Adoção da Clean Architecture como estilo arquitetural principal do projeto, organizada em torno de domínio, casos de uso, ports e adapters. A arquitetura atual substitui a leitura anterior de camadas genéricas por uma separação explícita entre núcleo de negócio, orquestração de aplicação e mecanismos externos:


domain/               → Modelos, Value Objects e exceções de domínio
application/          → Casos de uso, comandos, queries e ports
application/port/in   → Contratos de entrada consumidos pelos adapters inbound
application/port/out  → Contratos de saída implementados pelos adapters outbound
adapters/in/web       → Controllers REST, requests, responses e mappers web
adapters/out          → Persistência, pagamento, segurança e integrações externas
infrastructure/       → Configurações Spring, segurança JWT e clientes técnicos

Essa decisão está registrada na [ADR-002](./docs/ADRS/ADR-002-arquitetura-clean-arch.md).



### Funcionalidades



 ### Abertura de Ordem de Serviço (OS): receber os dados do cliente,veículo, serviços e peças, retornando a identificação única da OS.

O endpoint `POST /api/ordens-servico` preserva o fluxo por `clienteId` e `veiculoId`, indicado para cliente e veículo previamente cadastrados. O endpoint `POST /api/ordens-servico/completa` permite abrir a OS com dados cadastrais de cliente e veículo no mesmo payload, reaproveitando cadastros existentes por documento e placa quando aplicável. Serviços e peças são informados por IDs do catálogo existente; valores não são recebidos livremente no payload.

No cadastro direto de cliente e na abertura completa de OS, o CEP atua como enriquecimento cadastral opcional. Quando um cliente novo é criado e apenas o CEP é informado, o sistema tenta consultar o ViaCEP para preencher logradouro, bairro, cidade e UF; falha ou ausência de retorno do ViaCEP não bloqueia a criação. Na abertura completa, clientes já existentes são reaproveitados sem sobrescrever o endereço cadastrado.



### Consulta de status da OS: informar a situação atual da OS (Recebida, Diagnóstico, Aguardando Aprovação, Execução, Finalizada, Entregue).

A consulta de status da Ordem de Serviço está disponível por dois caminhos complementares. O endpoint `GET /api/ordens-servico/{id}/status` consulta a situação por identificador interno da OS e requer autenticação JWT, sendo indicado para uso operacional por usuários da oficina. O endpoint `GET /api/ordens-servico/numero/{numero}/status` consulta a situação pelo número legível gerado pelo sistema, como `OS-2026-00001`, e é indicado para acompanhamento externo pelo cliente.

Os retornos informam a situação atual da OS dentro do fluxo de atendimento, incluindo os estados principais do requisito: `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA` e `ENTREGUE`. O fluxo também contempla estados intermediários ou excepcionais usados pela regra de negócio, como `AGUARDANDO_RETIRADA` e `CANCELADA`.



### Aprovação de orçamento: endpoint para receber notificações externas de aprovação ou recusa do orçamento do cliente.

Foi adicionada uma evolução no fluxo de orçamento para permitir que o cliente aprove ou recuse o orçamento por meio de uma notificação externa. O fluxo interno autenticado foi preservado para uso operacional da oficina, enquanto o novo fluxo externo utiliza token opaco, expiração e uso único para permitir a decisão sem exigir login/JWT do cliente.

Principais pontos da implementação:

- `POST /api/ordens-servico/{id}/orcamento/notificar-cliente`: endpoint interno, protegido para `ATENDENTE` e `GESTOR`, responsável por gerar a solicitação de decisão e enviar a notificação ao e-mail cadastrado.
- `POST /api/orcamentos/decisoes-cliente/{token}/aprovar`: endpoint público para aprovação externa do orçamento por token.
- `POST /api/orcamentos/decisoes-cliente/{token}/recusar`: endpoint público para recusa externa do orçamento por token.
- O token é gerado de forma opaca e não previsível; somente seu hash é persistido na base de dados.
- A solicitação possui expiração padrão de 48 horas e só pode ser utilizada uma vez.
- A aprovação externa reaproveita a mesma regra de negócio da aprovação interna, avançando a OS para `EM_EXECUCAO`.
- A recusa externa reaproveita a mesma regra de negócio da rejeição interna, cancelando a OS conforme o fluxo já existente.
- O envio de e-mail usa `EmailNotificacaoPort`, com modo `LOG` para execução local e modo `SMTP` para envio real quando configurado.

Essa funcionalidade está documentada também na [ADR-010](./docs/ADRS/ADR-010-decisao-externa-orcamento-token.md), na especificação de requisitos e nos diagramas C4.

Para testar no Postman, importe a collection [`OficinaMecanicaDGCar — Suite Completa`](./postman/OficinaMecanicaDGCar%20%E2%80%94%20Suite%20Completa.postman_collection.json) e execute o grupo `04 — Oficina Mecanica DGCAR > 06 - Ordem de Serviço (Fluxo Completo)` na ordem. Após `06.6 - Gerar Orçamento`, execute `06.6a - Notificar Cliente sobre Orçamento`; esse request cria a solicitação externa, valida os links retornados e grava automaticamente a variável `orcamentoDecisaoToken`. Em seguida, execute `06.7 - Aprovar Orçamento por Token (cliente aceita)`, que usa essa variável para chamar o endpoint público de aprovação sem JWT.

O cenário de recusa pode ser validado no grupo `04 — Oficina Mecanica DGCAR > 08 - Fluxo Alternativo (Rejeição)`. Execute `08.1 - Criar OS para rejeição`, `08.2 - Gerar Orçamento`, `08.2a - Notificar Cliente sobre Orçamento (rejeição)` e, por fim, `08.3 - Recusar Orçamento por Token (cliente não aceita)`. A notificação grava a variável `orcamentoRecusaToken`, usada automaticamente no endpoint público de recusa.



### Listagem de ordens de serviço (■ Ordenação por status: ■ Em Execução > Aguardando Aprovação > Diagnóstico > Recebida. ■ Mais antigas primeiro. ■ Excluir (lógica não física) da listagem as OS finalizadas e entregues.)

O endpoint `GET /api/ordens-servico/fila` representa a fila operacional da oficina. Ele retorna apenas OS em `EM_EXECUCAO`, `AGUARDANDO_APROVACAO`, `EM_DIAGNOSTICO` ou `RECEBIDA`, nessa ordem de prioridade, e ordena OS do mesmo status por `dataCriacao` ascendente. OS em `FINALIZADA`, `AGUARDANDO_RETIRADA`, `ENTREGUE` ou `CANCELADA` não aparecem nessa fila.



### Atualização de status da OS via alguma ferramenta como e-mail.

A atualização de status da OS ocorre pelas transições oficiais da máquina de estados da aplicação. Como complemento informativo, o sistema envia uma notificação por e-mail ao cliente cadastrado sempre que a OS avança para um novo estado operacional relevante: `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA`, `AGUARDANDO_RETIRADA`, `ENTREGUE` ou `CANCELADA`.

A notificação não altera o fluxo de negócio, não cria novo endpoint público e não substitui a consulta de status por API. Ela apenas informa o cliente sobre a situação atual da OS e inclui o link público de acompanhamento por número da OS: `GET /api/ordens-servico/numero/{numero}/status`.

Por padrão, o envio fica em modo `LOG`, permitindo testes locais sem SMTP real. Para envio real, configure o modo `SMTP` por variáveis de ambiente:

```bash
OFICINA_EMAIL_ENABLED=true
OFICINA_EMAIL_MODE=SMTP
OFICINA_EMAIL_REMETENTE=no-reply@suaoficina.com
SMTP_HOST=smtp.seudominio.com
SMTP_PORT=587
SMTP_USERNAME=usuario-smtp
SMTP_PASSWORD=senha-smtp
SMTP_AUTH=true
SMTP_STARTTLS_ENABLE=true
```

Caso `OFICINA_EMAIL_ENABLED=false`, as notificações são ignoradas de forma controlada, sem bloquear as transições da OS. Falhas de envio também não impedem a evolução do status; elas são registradas em log para análise operacional.

Para validar esse fluxo no Postman, use a pasta `05 - Notificacao por E-mail ao Cliente (E2E SMTP)` da collection. Antes de executar, altere a variável de collection `emailClienteNotificacao` para o e-mail que deve receber as mensagens no provedor SMTP/Mailtrap. A pasta cria dados próprios, abre uma OS completa com esse e-mail e percorre o fluxo até `ENTREGUE`, gerando notificações de status e também a notificação de orçamento por token.

Para garantir que o fluxo chegue até os e-mails de `AGUARDANDO_RETIRADA` e `ENTREGUE`, configure o gateway mock com aprovação determinística no Docker Compose:

```bash
PAGAMENTO_GATEWAY_APPROVAL_RATE=1.0
```

Essa decisão está registrada na [ADR-011](./docs/ADRS/ADR-011-notificacao-status-os-email.md).



## ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------




## Infraestrutura

Esta fase prepara a aplicação para execução em Kubernetes e AWS com uma arquitetura simples, versionável e econômica para o contexto do projeto.

### Arquitetura AWS Proposta

```text
GitHub Actions
   |
   | build, testes e imagem Docker
   v
Amazon ECR
   |
   | imagem versionada
   v
Amazon EKS + Managed Node Group
   |
   | pods Spring Boot
   v
Amazon RDS PostgreSQL
```

Recursos provisionados em [`infra`](./infra):

| Recurso | Finalidade |
|---|---|
| VPC | Rede isolada do projeto. |
| Subnets públicas | Executam os nodes do EKS e o Load Balancer da API. |
| Subnets privadas | Hospedam o RDS PostgreSQL sem IP público. |
| Internet Gateway | Permite tráfego público para subnets públicas. |
| Security Groups | Restringem acesso entre EKS, Load Balancer e RDS. |
| Amazon ECR | Armazena a imagem Docker da API. |
| Amazon EKS | Cluster Kubernetes gerenciado. |
| Managed Node Group | Grupo de nodes EC2 para executar os pods. |
| Amazon RDS PostgreSQL | Banco gerenciado da aplicação. |
| IAM Roles | Permissões necessárias para EKS e nodes. |

Para reduzir custo, a arquitetura não usa NAT Gateway. Os nodes do EKS ficam em subnets públicas, enquanto o RDS fica em subnets privadas com `publicly_accessible=false`. O banco aceita conexão somente a partir do security group associado ao EKS:

```text
EKS cluster/node security group -> RDS security group -> TCP 5432
```

Em produção real, a recomendação seria usar nodes em subnets privadas, NAT Gateway ou VPC Endpoints, maior segregação de ambientes, backups mais longos, Multi-AZ e políticas IAM mais refinadas.

### Ordem Correta De Criação

1. Criar o backend remoto do Terraform em S3.
2. Configurar a infra principal para usar o backend S3.
3. Criar a infraestrutura AWS principal.
4. Atualizar o ConfigMap inicial com a URL do RDS.
5. Criar/atualizar Secrets no cluster.
6. Publicar a imagem Docker no ECR ou garantir que a pipeline ja publicou.
7. Aplicar os manifestos Kubernetes.
8. Obter a URL publica criada pelo Service `LoadBalancer`.
9. Atualizar `OFICINA_PUBLIC_BASE_URL` com a URL real e reiniciar o deployment.
10. Configurar GitHub Secrets.
11. Executar a pipeline CI/CD.
12. Revisar segurança e boas práticas.
13. Destruir o ambiente após a demonstração para reduzir custos.

### Passo A Passo Completo Para Subida Da Infra E Deploy Via CI/CD

Este roteiro consolida a ordem operacional para criar a infraestrutura AWS, preparar o Kubernetes e disparar a esteira que publica a imagem Docker no Amazon ECR.

#### 0. Validar Pré-Requisitos Locais

```bash
aws --version
terraform --version
kubectl version --client
docker --version
aws sts get-caller-identity
```

Se a identidade AWS não estiver configurada:

```bash
aws configure
```

#### 1. Criar O Backend Remoto Do Terraform

```bash
cd infra/backend-bootstrap
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars` e troque o bucket por um nome globalmente único:

```hcl
aws_region        = "us-east-1"
project_name      = "oficina-dgcar"
environment       = "academic"
state_bucket_name = "oficina-dgcar-academic-tfstate-seu-sufixo-unico"
lock_table_name   = "oficina-dgcar-academic-terraform-lock"
```

Execute:

```bash
terraform init
terraform validate
terraform plan
terraform apply
terraform output
terraform output backend_config_example
```

Esse passo cria o bucket S3 para o `terraform.tfstate` remoto e a tabela DynamoDB para lock.

#### 2. Configurar Backend S3 Na Infra Principal

```bash
cd ..
cp backend.tf.example backend.tf
```

Edite `backend.tf` com o bucket e a tabela criados:

```hcl
terraform {
  backend "s3" {
    bucket         = "oficina-dgcar-academic-tfstate-seu-sufixo-unico"
    key            = "oficina-dgcar/academic/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "oficina-dgcar-academic-terraform-lock"
    encrypt        = true
  }
}
```

Inicialize a infra principal:

```bash
terraform init
```

Se já existir state local da infra principal e for necessário migrar:

```bash
terraform init -migrate-state
```

#### 3. Configurar Variáveis Da Infra Principal

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars`:

```hcl
aws_region  = "us-east-1"
project_name = "oficina-dgcar"
environment  = "academic"

eks_cluster_version = null

node_instance_types = ["t3.small"]
node_desired_size   = 1
node_min_size       = 1
node_max_size       = 2
node_disk_size      = 20

db_name                  = "oficina"
db_username              = "oficina"
db_password              = "senha-forte-nao-versionada"
db_instance_class        = "db.t4g.micro"
db_allocated_storage     = 20
db_max_allocated_storage = 30
```

Não versionar `terraform.tfvars`.

#### 4. Criar Infraestrutura AWS

```bash
terraform fmt -recursive
terraform validate
terraform plan
terraform apply
terraform output
```

Guarde os outputs:

```bash
terraform output ecr_repository_url
terraform output eks_cluster_name
terraform output rds_endpoint
terraform output spring_datasource_url
```

Com os valores padrão, o Terraform cria o repositório ECR:

```text
oficina-dgcar/oficina-api
```

A URL completa será semelhante a:

```text
<aws-account-id>.dkr.ecr.us-east-1.amazonaws.com/oficina-dgcar/oficina-api
```

#### 5. Configurar Kubectl Para O EKS

```bash
aws eks update-kubeconfig --region us-east-1 --name oficina-dgcar-academic-eks
kubectl get nodes
```

#### 6. Atualizar ConfigMap Inicial Do Kubernetes

Use o output:

```bash
terraform output spring_datasource_url
```

Atualize `k8s/configmap.yaml`:

```yaml
SPRING_DATASOURCE_URL: "jdbc:postgresql://endpoint-rds-real:5432/oficina"
```

Neste momento ainda não existe URL pública do Load Balancer. Ela só será criada depois que os manifestos Kubernetes forem aplicados. Por enquanto, mantenha `OFICINA_PUBLIC_BASE_URL` com um valor temporário:

```yaml
OFICINA_PUBLIC_BASE_URL: "http://localhost:8080"
```

#### 7. Criar Secret No Kubernetes

```bash
kubectl create namespace oficina --dry-run=client -o yaml | kubectl apply -f -
```

Linux/macOS/Git Bash:

```bash
kubectl create secret generic oficina-api-secret -n oficina \
  --from-literal=SPRING_DATASOURCE_USERNAME=oficina \
  --from-literal=SPRING_DATASOURCE_PASSWORD=senha-do-rds \
  --from-literal=JWT_SECRET=chave-forte-com-no-minimo-32-caracteres \
  --from-literal=SMTP_USERNAME= \
  --from-literal=SMTP_PASSWORD= \
  --dry-run=client -o yaml | kubectl apply -f -
```

Windows PowerShell:

```powershell
kubectl create secret generic oficina-api-secret -n oficina `
  --from-literal=SPRING_DATASOURCE_USERNAME=oficina `
  --from-literal=SPRING_DATASOURCE_PASSWORD=senha-do-rds `
  --from-literal=JWT_SECRET=chave-forte-com-no-minimo-32-caracteres `
  --from-literal=SMTP_USERNAME= `
  --from-literal=SMTP_PASSWORD= `
  --dry-run=client -o yaml | kubectl apply -f -
```

#### 8. Publicar Imagem Docker No ECR

Antes de aplicar os manifestos Kubernetes manualmente, a imagem da aplicação precisa existir no Amazon ECR. Se esse deploy for feito pela pipeline, o GitHub Actions executa este passo automaticamente. Para deploy manual, rode na raiz do projeto:

```bash
cd infra
ECR_URL=$(terraform output -raw ecr_repository_url)
cd ..

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${ECR_URL%/*}

docker build -t oficina-api:latest .
docker tag oficina-api:latest $ECR_URL:latest
docker push $ECR_URL:latest
```

Depois atualize o `Deployment` para usar a imagem publicada:

```bash
kubectl set image deployment/oficina-api oficina-api=$ECR_URL:latest -n oficina
```

Alternativamente, atualize `k8s/kustomization.yaml` com o repositório real antes do `kubectl apply -k k8s`:

```yaml
images:
  - name: oficina-api
    newName: 857145323352.dkr.ecr.us-east-1.amazonaws.com/oficina-dgcar-t16soat/oficina-api
    newTag: latest
```

#### 9. Aplicar E Validar Kubernetes Manualmente

```bash
kubectl kustomize k8s
kubectl apply -k k8s
kubectl get pods -n oficina
kubectl get svc -n oficina
kubectl get hpa -n oficina
kubectl rollout status deployment/oficina-api -n oficina
kubectl logs -n oficina deployment/oficina-api
```

Obtenha a URL pública do Load Balancer:

```bash
kubectl get svc oficina-api -n oficina
```

Se a coluna `EXTERNAL-IP` aparecer como `<pending>`, aguarde alguns minutos:

```bash
kubectl get svc oficina-api -n oficina -w
```

Quando o `EXTERNAL-IP` estiver preenchido, use o hostname exibido com `http://` na frente. Exemplo:

```text
http://a1b2c3d4e5.us-east-1.elb.amazonaws.com
```

#### 10. Atualizar URL Pública Da Aplicação

Atualize `k8s/configmap.yaml` com a URL pública real:

```yaml
OFICINA_PUBLIC_BASE_URL: "http://a1b2c3d4e5.us-east-1.elb.amazonaws.com"
```

Aplique novamente o ConfigMap e reinicie o deployment para os pods carregarem o novo valor:

```bash
kubectl apply -k k8s
kubectl rollout restart deployment/oficina-api -n oficina
kubectl rollout status deployment/oficina-api -n oficina
```

#### 11. Configurar GitHub Secrets

Configure no repositório GitHub:

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_REGION
EKS_CLUSTER_NAME
ECR_REPOSITORY
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
SMTP_USERNAME
SMTP_PASSWORD
```

Valores esperados para este projeto:

```text
AWS_REGION=us-east-1
EKS_CLUSTER_NAME=oficina-dgcar-academic-eks
ECR_REPOSITORY=oficina-dgcar/oficina-api
SPRING_DATASOURCE_USERNAME=oficina
SPRING_DATASOURCE_PASSWORD=senha-do-rds
JWT_SECRET=chave-forte-com-no-minimo-32-caracteres
SMTP_USERNAME=
SMTP_PASSWORD=
```

O secret `ECR_REPOSITORY` deve receber apenas o nome do repositório:

```text
oficina-dgcar/oficina-api
```

Não colocar a URL completa do registry nesse secret.

#### 12. Commitar E Disparar A Esteira

```bash
git status
git add .
git commit -m "feat: add aws eks terraform ci cd phase 2"
git push origin main
```

O push na `main` dispara `.github/workflows/ci-cd.yml`. A pipeline executa:

```text
mvn clean test
docker build
login no Amazon ECR
docker push para o ECR
aws eks update-kubeconfig
kubectl create/update secret
kubectl apply -k k8s
kubectl set image deployment/oficina-api
kubectl rollout status
```

A imagem publicada terá o formato:

```text
<aws-account-id>.dkr.ecr.us-east-1.amazonaws.com/oficina-dgcar/oficina-api:<commit-sha>
```

#### 13. Validar Imagem No ECR E Deploy No EKS

```bash
aws ecr describe-repositories --region us-east-1
aws ecr list-images --region us-east-1 --repository-name oficina-dgcar/oficina-api
kubectl get pods -n oficina
kubectl get svc oficina-api -n oficina
kubectl get hpa -n oficina
kubectl rollout status deployment/oficina-api -n oficina
kubectl logs -n oficina deployment/oficina-api
```

#### 14. Encerrar Ambiente Após A Demonstração

Remova primeiro os recursos Kubernetes que podem manter Load Balancer ativo:

```bash
kubectl delete service oficina-api -n oficina
kubectl delete namespace oficina
```

Depois destrua a infra principal:

```bash
cd infra
terraform destroy
```

Se não precisar mais manter o backend remoto:

```bash
cd backend-bootstrap
terraform destroy
```

Antes de destruir o backend remoto, confirme que não há state necessário no bucket S3.

### 1. Backend Remoto Do Terraform

O backend remoto é criado primeiro porque o bucket S3 precisa existir antes de ser usado para armazenar o `terraform.tfstate`.

Arquivos em [`infra/backend-bootstrap`](./infra/backend-bootstrap):

| Arquivo | Finalidade |
|---|---|
| `main.tf` | Cria bucket S3 e tabela DynamoDB. |
| `variables.tf` | Define região, projeto, ambiente, nome do bucket e tabela. |
| `outputs.tf` | Exibe os valores usados no backend da infra principal. |
| `terraform.tfvars.example` | Modelo local de variáveis do bootstrap. |

Recursos criados:

- bucket S3 privado para o state;
- versionamento no bucket;
- criptografia SSE-S3;
- bloqueio de acesso público;
- tabela DynamoDB para lock do Terraform.

Comandos:

```bash
cd infra/backend-bootstrap
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars` e escolha um nome de bucket globalmente único:

```hcl
state_bucket_name = "oficina-dgcar-academic-tfstate-seu-sufixo-unico"
```

Execute:

```bash
terraform init
terraform plan
terraform apply
terraform output backend_config_example
```

O bootstrap terá um state local apenas para gerenciar o bucket e a tabela de lock. Não versionar `terraform.tfvars` nem arquivos `terraform.tfstate`.

### 2. Configurar Backend S3 Na Infra Principal

Volte para a pasta principal de infraestrutura:

```bash
cd ..
cp backend.tf.example backend.tf
```

Edite `backend.tf` com o bucket e a tabela criados no bootstrap:

```hcl
terraform {
  backend "s3" {
    bucket         = "oficina-dgcar-academic-tfstate-seu-sufixo-unico"
    key            = "oficina-dgcar/academic/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "oficina-dgcar-academic-terraform-lock"
    encrypt        = true
  }
}
```

O arquivo real `backend.tf` não deve ser versionado, pois referencia recursos específicos da conta AWS. O repositório mantém apenas [`infra/backend.tf.example`](./infra/backend.tf.example).

Inicialize a infra principal com backend remoto:

```bash
terraform init
```

Se já existir state local da infra principal, migre para o S3:

```bash
terraform init -migrate-state
```

### 3. Criar Infraestrutura AWS Principal

Na pasta [`infra`](./infra), copie o arquivo de variáveis:

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edite ao menos:

```hcl
aws_region  = "us-east-1"
project_name = "oficina-dgcar"
environment  = "academic"
db_password  = "uma-senha-forte"
```

Por padrão, `eks_cluster_version = null`, deixando a AWS escolher uma versão suportada do EKS no momento da criação. Para travar uma versão específica, informe o valor explicitamente.

Execute:

```bash
terraform init
terraform plan
terraform apply
terraform output
```

Configure o acesso ao cluster:

```bash
aws eks update-kubeconfig --region us-east-1 --name oficina-dgcar-academic-eks
```

Outputs importantes:

| Output | Uso |
|---|---|
| `ecr_repository_url` | Usado no build/push da imagem Docker e no Kustomize. |
| `eks_cluster_name` | Usado no `aws eks update-kubeconfig` e CI/CD. |
| `rds_endpoint` | Endpoint DNS do PostgreSQL. |
| `spring_datasource_url` | Valor para `SPRING_DATASOURCE_URL` no Kubernetes. |

### 4. Estratégia De Banco De Dados

O banco de dados é tratado como infraestrutura gerenciada e versionada pela aplicação:

1. O Terraform cria o Amazon RDS PostgreSQL em subnets privadas, sem exposição pública.
2. O output `spring_datasource_url` gera a JDBC URL do banco.
3. O Kubernetes injeta `SPRING_DATASOURCE_URL` via `ConfigMap`.
4. O Kubernetes injeta `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` via `Secret`.
5. O pod da aplicação Spring Boot inicia no EKS.
6. O Flyway executa automaticamente as migrations versionadas em `src/main/resources/db/migration`.
7. Após as migrations, o Hibernate/JPA valida o schema com `ddl-auto=validate`.

Fluxo visual:

```text
Terraform
   |
   | cria RDS PostgreSQL
   v
Outputs Terraform
   |
   | spring_datasource_url
   v
Kubernetes ConfigMap + Secret
   |
   | variáveis de conexão
   v
Spring Boot
   |
   | startup
   v
Flyway migrations
   |
   | V1, V2, V3...
   v
JPA/Hibernate validate
```

As alterações estruturais do banco devem ser feitas somente por novas migrations Flyway:

```text
src/main/resources/db/migration/V17__descricao_da_mudanca.sql
```

Não criar tabelas, colunas ou seeds diretamente no RDS por SQL manual fora do Flyway, salvo necessidade explícita e documentada. Essa regra mantém o banco reproduzível em ambiente local, Kubernetes e AWS.

### 5. Preparação Da Aplicação Para Kubernetes

A aplicação recebe configurações por variáveis de ambiente. No Kubernetes, valores não sensíveis ficam em `ConfigMap`; valores sensíveis ficam em `Secret`.

Variáveis principais:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://endpoint-rds:5432/oficina
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=senha
JWT_SECRET=chave-forte-para-assinatura-jwt
OFICINA_PUBLIC_BASE_URL=https://url-publica-da-api
OFICINA_EMAIL_ENABLED=false
OFICINA_EMAIL_MODE=LOG
```

O Flyway permanece habilitado no startup. Assim, as migrations em `src/main/resources/db/migration` são aplicadas automaticamente no RDS antes da validação do schema pelo Hibernate com `ddl-auto=validate`.

O projeto usa Spring Boot Actuator para probes:

```text
/actuator/health
/actuator/health/liveness
/actuator/health/readiness
/actuator/info
```

Esses endpoints são liberados no Spring Security para permitir validação pelo Kubernetes sem JWT.

Por padrão, `MANAGEMENT_HEALTH_MAIL_ENABLED=false` desabilita o health indicator de SMTP no Actuator. Isso evita que `/actuator/health` fique `DOWN` em ambiente local ou acadêmico sem servidor SMTP real, mantendo `/actuator/health/liveness` e `/actuator/health/readiness` como probes oficiais do Kubernetes. Para validar o provedor SMTP pelo health agregado, altere essa variável para `true` no ambiente desejado.

### 6. Manifestos Kubernetes

Os manifestos ficam em [`k8s`](./k8s):

| Arquivo | Finalidade |
|---|---|
| `namespace.yaml` | Cria o namespace `oficina`. |
| `configmap.yaml` | Define variáveis não sensíveis. |
| `secret.example.yaml` | Modelo de `Secret` sem credenciais reais. Não é aplicado pelo `kustomization.yaml`. |
| `deployment.yaml` | Executa a API com rolling update, probes, resources e security context. |
| `service.yaml` | Expõe a API via `LoadBalancer`, porta `80` para `8080`. |
| `hpa.yaml` | Escala de `1` a `3` pods por CPU e memória. |
| `kustomization.yaml` | Agrupa manifestos e permite parametrizar imagem/tag. |

Resources do `Deployment`:

```yaml
resources:
  requests:
    cpu: 250m
    memory: 512Mi
  limits:
    cpu: 500m
    memory: 768Mi
```

Probes:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: http

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: http
```

HPA:

```text
minReplicas: 1
maxReplicas: 3
CPU: 70%
Memória: 75%
```

Atualize `k8s/configmap.yaml` com o output `spring_datasource_url`:

```yaml
SPRING_DATASOURCE_URL: "jdbc:postgresql://endpoint-rds:5432/oficina"
```

Atualize a imagem pelo Kustomize ou pela pipeline:

```bash
kubectl set image deployment/oficina-api oficina-api=<account-id>.dkr.ecr.us-east-1.amazonaws.com/oficina-dgcar/oficina-api:<tag> -n oficina
```

### 7. Criar Secrets No Cluster

Não coloque credenciais reais em arquivos versionados. Para uma execução manual, crie o secret diretamente no cluster:

```bash
kubectl create namespace oficina --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic oficina-api-secret -n oficina \
  --from-literal=SPRING_DATASOURCE_USERNAME=oficina \
  --from-literal=SPRING_DATASOURCE_PASSWORD=senha-do-rds \
  --from-literal=JWT_SECRET=chave-forte-com-no-minimo-32-caracteres \
  --from-literal=SMTP_USERNAME= \
  --from-literal=SMTP_PASSWORD= \
  --dry-run=client -o yaml | kubectl apply -f -
```

Em CI/CD, esses valores devem vir de GitHub Secrets ou de um gerenciador de segredos.

### 8. Aplicar E Validar Kubernetes

Renderize os manifestos:

```bash
kubectl kustomize k8s
```

Aplicar:

```bash
kubectl apply -k k8s
```

Validar:

```bash
kubectl get pods -n oficina
kubectl get svc -n oficina
kubectl get hpa -n oficina
kubectl rollout status deployment/oficina-api -n oficina
```

Obter URL pública:

```bash
kubectl get svc oficina-api -n oficina
```

Se o `EXTERNAL-IP` ainda estiver como `<pending>`, aguarde:

```bash
kubectl get svc oficina-api -n oficina -w
```

Depois que o hostname publico aparecer, atualize `OFICINA_PUBLIC_BASE_URL` em `k8s/configmap.yaml`, aplique novamente os manifestos e reinicie o deployment:

```bash
kubectl apply -k k8s
kubectl rollout restart deployment/oficina-api -n oficina
kubectl rollout status deployment/oficina-api -n oficina
```

Em EKS, o `Service` do tipo `LoadBalancer` cria um Load Balancer AWS. Para controlar custos, mantenha apenas um serviço externo e destrua o ambiente quando não estiver em uso.

### 9. Pipeline CI/CD

A esteira fica definida em [`.github/workflows/ci-cd.yml`](./.github/workflows/ci-cd.yml) e executa validação, build, publicação da imagem no Amazon ECR e deploy no Amazon EKS.

Gatilhos:

| Evento | Comportamento |
|---|---|
| `pull_request` para `main` | Executa testes e build Docker local, sem deploy. |
| `push` na `main` | Executa testes, build Docker, push para ECR e deploy no EKS. |
| `workflow_dispatch` | Permite execução manual pelo GitHub Actions. |

Jobs da pipeline:

| Job | Etapas |
|---|---|
| `test` | Checkout, setup Java 17 com cache Maven, `mvn clean test` e upload de relatórios. |
| `docker-build` | Build local da imagem Docker para validar o Dockerfile. |
| `deploy` | Login AWS/ECR, build da imagem, push para ECR, update kubeconfig, criação do Secret, `kubectl apply -k k8s`, atualização da imagem e `rollout status`. |

Secrets necessários no GitHub:

| Secret | Uso |
|---|---|
| `AWS_ACCESS_KEY_ID` | Autenticação AWS da pipeline. |
| `AWS_SECRET_ACCESS_KEY` | Autenticação AWS da pipeline. |
| `AWS_REGION` | Região AWS, neste projeto `us-east-1`. |
| `EKS_CLUSTER_NAME` | Nome do cluster EKS, por exemplo `oficina-dgcar-academic-eks`. |
| `ECR_REPOSITORY` | Nome do repositório ECR, por exemplo `oficina-dgcar/oficina-api`. |
| `SPRING_DATASOURCE_USERNAME` | Usuário do RDS PostgreSQL. |
| `SPRING_DATASOURCE_PASSWORD` | Senha do RDS PostgreSQL. |
| `JWT_SECRET` | Chave forte de assinatura JWT. |
| `SMTP_USERNAME` | Usuário SMTP, vazio se e-mail real estiver desabilitado. |
| `SMTP_PASSWORD` | Senha SMTP, vazio se e-mail real estiver desabilitado. |

Fluxo do deploy:

```text
push main
   |
   v
mvn clean test
   |
   v
docker build
   |
   v
docker push -> Amazon ECR
   |
   v
aws eks update-kubeconfig
   |
   v
kubectl apply -k k8s
   |
   v
kubectl set image deployment/oficina-api
   |
   v
kubectl rollout status
```

A pipeline cria ou atualiza o `Secret` `oficina-api-secret` no namespace `oficina` com valores vindos dos GitHub Secrets. Não há credenciais reais versionadas no repositório.

Antes do primeiro deploy pela pipeline, confirme que:

1. A infraestrutura Terraform foi criada.
2. O output `ecr_repository_url` corresponde ao repositório configurado em `ECR_REPOSITORY`.
3. O output `spring_datasource_url` foi aplicado no `k8s/configmap.yaml`.
4. O usuário AWS da pipeline tem permissão para ECR, EKS e leitura do cluster.

### 10. Segurança E Boas Práticas

Controles aplicados ou documentados no projeto:

| Prática | Aplicação no projeto |
|---|---|
| Não versionar segredos reais | `.env`, `terraform.tfvars`, `backend.tf`, state Terraform e `k8s/secret.local.yaml` estão no `.gitignore`. |
| GitHub Secrets | A pipeline lê credenciais AWS, banco, JWT e SMTP por GitHub Secrets. |
| Kubernetes Secrets | O Secret `oficina-api-secret` é criado/atualizado pela pipeline ou por comando manual, sem credenciais versionadas. |
| Secret de exemplo | `k8s/secret.example.yaml` é apenas modelo e não entra no `kustomization.yaml`, evitando sobrescrever secrets reais. |
| Container não-root | O Dockerfile cria `appuser` com UID/GID `10001`, e o Deployment usa `runAsNonRoot`. |
| Hardening do pod | `allowPrivilegeEscalation=false`, capabilities removidas, `seccompProfile=RuntimeDefault` e token de service account desativado. |
| Requests e limits | O Deployment define CPU/memória para permitir agendamento previsível e HPA. |
| HPA | O HPA escala de `1` a `3` pods por CPU e memória. |
| Security groups restritivos | O RDS aceita PostgreSQL apenas a partir do security group associado ao EKS. |
| Flyway para migrations | Mudanças de schema devem ser feitas por arquivos versionados em `src/main/resources/db/migration`. |
| Logs básicos | A aplicação registra eventos no log padrão do container, acessíveis por `docker compose logs` ou `kubectl logs`. |
| Swagger acadêmico | Swagger/OpenAPI fica habilitado para demonstração. Em produção real, restringir ou desabilitar. |
| IAM com menor privilégio possível | A infra usa roles separadas para EKS cluster e nodes. Para CI/CD, recomenda-se usuário/role dedicada com escopo mínimo para ECR, EKS e leitura do cluster. |

Recomendação para Swagger em ambiente produtivo:

```bash
SPRINGDOC_API_DOCS_ENABLED=false
SPRINGDOC_SWAGGER_UI_ENABLED=false
```

Recomendação de permissões para a identidade usada no GitHub Actions:

- push/pull no repositório ECR da aplicação;
- `eks:DescribeCluster` para o cluster do projeto;
- permissões Kubernetes via `aws-auth`/access entries restritas ao namespace `oficina`;
- evitar usuário administrador permanente para a pipeline.

### 11. Destruir Ambiente E Evitar Custos

Remova primeiro recursos Kubernetes que criam Load Balancer:

```bash
kubectl delete service oficina-api -n oficina
kubectl delete namespace oficina
```

Depois destrua a infraestrutura principal:

```bash
cd infra
terraform destroy
```

Por último, se não precisar mais do backend remoto:

```bash
cd backend-bootstrap
terraform destroy
```

Antes de destruir o bucket de state, confirme que não há state necessário armazenado nele.

## ⚙️ Como rodar

### Pré-requisitos
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

### Com Docker (recomendado)

```bash
# Crie o arquivo local de variáveis de ambiente
cp .env.example .env

# Sobe aplicação + banco PostgreSQL
docker compose up --build -d

# Acompanhar logs
docker compose logs -f app

# Parar
docker compose down

# Parar e resetar o banco
docker compose down -v
```

> O arquivo `.env` é local e não deve ser versionado. Use `.env.example` apenas como modelo e nunca coloque credenciais reais diretamente no `docker-compose.yml`.

### Teste local de notificações por e-mail

Para demonstrar a integração com envio real de e-mails, configure um provedor SMTP de teste, como Mailtrap, no arquivo `.env` local:

```bash
OFICINA_EMAIL_ENABLED=true
OFICINA_EMAIL_MODE=SMTP
OFICINA_EMAIL_REMETENTE=no-reply@dgcar.local
SMTP_HOST=sandbox.smtp.mailtrap.io
SMTP_PORT=587
SMTP_USERNAME=seu_usuario_smtp
SMTP_PASSWORD=sua_senha_smtp
SMTP_AUTH=true
SMTP_STARTTLS_ENABLE=true
PAGAMENTO_GATEWAY_APPROVAL_RATE=1.0
PAGAMENTO_GATEWAY_LATENCY_MS=0
```

Depois reinicie os containers para aplicar as variáveis:

```bash
docker compose down
docker compose up --build -d
docker compose logs -f app
```

No Postman, importe a collection completa em [`postman`](./postman) e execute a pasta `05 - Notificacao por E-mail ao Cliente (E2E SMTP)`. Antes de rodar a pasta, ajuste a variável de collection `emailClienteNotificacao` para o e-mail que deve aparecer no provedor SMTP/Mailtrap.

Fluxo sugerido para demonstração:

1. Mostrar o arquivo `.env` local com as credenciais SMTP preenchidas, sem versionar o arquivo.
2. Subir aplicação e PostgreSQL com `docker compose up --build -d`.
3. Acompanhar logs com `docker compose logs -f app`.
4. Executar a pasta `05 - Notificacao por E-mail ao Cliente (E2E SMTP)` no Postman.
5. Mostrar os e-mails chegando no Mailtrap/provedor SMTP.
6. Mostrar no Swagger ou Postman a consulta de status da OS após as transições.

### Sem Docker (perfil dev com H2)

```bash
# Linux / macOS
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Windows (PowerShell)
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

> ⚠️ No perfil `dev` o banco é em memória — os dados são perdidos ao parar a aplicação.

Após subir, acesse:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
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
| Abrir OS completa | `POST /api/ordens-servico/completa` | ATENDENTE |
| Listar fila operacional | `GET /api/ordens-servico/fila` | ATENDENTE / MECANICO / GESTOR |
| Iniciar diagnóstico | `PATCH /{id}/iniciar-diagnostico` | MECANICO |
| Gerar orçamento | `POST /{id}/orcamento` | ATENDENTE |
| Aprovar / Rejeitar | `PATCH /{id}/aprovar` · `/{id}/rejeitar` | ATENDENTE |
| Notificar cliente sobre orçamento | `POST /{id}/orcamento/notificar-cliente` | ATENDENTE / GESTOR |
| Decisão externa do cliente | `POST /api/orcamentos/decisoes-cliente/{token}/aprovar` · `/recusar` | Público com token opaco |
| Finalizar serviço | `PATCH /{id}/finalizar` | MECANICO |
| Registrar pagamento | `POST /{id}/pagamento` | ATENDENTE |
| Entregar veículo | `PATCH /{id}/entregar` | ATENDENTE |



---

## 🗂️ Estrutura do projeto

```
src/main/java/br/com/oficina/
├── domain/           # Regras de negócio puras: modelos, VOs e exceções de domínio
│   ├── model/        # Entidades e agregados do domínio
│   ├── valueobject/  # Objetos de valor com validação própria
│   └── exception/    # Exceções específicas do domínio
├── application/      # Casos de uso, comandos, queries e ports
│   ├── usecase/      # Orquestração dos fluxos de negócio
│   ├── port/in/      # Contratos de entrada usados pelos adapters inbound
│   ├── port/out/     # Contratos de saída implementados pelos adapters outbound
│   ├── command/      # Objetos de comando para escrita
│   └── query/        # Objetos de consulta/leitura
├── adapters/         # Adapters de entrada e saída
│   ├── in/web/       # Controllers REST, requests, responses e mappers web
│   └── out/          # Persistência, segurança, ViaCEP e pagamento mock
└── infrastructure/   # Configurações Spring, segurança JWT e clientes técnicos
```

---

## 🧪 Testes

A suíte segue uma pirâmide de testes para demonstrar robustez em diferentes níveis:

- **Domínio:** valida modelos, value objects, invariantes e transições de estado sem Spring.
- **Aplicação/use cases:** valida a orquestração dos fluxos com mocks dos ports de saída.
- **Adapters web:** valida controllers REST, payloads JSON, status HTTP, autenticação e RBAC com MockMvc.
- **Segurança e integrações simuladas:** valida JWT, regras 401/403, gateway de pagamento mock e cliente ViaCEP isolado.

| Métrica | Valor atual | Fonte |
|---|---:|---|
| Classes de teste | 45 | `src/test/**/*.java` |
| Testes executados | 361 | Última execução local de `mvn clean test` |
| Cobertura de instruções | 96,23% | `target/site/jacoco/jacoco.csv` |
| Cobertura `br.com.oficina.application.usecase` | 92,02% | `target/site/jacoco/jacoco.csv` |
| Cobertura `br.com.oficina.application.exception` | 100% | `target/site/jacoco/jacoco.csv` |
| Limite mínimo no build | 95% | `jacoco-maven-plugin` |

O JaCoCo está integrado ao ciclo Maven e a build falha se a cobertura total de instruções ficar abaixo de 95%. A suíte atual usa H2 em memória no perfil `test` para velocidade e isolamento; Testcontainers está declarado como extensão futura para cenários de integração com PostgreSQL real.

```bash
# Executar a suíte e validar cobertura mínima
mvn clean test

# Gerar relatório HTML de cobertura (JaCoCo)
mvn clean test jacoco:report
# → target/site/jacoco/index.html

# Gerar relatório Allure (Linux/macOS ou caminhos sem caracteres especiais)
mvn test allure:report
# → target/site/allure-maven-plugin/index.html

# Windows PowerShell (recomendado neste projeto)
.\allure-report.ps1
# → roda testes, gera target/allure-report e abre em http://localhost:9090
# O script evita problemas do allure.bat com caminhos contendo caracteres especiais, como '&'.

```
---

## 📁 Documentação adicional

| Recurso | Localização |
|---|---|
| Diagramas DDD (PlantUML) | [`docs/diagramas`](./docs/diagramas) |
| ADRs (decisões de arquitetura) | [`docs/ADRS`](./docs/ADRS) |
| Collection Postman (280 requests) | [`postman`](./postman) |
| Baseline de contrato da API | [`docs/api-contract-baseline.md`](./docs/api-contract-baseline.md) |
| Requisições HTTP (VS Code) | [`api-requests.http`](./api-requests.http) |
| Requisitos Funcionais e Não Funcionais | [`docs/requisitos`](./docs/requisitos) |
| Relatório de Vulnerabilidades OWASP | [`docs/ReportOWASP`](./docs/ReportOWASP) |
| Plano de deploy AWS da Fase 2 | [`docs/fase-2/plano-deploy-aws.md`](./docs/fase-2/plano-deploy-aws.md) |
| Arquitetura da Fase 2 | [`docs/fase-2/arquitetura-fase-2.md`](./docs/fase-2/arquitetura-fase-2.md) |
| Checklist de entrega da Fase 2 | [`docs/fase-2/checklist-entrega.md`](./docs/fase-2/checklist-entrega.md) |
| Roteiro do vídeo demonstrativo | [`docs/fase-2/roteiro-video-demo.md`](./docs/fase-2/roteiro-video-demo.md) |
| Vídeo demonstrativo da Fase 2 | `TODO: adicionar link público ou não listado do YouTube/Vimeo` |
| BrainStorming | Miro link abaixo |
| Domain Storytelling | Miro link abaixo |
| Diagrama de Linguagem Ubíqua | Miro link abaixo |
| EventStorming | Miro link abaixo |
| Diagrama de Contexto Limitado | Miro link abaixo |

https://miro.com/app/board/uXjVHc0alo8=/?share_link_id=611826904943


## 📄 Licença

Projeto desenvolvido para fins acadêmicos — **Pós-Graduação FIAP**.
