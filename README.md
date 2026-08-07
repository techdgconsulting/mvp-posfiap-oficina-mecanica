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



##  Evoluções do Projeto - Fase 2 Tech Challenge

### Objetivo da Fase 2

A Fase 2 prepara o projeto para implantação conteinerizada e automatizada em AWS. O escopo inclui Docker, Docker Compose, Kubernetes, Amazon EKS, Amazon ECR, Amazon RDS PostgreSQL, Terraform, backend remoto de state em S3, CI/CD com GitHub Actions, secrets externos ao repositório e documentação operacional.

### Arquitetura

O projeto iniciou com uma organização mais próxima de uma arquitetura em camadas tradicional, com separação entre interfaces, serviços de aplicação, domínio e infraestrutura. Com a evolução funcional do sistema e a necessidade de explicitar melhor os limites arquiteturais, a estrutura foi migrada para uma abordagem baseada em Clean Architecture, mantendo elementos compatíveis com Ports and Adapters.

Adoção da Clean Architecture como estilo arquitetural principal do projeto, organizada em torno de domínio, casos de uso, ports e adapters. A arquitetura atual substitui a leitura anterior de camadas genéricas por uma separação explícita entre núcleo de negócio, orquestração de aplicação e mecanismos externos:


# Estrutura da Clean Architecture

| Camada | Responsabilidade |
|--------|------------------|
| 🟨 **domain/** <br><sub>Enterprise Business Rules</sub> | Contém o núcleo do negócio da aplicação, incluindo **Entities**, **Value Objects**, **Enums**, **Exceções de Domínio** e regras de negócio puras, sem dependência de frameworks ou infraestrutura. |
| 🟥 **application/** <br><sub>Application Business Rules</sub> | Implementa os **Casos de Uso**, **Commands**, **Queries** e a orquestração da aplicação. Define os contratos (Ports) utilizados para comunicação entre o domínio e as camadas externas. |
| 🟥 **application/port/in/** <br><sub>Application Business Rules</sub> | Define os **Input Ports**, ou seja, os contratos de entrada consumidos pelos adapters inbound (Controllers, Mensageria, Scheduler etc.), representando as operações disponíveis da aplicação. |
| 🟥 **application/port/out/** <br><sub>Application Business Rules</sub> | Define os **Output Ports**, responsáveis pelos contratos de saída utilizados pelos casos de uso para acessar persistência, serviços externos, mensageria, autenticação e demais dependências. |
| 🟩 **adapters/in/web/** <br><sub>Interface Adapters</sub> | Implementa os adapters de entrada da aplicação, contendo **Controllers REST**, **DTOs de Request/Response**, **Mappers** e validações responsáveis por converter requisições HTTP em chamadas aos casos de uso. |
| 🟩 **adapters/out/** <br><sub>Interface Adapters</sub> | Implementa os adapters de saída da aplicação, incluindo **Repositórios**, **Clientes HTTP**, **Integrações Externas**, **Gateways**, **Persistência**, **Segurança**, **Mensageria** e demais implementações dos Output Ports. |
| 🟦 **infrastructure/** <br><sub>Frameworks & Drivers</sub> | Contém configurações técnicas da aplicação, como **Spring Boot Configuration**, **Security (JWT/OAuth2)**, **Beans**, **Clients**, **Properties**, configuração de banco de dados, observabilidade e demais componentes de infraestrutura. |

### Legenda

| Cor | Camada |
|------|---------|
| 🟨 | **Enterprise Business Rules** (Domain) |
| 🟥 | **Application Business Rules** (Application / Use Cases / Ports) |
| 🟩 | **Interface Adapters** (Controllers, Gateways, Repositories, Clients) |
| 🟦 | **Frameworks & Drivers** (Spring, Banco de Dados, Segurança, Infraestrutura) |


Essa decisão está registrada na [ADR-002](./docs/ADRS/ADR-002-arquitetura-clean-arch.md).


<br>


### Funcionalidades

 ### Abertura de Ordem de Serviço (OS): receber os dados do cliente, veículo, serviços e peças, retornando a identificação única da OS.

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



### Listagem de ordens de serviço 
#### -> Ordenação por status:  Em Execução > Aguardando Aprovação > Diagnóstico > Recebida; <br>-> Mais antigas primeiro;<br> -> Excluir (lógica não física) da listagem as OS finalizadas e entregues.)

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


Para testar as funcionalidades descritas acima no Postman, importe a collection [`OficinaMecanicaDGCar — Suite Completa`](./postman/OficinaMecanicaDGCar%20%E2%80%94%20Suite%20Completa.postman_collection.json). 

```text
TechChallengeFase2
└── Fluxo Completo + Notificação por E-mail + Aprovação + Consulta de Status + Fila de OSs
```

Não execute isoladamente os requests de notificação, aprovação ou recusa de orçamento, pois eles dependem de login, IDs de cliente/veículo/OS, orçamento ativo e tokens gravados por etapas anteriores.

Antes de executar, altere a variável de collection `emailClienteNotificacao` para o e-mail que deve receber as mensagens no provedor SMTP/Mailtrap. A pasta cria dados próprios, abre uma OS completa com esse e-mail e percorre o fluxo até `ENTREGUE`, gerando notificações de status e também a notificação de orçamento por token. O request `14 - Listar fila operacional de OS` complementa a validação ao confirmar que a OS entregue não retorna na fila operacional.

Para garantir que o fluxo chegue até os e-mails de `AGUARDANDO_RETIRADA` e `ENTREGUE`, configure o gateway mock com aprovação determinística no Docker Compose:

```bash
PAGAMENTO_GATEWAY_APPROVAL_RATE=1.0
```

Essa decisão está registrada na [ADR-011](./docs/ADRS/ADR-011-notificacao-status-os-email.md).


<br>


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
6. Publicar a imagem Docker no ECR.
7. Aplicar os manifestos Kubernetes.
8. Obter a URL publica criada pelo Service `LoadBalancer`.
9. Atualizar `OFICINA_PUBLIC_BASE_URL` com a URL real e reiniciar o deployment.
10. Revisar seguranca e boas praticas.
11. Destruir o ambiente apos a demonstracao para reduzir custos.

Para mantenedores com permissao administrativa no repositorio, a infraestrutura e o deploy da aplicacao tambem podem ser feitos por GitHub Actions. O projeto usa duas pipelines separadas: uma para infraestrutura e outra para aplicacao. Assim, commits de codigo nao executam `terraform apply`.

### Passo A Passo Completo Para Subida Da Infra E Deploy Manual

Este roteiro consolida a ordem operacional para criar a infraestrutura AWS, preparar o Kubernetes e fazer o deploy manual da API. Esse é o fluxo recomendado para quem não tem permissao para alterar GitHub Secrets.

#### 0. Validar Pré-Requisitos Locais 
Execute os comandos no mesmo ambiente onde `aws`, `terraform`, `docker` e `kubectl` estão instalados. Windows/PowerShell, Git Bash e WSL possuem instalações e `PATH` separados.

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

Este passo deve ser executado antes de qualquer `terraform init` na pasta `infra`. A infra principal usa um backend S3, então o bucket S3 precisa existir primeiro.

```bash
cd infra/backend-bootstrap
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars` e troque o bucket por um nome globalmente único:

```hcl
aws_region        = "us-east-1"
project_name      = "oficina-dgcar"
environment       = "development"
state_bucket_name = "oficina-dgcar-fiap-tfstate-tsoat16"
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

Esse passo cria o bucket S3 para o `terraform.tfstate` remoto. O lock do state será feito por arquivo `.tflock` no próprio S3 com `use_lockfile = true`.

#### 2. Configurar Backend S3 Na Infra Principal

Siga para este passo somente depois que o passo anterior terminar com sucesso. Se `terraform init` for executado em `infra` antes da criação do bucket, o Terraform retornará erro informando que o bucket S3 do backend não existe.

```bash
cd ..
cp backend.tf.example backend.tf
```

Edite `backend.tf` com o bucket criado:

```hcl
terraform {
  backend "s3" {
    bucket       = "oficina-dgcar-fiap-tfstate-tsoat16"
    key          = "oficina-dgcar/academic/terraform.tfstate"
    region       = "us-east-1"
    encrypt      = true
    use_lockfile = true
  }
}

```

Inicialize a infra principal:

```bash
terraform init
```

#### 3. Configurar Variáveis Da Infra Principal

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars`:

```hcl
aws_region  = "us-east-1"
project_name = "oficina-dgcar-16soat"
environment  = "development"

eks_cluster_version = null

node_instance_types = ["t3.small"]
node_desired_size   = 1
node_min_size       = 1
node_max_size       = 2
node_disk_size      = 20

db_name                  = "oficina"
db_username              = "oficina"
db_password              = "oficina123"
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
aws eks update-kubeconfig --region us-east-1 --name oficina-dgcar-16soat-development-eks
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

Este passo é obrigatorio para deploy manual pelo terminal, antes de aplicar os manifestos Kubernetes.

Se o deploy for feito pela pipeline do GitHub Actions, este passo manual pode ser pulado. A pipeline cria ou atualiza o Secret `oficina-api-secret` usando os valores configurados em GitHub Secrets por um mantenedor do repositorio.

Crie o namespace:

```bash
kubectl create namespace oficina --dry-run=client -o yaml | kubectl apply -f -
```

Prepare os valores sensiveis:

- `SPRING_DATASOURCE_USERNAME`: use o mesmo valor de `db_username` em `infra/terraform.tfvars`.
- `SPRING_DATASOURCE_PASSWORD`: use o mesmo valor de `db_password` em `infra/terraform.tfvars`.
- `JWT_SECRET`: gere uma chave forte para assinatura dos tokens JWT.

Para gerar o `JWT_SECRET` no Linux/macOS/Git Bash:

```bash
JWT_SECRET=$(openssl rand -base64 48)
echo "$JWT_SECRET"
```

Se `openssl` nao estiver disponivel, gere uma string aleatoria com pelo menos 32 caracteres e use como `JWT_SECRET`.

Linux/macOS/Git Bash:

```bash
kubectl create secret generic oficina-api-secret -n oficina \
  --from-literal=SPRING_DATASOURCE_USERNAME=oficina \
  --from-literal=SPRING_DATASOURCE_PASSWORD='<senha-do-rds>' \
  --from-literal=JWT_SECRET="$JWT_SECRET" \
  --from-literal=SMTP_USERNAME= \
  --from-literal=SMTP_PASSWORD= \
  --dry-run=client -o yaml | kubectl apply -f -
```

Windows PowerShell:

```powershell
kubectl create secret generic oficina-api-secret -n oficina `
  --from-literal=SPRING_DATASOURCE_USERNAME=oficina `
  --from-literal=SPRING_DATASOURCE_PASSWORD='<senha-do-rds>' `
  --from-literal=JWT_SECRET='<chave-jwt-com-no-minimo-32-caracteres>' `
  --from-literal=SMTP_USERNAME= `
  --from-literal=SMTP_PASSWORD= `
  --dry-run=client -o yaml | kubectl apply -f -
```

Para deploy manual sem envio real de e-mail, `SMTP_USERNAME` e `SMTP_PASSWORD` podem ficar vazios. Se houver envio real de e-mail no deploy manual, preencha esses dois valores no comando.

#### 8. Publicar Imagem Docker No ECR

Antes de aplicar os manifestos Kubernetes manualmente, a imagem da aplicação precisa existir no Amazon ECR. Rode na raiz do projeto:

```bash
cd infra
ECR_URL=$(terraform output -raw ecr_repository_url)
cd ..

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${ECR_URL%/*}

docker build -t oficina-api:latest .
docker tag oficina-api:latest $ECR_URL:latest
docker push $ECR_URL:latest
```

O valor de `newName` no `k8s/kustomization.yaml` deve ser o mesmo valor de `ECR_URL`, sem a tag `:latest`. Para conferir:

```bash
echo $ECR_URL
```

Antes de aplicar os manifestos, atualize `k8s/kustomization.yaml` com o repositorio real publicado no ECR:

```yaml
images:
  - name: oficina-api
    newName: <aws-account-id>.dkr.ecr.us-east-1.amazonaws.com/oficina-dgcar/oficina-api
    newTag: latest
```

Use o valor de `ECR_URL` sem a tag `:latest` no campo `newName`. Se o placeholder permanecer, o EKS tentara baixar uma imagem inexistente no Docker Hub e os pods ficarao em `ImagePullBackOff`.

O comando `kubectl set image` so deve ser usado depois que o `Deployment` ja existir no cluster, por exemplo em uma troca posterior de imagem.

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

Essa variável não é necessária para o `Service` responder chamadas HTTP básicas. Ela é usada pela aplicação para montar links públicos em notificações, como acompanhamento de OS e aprovação/recusa de orçamento por token. Se permanecer como `http://localhost:8080`, os links gerados em e-mails ou logs apontarao para a maquina de quem abrir o link, e nao para o Load Balancer da AWS.

Aplique novamente o ConfigMap e reinicie o deployment para os pods carregarem o novo valor:

```bash
kubectl apply -k k8s
kubectl rollout restart deployment/oficina-api -n oficina
kubectl rollout status deployment/oficina-api -n oficina
```

#### 11. Liberar Usuario IAM Da Pipeline No EKS (Opcional Para Mantenedores)

Este passo nao e necessario para o deploy manual. Execute somente se o deploy tambem será feito pela pipeline do GitHub Actions.

Para o GitHub Actions fazer deploy, ele precisa de dois niveis de permissao:

- permissao IAM na AWS para acessar ECR e descrever o cluster EKS;
- permissao Kubernetes dentro do cluster EKS para executar `kubectl apply`, `kubectl set image` e `kubectl rollout status`.

##### 11.1 Criar O Usuario IAM Da Pipeline

O usuario deve existir na AWS IAM. Pelo Console AWS, acesse `IAM > Users > Create user` e crie um usuario programatico especifico para a esteira, por exemplo:

```text
github-actions-oficina-dgcar
```

Nao use access key do usuario root. Depois gere uma access key para esse usuario em `IAM > Users > github-actions-oficina-dgcar > Security credentials > Create access key`. Os valores gerados serao cadastrados nos GitHub Secrets `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` no passo 12.

##### 11.2 Automatizar Permissões Com Terraform

O Terraform da pasta `infra` pode automatizar as permissões da pipeline para um usuario IAM ja existente. Para habilitar, configure no `infra/terraform.tfvars`:

```hcl
enable_github_actions_eks_access = true
github_actions_iam_user_name     = "github-actions-oficina-dgcar"
```

Depois aplique a infra novamente:

```bash
cd infra
terraform plan
terraform apply
```

Com essa opção habilitada, o Terraform:

- anexa `AmazonEC2ContainerRegistryPowerUser` ao usuario IAM da pipeline;
- cria uma policy inline permitindo `eks:DescribeCluster` no cluster criado;
- cria a `aws_eks_access_entry` para o usuario IAM no EKS;
- associa `AmazonEKSClusterAdminPolicy` ao usuario no escopo do cluster.

Para conferir o ARN esperado do usuário da pipeline:

```bash
terraform output github_actions_iam_user_arn
```

##### 11.3 Alternativa Manual Pelo Console AWS

Se não quiser automatizar pelo Terraform, faça todos os passos abaixo no Console AWS.

**A. Anexar permissao de ECR ao usuario IAM**

1. Acesse `IAM > Users > github-actions-oficina-dgcar > Permissions`.
2. Clique em `Add permissions`.
3. Escolha `Attach policies directly`.
4. Procure e selecione `AmazonEC2ContainerRegistryPowerUser`.
5. Confirme em `Add permissions`.

**B. Criar policy inline para `eks:DescribeCluster`**

1. Ainda em `IAM > Users > github-actions-oficina-dgcar > Permissions`, clique em `Add permissions`.
2. Escolha `Create inline policy`.
3. Abra a aba `JSON`.
4. Cole a policy abaixo, trocando `<aws-account-id>` e `<eks_cluster_name>` pelos valores reais:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "eks:DescribeCluster",
      "Resource": "arn:aws:eks:us-east-1:<aws-account-id>:cluster/<eks_cluster_name>"
    }
  ]
}
```

Para obter os valores:

```bash
aws sts get-caller-identity --query Account --output text
cd infra
terraform output -raw eks_cluster_name
```

5. Clique em `Next`.
6. Informe um nome, por exemplo `github-actions-describe-oficina-eks`.
7. Clique em `Create policy`.

**C. Criar a access entry no EKS**

1. Copie o ARN do usuario em `IAM > Users > github-actions-oficina-dgcar > Summary > ARN`.
2. Acesse `EKS > Clusters > <eks_cluster_name> > Access`.
3. Clique em `Create access entry`.
4. Em `IAM principal ARN`, cole o ARN do usuario `github-actions-oficina-dgcar`.
5. Em `Type`, escolha `Standard`.
6. Avance para a etapa de policy.
7. Em `Access policy`, selecione `AmazonEKSClusterAdminPolicy`.
8. Em `Access scope`, selecione `Cluster`.
9. Conclua em `Create`.

O `eks:DescribeCluster` e necessario porque a pipeline executa `aws eks update-kubeconfig`. A access entry e necessaria porque, depois de autenticar na AWS, a pipeline tambem precisa de permissao Kubernetes dentro do cluster.

##### 11.4 Alternativa Manual Pela AWS CLI

Se preferir liberar o usuário da pipeline por linha de comando, execute os comandos abaixo com uma identidade AWS que ja tenha permissão administrativa no EKS:

```bash
AWS_REGION="us-east-1"
CLUSTER_NAME="oficina-dgcar-16soat-development-eks"
GITHUB_USER_ARN="arn:aws:iam::857145323352:user/github-actions-oficina-dgcar"
```

Se o cluster ainda estiver usando o modo antigo de autenticação, habilite o suporte a access entries. Use `API_AND_CONFIG_MAP` para manter compatibilidade com o `aws-auth` existente e permitir o uso da API de access entries:

```bash
aws eks update-cluster-config \
  --region "$AWS_REGION" \
  --name "$CLUSTER_NAME" \
  --access-config authenticationMode=API_AND_CONFIG_MAP
```

Aguarde o cluster voltar ao estado ativo:

```bash
aws eks wait cluster-active \
  --region "$AWS_REGION" \
  --name "$CLUSTER_NAME"
```

Verifique se a access entry ja existe:

```bash
aws eks list-access-entries \
  --region "$AWS_REGION" \
  --cluster-name "$CLUSTER_NAME"
```

Crie a access entry para o usuario IAM da pipeline:

```bash
aws eks create-access-entry \
  --region "$AWS_REGION" \
  --cluster-name "$CLUSTER_NAME" \
  --principal-arn "$GITHUB_USER_ARN" \
  --type STANDARD
```

Associe a policy de acesso ao cluster:

```bash
aws eks associate-access-policy \
  --region "$AWS_REGION" \
  --cluster-name "$CLUSTER_NAME" \
  --principal-arn "$GITHUB_USER_ARN" \
  --policy-arn arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy \
  --access-scope type=cluster
```

Confira a policy associada:

```bash
aws eks list-associated-access-policies \
  --region "$AWS_REGION" \
  --cluster-name "$CLUSTER_NAME" \
  --principal-arn "$GITHUB_USER_ARN"
```



#### 12. Configurar GitHub Secrets (Opcional Para Mantenedores)

Este passo nao e necessário para o deploy manual. Ele e necessário apenas para quem tem permissão administrativa no repositorio e vai executar o deploy pela pipeline do GitHub Actions.

Configure no repositorio GitHub:

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_REGION
TF_STATE_BUCKET
TF_STATE_KEY
DB_USERNAME
DB_PASSWORD
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
SMTP_USERNAME
SMTP_PASSWORD
```

Valores esperados para este projeto:

```text
AWS_REGION=us-east-1
TF_STATE_BUCKET=oficina-dgcar-fiap-tfstate-tsoat16
TF_STATE_KEY=oficina-dgcar/academic/terraform.tfstate
DB_USERNAME=oficina
DB_PASSWORD=senha-do-rds
SPRING_DATASOURCE_USERNAME=oficina
SPRING_DATASOURCE_PASSWORD=senha-do-rds
JWT_SECRET=chave-forte-com-no-minimo-32-caracteres
SMTP_USERNAME=
SMTP_PASSWORD=
```

Os secrets `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` sao opcionais se forem iguais a `DB_USERNAME` e `DB_PASSWORD`. A pipeline da aplicacao usa esses valores como fallback.

Nao e necessario cadastrar `EKS_CLUSTER_NAME` nem `ECR_REPOSITORY`. A pipeline da aplicacao le `eks_cluster_name`, `ecr_repository_url` e `spring_datasource_url` diretamente dos outputs do Terraform no state remoto.

Para gerar o valor de `JWT_SECRET` antes de cadastrar no GitHub:

```bash
openssl rand -base64 48
```

#### 13. Disparar As Esteiras CI/CD Separadas (Opcional Para Mantenedores)

Este passo não é necessário para o deploy manual. Use apenas quando os GitHub Secrets ja estiverem configurados e o usuario/role da pipeline ja tiver acesso ao EKS.

Primeiro execute a esteira de infraestrutura:

```text
.github/workflows/infra.yml
```

Ela pode ser disparada manualmente por `workflow_dispatch` ou automaticamente quando houver mudanca em `infra/**`. Essa esteira executa:

```text
bootstrap governado do bucket S3 do Terraform state
terraform init
terraform validate
terraform plan
terraform apply
terraform output
```

Depois execute a esteira da aplicacao:

```text
.github/workflows/app-cd.yml
```

```bash
git status
git add .
git commit -m "feat: add aws eks terraform ci cd phase 2"
git push origin main
```

O push na `main` dispara `.github/workflows/app-cd.yml` somente quando houver mudanca em codigo da aplicacao, `Dockerfile`, `pom.xml`, `k8s/**` ou no proprio workflow. Essa pipeline nao executa `terraform apply`; ela apenas le os outputs do Terraform no state remoto e executa:

```text
mvn clean test
terraform output
docker build
login no Amazon ECR
docker push para o ECR
aws eks update-kubeconfig
kubectl create/update secret
preparo do ConfigMap e kustomization com outputs do Terraform
kubectl apply -k k8s
kubectl set image deployment/oficina-api
kubectl rollout status
atualizacao de OFICINA_PUBLIC_BASE_URL com o LoadBalancer
```

A imagem publicada terá o formato:

```text
<aws-account-id>.dkr.ecr.us-east-1.amazonaws.com/oficina-dgcar/oficina-api:<commit-sha>
```

#### 14. Validar Imagem No ECR E Deploy No EKS

```bash
aws ecr describe-repositories --region us-east-1
aws ecr list-images --region us-east-1 --repository-name oficina-dgcar-16soat/oficina-api
kubectl get pods -n oficina
kubectl get svc oficina-api -n oficina
kubectl get hpa -n oficina
kubectl rollout status deployment/oficina-api -n oficina
kubectl logs -n oficina deployment/oficina-api
```


#### Teste controlado do HPA para demonstrar escalabilidade:

Instale o Metrics Server, necessario para `kubectl top` e para o HPA calcular CPU/memoria:

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl rollout status deployment/metrics-server -n kube-system
kubectl top nodes
```

Confira o estado inicial do HPA:

```bash
kubectl top pods -n oficina
kubectl get hpa oficina-api -n oficina
```

Confirme que o HPA ficou com `MINPODS 2`, `MAXPODS 3` e `REPLICAS 2`:

Para demonstrar o scale up, reduza temporariamente apenas o target de CPU para `5%`, mantendo memoria em `75%`. Isso faz o HPA tender a subir de 2 para 3 replicas quando houver carga controlada:

```bash
kubectl patch hpa oficina-api -n oficina --type merge -p '{"spec":{"metrics":[{"type":"Resource","resource":{"name":"cpu","target":{"type":"Utilization","averageUtilization":5}}},{"type":"Resource","resource":{"name":"memory","target":{"type":"Utilization","averageUtilization":75}}}]}}'
```

Gere carga contra o `Service` interno da API:

```bash
kubectl run hpa-load -n oficina --image=busybox:1.36 --restart=Never -- /bin/sh -c 'for i in $(seq 1 80); do while true; do wget -q -O- http://oficina-api/actuator/health >/dev/null; done & done; sleep 300'
```

No Git Bash do Windows, evite a conversao automatica de `/bin/sh` usando `MSYS_NO_PATHCONV=1`:

```bash
MSYS_NO_PATHCONV=1 kubectl run hpa-load -n oficina --image=busybox:1.36 --restart=Never -- /bin/sh -c 'for i in $(seq 1 80); do while true; do wget -q -O- http://oficina-api/actuator/health >/dev/null; done & done; sleep 300'
```

Acompanhe o autoscale. Pode levar alguns minutos ate o HPA recalcular as metricas. O objetivo da demonstracao e ver `REPLICAS` subir de `2` para `3`:

```bash
kubectl get hpa oficina-api -n oficina -w
```

Em outro terminal, acompanhe a criação do terceiro pod:

```bash
kubectl get pods -n oficina -w
kubectl get deployment oficina-api -n oficina
kubectl get hpa oficina-api -n oficina
```

Se não escalar depois de alguns minutos, remova o pod de carga e repita com mais concorrência:

```bash
kubectl delete pod hpa-load -n oficina --ignore-not-found

MSYS_NO_PATHCONV=1 kubectl run hpa-load -n oficina --image=busybox:1.36 --restart=Never -- /bin/sh -c 'for i in $(seq 1 150); do while true; do wget -q -O- http://oficina-api/actuator/health >/dev/null; done & done; sleep 300'
```

Ao final, remova o pod de carga e restaure os targets normais do HPA:

```bash
kubectl delete pod hpa-load -n oficina --ignore-not-found
kubectl patch hpa oficina-api -n oficina --type merge -p '{"spec":{"minReplicas":2,"maxReplicas":3,"metrics":[{"type":"Resource","resource":{"name":"cpu","target":{"type":"Utilization","averageUtilization":70}}},{"type":"Resource","resource":{"name":"memory","target":{"type":"Utilization","averageUtilization":75}}}]}}'
```

Depois de alguns minutos sem carga, o HPA deve estabilizar novamente em `REPLICAS 2`. O scale down respeita a janela de estabilização configurada no HPA.

#### 15. Encerrar Ambiente Após A Utilização

Siga esta ordem. O bucket S3 do backend remoto guarda o state da infraestrutura principal, então ele deve ser distruído somente no final. Se o bucket for apagado antes do `terraform destroy` da pasta `infra`, o Terraform perde o mapa dos recursos e VPC/EKS/RDS podem ficar órfãos na AWS.

##### 15.1 Remover Recursos Kubernetes

Remova primeiro os recursos Kubernetes que podem manter Load Balancer e ENIs ativos na VPC:

```bash
aws eks update-kubeconfig --region us-east-1 --name oficina-dgcar-16soat-development-eks

kubectl delete service oficina-api -n oficina
kubectl delete namespace oficina
```

Se o namespace demorar para sumir, acompanhe:

```bash
kubectl get namespace oficina
kubectl get svc -A
```

##### 15.2 Destruir A Infra Principal

Com o backend S3 ainda existente, destrua a infraestrutura principal usando o estado remoto do Terraform:

```bash
cd infra
terraform init -reconfigure
terraform state list
terraform destroy -auto-approve
```

Esse passo deve remover a VPC, subnets, internet gateway, security groups, EKS, node group, RDS e ECR criados pelo Terraform.

Se a operação ficar travada por um lock do backend S3, libere o lock manualmente com o ID exibido pelo erro e repita o comando:

```bash
terraform force-unlock <lock-id>
```

> Use `force-unlock` somente quando você tiver certeza de que ninguém está usando o mesmo state remoto no momento.

##### 15.3 Validar Se A VPC Foi Removida

Depois do `destroy` da infra principal, confirme que a VPC do projeto não aparece mais:

```bash
aws ec2 describe-vpcs \
  --region us-east-1 \
  --filters "Name=tag:Name,Values=oficina-dgcar-16soat-development-vpc" \
  --query "Vpcs[].{VpcId:VpcId,CidrBlock:CidrBlock,Name:Tags[?Key=='Name'].Value|[0]}"
```

Resultado esperado após remoção bem-sucedida:

```json
[]
```

Se a VPC ainda estiver presente, isso normalmente significa que algum recurso dependente ainda está bloqueando a exclusão. Em cenários de EKS, o bloqueio mais comum vem do node group ou do cluster ainda ativo. Nesse caso, remova em ordem:

```bash
aws eks delete-nodegroup \
  --region us-east-1 \
  --cluster-name oficina-dgcar-16soat-development-eks \
  --nodegroup-name oficina-dgcar-16soat-development-nodes

aws eks wait nodegroup-deleted \
  --region us-east-1 \
  --cluster-name oficina-dgcar-16soat-development-eks \
  --nodegroup-name oficina-dgcar-16soat-development-nodes
```

Depois, se o cluster ainda existir:

```bash
aws eks delete-cluster \
  --region us-east-1 \
  --name oficina-dgcar-16soat-development-eks

aws eks wait cluster-deleted \
  --region us-east-1 \
  --name oficina-dgcar-16soat-development-eks
```

Se a exclusão da VPC continuar bloqueada por `network interfaces`, verifique no Console AWS os recursos associados às ENIs, como Load Balancer, EKS ou RDS, e remova esses recursos antes de tentar excluir novamente a VPC.

##### 15.4 Destruir O Backend Remoto

Somente depois da infra principal ter sido destruída, remova o backend remoto:

```bash
cd backend-bootstrap
terraform init -reconfigure
terraform destroy -auto-approve
```

Se o `destroy` do bucket do backend falhar com `BucketNotEmpty`, isso significa que o bucket tem versionamento ativado e ainda guarda versões antigas do `terraform.tfstate` e/ou `delete markers` do S3. Nesse caso, remova manualmente todas as versões antes de repetir o `destroy`:

```bash
BUCKET="oficina-dgcar-fiap-tfstate-tsoat16"

aws s3api delete-objects \
  --bucket "$BUCKET" \
  --delete "$(aws s3api list-object-versions \
    --bucket "$BUCKET" \
    --query '{Objects: Versions[].{Key:Key,VersionId:VersionId}}' \
    --output json)"

aws s3api delete-objects \
  --bucket "$BUCKET" \
  --delete "$(aws s3api list-object-versions \
    --bucket "$BUCKET" \
    --query '{Objects: DeleteMarkers[].{Key:Key,VersionId:VersionId}}' \
    --output json)"
```

Depois de limpar os objetos versionados, execute novamente o destroy do bootstrap:

```bash
terraform destroy -auto-approve
```

> O erro `BucketNotEmpty` não é um problema do Terraform em si; ele aparece porque o bucket do state S3 foi configurado com versionamento, então a remoção do bucket só é permitida após a limpeza explícita das versões e markers.


Todas as decisões da Infra estrutura estão descritas na [ADR-Infra.md] [`docs/infraestrutura`](./docs/infraestrutura) |
<br>

<br>

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

No Postman, importe a collection completa em [`postman`](./postman) e execute a pasta `TechChallengeFase2 > Fluxo Completo + Notificação por E-mail + Aprovação + Consulta de Status + Fila de OSs`. Antes de rodar a pasta, ajuste a variável de collection `emailClienteNotificacao` para o e-mail que deve aparecer no provedor SMTP/Mailtrap.

Fluxo sugerido para demonstração:

1. Mostrar o arquivo `.env` local com as credenciais SMTP preenchidas, sem versionar o arquivo.
2. Subir aplicação e PostgreSQL com `docker compose up --build -d`.
3. Acompanhar logs com `docker compose logs -f app`.
4. Executar a pasta `TechChallengeFase2 > Fluxo Completo + Notificação por E-mail + Aprovação + Consulta de Status + Fila de OSs` no Postman.
5. Mostrar os e-mails chegando no Mailtrap/provedor SMTP.
6. Mostrar no Swagger ou Postman a consulta de status da OS após as transições.
7. Executar o request `14 - Listar fila operacional de OS` para demonstrar que OS entregues/finalizadas ficam fora da fila operacional.

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
| Collection Postman | [`postman`](./postman) |
| Requisições HTTP (VS Code) | [`api-requests.http`](./api-requests.http) |
| Requisitos Funcionais e Não Funcionais | [`docs/requisitos`](./docs/requisitos) |
| Relatório de Vulnerabilidades OWASP | [`docs/ReportOWASP`](./docs/ReportOWASP) |
| BrainStorming | Miro link abaixo |
| Domain Storytelling | Miro link abaixo |
| Diagrama de Linguagem Ubíqua | Miro link abaixo |
| EventStorming | Miro link abaixo |
| Diagrama de Contexto Limitado | Miro link abaixo |

https://miro.com/app/board/uXjVHc0alo8=/?share_link_id=611826904943


## 📄 Licença

Projeto desenvolvido para fins acadêmicos — **Pós-Graduação FIAP**.
