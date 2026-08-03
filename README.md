# ðŸ”§ Oficina MecÃ¢nica API

> **Tech Challenge â€” PÃ³s-GraduaÃ§Ã£o FIAP**
> MVP de back-end para gestÃ£o completa de uma oficina mecÃ¢nica.

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.16-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Coverage](https://img.shields.io/badge/coverage-96.23%25-brightgreen)
![Tests](https://img.shields.io/badge/testes-361-blue)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)

---

## ðŸ“‹ Sobre o projeto

API REST que cobre o ciclo completo de atendimento de uma oficina mecÃ¢nica: do cadastro de clientes e veÃ­culos atÃ© a entrega do veÃ­culo apÃ³s pagamento. Desenvolvido com arquitetura **DDD (Domain-Driven Design)** em camadas, autenticaÃ§Ã£o **JWT** e controle de acesso por perfil (**RBAC**).

**Funcionalidades principais:**
- Cadastro de clientes (CPF/CNPJ) com busca automÃ¡tica de endereÃ§o via **ViaCEP**
- Cadastro de veÃ­culos vinculados a clientes
- CatÃ¡logo de serviÃ§os e controle de estoque de peÃ§as (com alerta de estoque mÃ­nimo)
- Ordens de serviÃ§o com fluxo completo de 7 estados
- GeraÃ§Ã£o de orÃ§amentos, aprovaÃ§Ã£o interna e decisÃ£o externa segura por token
- NotificaÃ§Ã£o informativa por e-mail ao cliente a cada mudanÃ§a relevante de status da OS
- Registro de pagamento via gateway (mock configurÃ¡vel)
- KPIs de tempo mÃ©dio de execuÃ§Ã£o e atendimento


## ðŸš€ Stack

| Camada | Tecnologia |
|---|---|
| Linguagem / Framework | Java 17 + Spring Boot 3.5.16 |
| Banco de dados | PostgreSQL 16 (produÃ§Ã£o) Â· H2 (dev/test) |
| Migrations | Flyway |
| SeguranÃ§a | Spring Security + JWT (HMAC-SHA256) |
| DocumentaÃ§Ã£o | SpringDoc OpenAPI 2.8.17 (Swagger) |
| Testes | JUnit 5 + Mockito Â· JaCoCo Â· Allure Report |
| Infraestrutura | Docker + Docker Compose |
| NotificaÃ§Ãµes | Spring Mail + SMTP configurÃ¡vel |


---



## ---------------------------------------------------------------- EvoluÃ§Ãµes do Projeto - Fase 2 Tech Challenge -------------------------------------------------------------

### Objetivo da Fase 2

A Fase 2 prepara o projeto para implantaÃ§Ã£o conteinerizada e automatizada em AWS, mantendo o foco acadÃªm. O escopo inclui Docker, Docker Compose, Kubernetes, Amazon EKS, Amazon ECR, Amazon RDS PostgreSQL, Terraform, backend remoto de state em S3, CI/CD com GitHub Actions, secrets externos ao repositÃ³rio e documentaÃ§Ã£o operacional.

### Arquitetura

O projeto iniciou com uma organizaÃ§Ã£o mais prÃ³xima de uma arquitetura em camadas tradicional, com separaÃ§Ã£o entre interfaces, serviÃ§os de aplicaÃ§Ã£o, domÃ­nio e infraestrutura. Com a evoluÃ§Ã£o funcional do sistema e a necessidade de explicitar melhor os limites arquiteturais, a estrutura foi migrada para uma abordagem baseada em Clean Architecture, mantendo elementos compatÃ­veis com Ports and Adapters.

AdoÃ§Ã£o da Clean Architecture como estilo arquitetural principal do projeto, organizada em torno de domÃ­nio, casos de uso, ports e adapters. A arquitetura atual substitui a leitura anterior de camadas genÃ©ricas por uma separaÃ§Ã£o explÃ­cita entre nÃºcleo de negÃ³cio, orquestraÃ§Ã£o de aplicaÃ§Ã£o e mecanismos externos:


domain/               â†’ Modelos, Value Objects e exceÃ§Ãµes de domÃ­nio
application/          â†’ Casos de uso, comandos, queries e ports
application/port/in   â†’ Contratos de entrada consumidos pelos adapters inbound
application/port/out  â†’ Contratos de saÃ­da implementados pelos adapters outbound
adapters/in/web       â†’ Controllers REST, requests, responses e mappers web
adapters/out          â†’ PersistÃªncia, pagamento, seguranÃ§a e integraÃ§Ãµes externas
infrastructure/       â†’ ConfiguraÃ§Ãµes Spring, seguranÃ§a JWT e clientes tÃ©cnicos

Essa decisÃ£o estÃ¡ registrada na [ADR-002](./docs/ADRS/ADR-002-arquitetura-clean-arch.md).



### Funcionalidades



 ### Abertura de Ordem de ServiÃ§o (OS): receber os dados do cliente,veÃ­culo, serviÃ§os e peÃ§as, retornando a identificaÃ§Ã£o Ãºnica da OS.

O endpoint `POST /api/ordens-servico` preserva o fluxo por `clienteId` e `veiculoId`, indicado para cliente e veÃ­culo previamente cadastrados. O endpoint `POST /api/ordens-servico/completa` permite abrir a OS com dados cadastrais de cliente e veÃ­culo no mesmo payload, reaproveitando cadastros existentes por documento e placa quando aplicÃ¡vel. ServiÃ§os e peÃ§as sÃ£o informados por IDs do catÃ¡logo existente; valores nÃ£o sÃ£o recebidos livremente no payload.

No cadastro direto de cliente e na abertura completa de OS, o CEP atua como enriquecimento cadastral opcional. Quando um cliente novo Ã© criado e apenas o CEP Ã© informado, o sistema tenta consultar o ViaCEP para preencher logradouro, bairro, cidade e UF; falha ou ausÃªncia de retorno do ViaCEP nÃ£o bloqueia a criaÃ§Ã£o. Na abertura completa, clientes jÃ¡ existentes sÃ£o reaproveitados sem sobrescrever o endereÃ§o cadastrado.



### Consulta de status da OS: informar a situaÃ§Ã£o atual da OS (Recebida, DiagnÃ³stico, Aguardando AprovaÃ§Ã£o, ExecuÃ§Ã£o, Finalizada, Entregue).

A consulta de status da Ordem de ServiÃ§o estÃ¡ disponÃ­vel por dois caminhos complementares. O endpoint `GET /api/ordens-servico/{id}/status` consulta a situaÃ§Ã£o por identificador interno da OS e requer autenticaÃ§Ã£o JWT, sendo indicado para uso operacional por usuÃ¡rios da oficina. O endpoint `GET /api/ordens-servico/numero/{numero}/status` consulta a situaÃ§Ã£o pelo nÃºmero legÃ­vel gerado pelo sistema, como `OS-2026-00001`, e Ã© indicado para acompanhamento externo pelo cliente.

Os retornos informam a situaÃ§Ã£o atual da OS dentro do fluxo de atendimento, incluindo os estados principais do requisito: `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA` e `ENTREGUE`. O fluxo tambÃ©m contempla estados intermediÃ¡rios ou excepcionais usados pela regra de negÃ³cio, como `AGUARDANDO_RETIRADA` e `CANCELADA`.



### AprovaÃ§Ã£o de orÃ§amento: endpoint para receber notificaÃ§Ãµes externas de aprovaÃ§Ã£o ou recusa do orÃ§amento do cliente.

Foi adicionada uma evoluÃ§Ã£o no fluxo de orÃ§amento para permitir que o cliente aprove ou recuse o orÃ§amento por meio de uma notificaÃ§Ã£o externa. O fluxo interno autenticado foi preservado para uso operacional da oficina, enquanto o novo fluxo externo utiliza token opaco, expiraÃ§Ã£o e uso Ãºnico para permitir a decisÃ£o sem exigir login/JWT do cliente.

Principais pontos da implementaÃ§Ã£o:

- `POST /api/ordens-servico/{id}/orcamento/notificar-cliente`: endpoint interno, protegido para `ATENDENTE` e `GESTOR`, responsÃ¡vel por gerar a solicitaÃ§Ã£o de decisÃ£o e enviar a notificaÃ§Ã£o ao e-mail cadastrado.
- `POST /api/orcamentos/decisoes-cliente/{token}/aprovar`: endpoint pÃºblico para aprovaÃ§Ã£o externa do orÃ§amento por token.
- `POST /api/orcamentos/decisoes-cliente/{token}/recusar`: endpoint pÃºblico para recusa externa do orÃ§amento por token.
- O token Ã© gerado de forma opaca e nÃ£o previsÃ­vel; somente seu hash Ã© persistido na base de dados.
- A solicitaÃ§Ã£o possui expiraÃ§Ã£o padrÃ£o de 48 horas e sÃ³ pode ser utilizada uma vez.
- A aprovaÃ§Ã£o externa reaproveita a mesma regra de negÃ³cio da aprovaÃ§Ã£o interna, avanÃ§ando a OS para `EM_EXECUCAO`.
- A recusa externa reaproveita a mesma regra de negÃ³cio da rejeiÃ§Ã£o interna, cancelando a OS conforme o fluxo jÃ¡ existente.
- O envio de e-mail usa `EmailNotificacaoPort`, com modo `LOG` para execuÃ§Ã£o local e modo `SMTP` para envio real quando configurado.

Essa funcionalidade estÃ¡ documentada tambÃ©m na [ADR-010](./docs/ADRS/ADR-010-decisao-externa-orcamento-token.md), na especificaÃ§Ã£o de requisitos e nos diagramas C4.

Para testar no Postman, importe a collection [`OficinaMecanicaDGCar â€” Suite Completa`](./postman/OficinaMecanicaDGCar%20%E2%80%94%20Suite%20Completa.postman_collection.json) e execute o grupo `04 â€” Oficina Mecanica DGCAR > 06 - Ordem de ServiÃ§o (Fluxo Completo)` na ordem. ApÃ³s `06.6 - Gerar OrÃ§amento`, execute `06.6a - Notificar Cliente sobre OrÃ§amento`; esse request cria a solicitaÃ§Ã£o externa, valida os links retornados e grava automaticamente a variÃ¡vel `orcamentoDecisaoToken`. Em seguida, execute `06.7 - Aprovar OrÃ§amento por Token (cliente aceita)`, que usa essa variÃ¡vel para chamar o endpoint pÃºblico de aprovaÃ§Ã£o sem JWT.

O cenÃ¡rio de recusa pode ser validado no grupo `04 â€” Oficina Mecanica DGCAR > 08 - Fluxo Alternativo (RejeiÃ§Ã£o)`. Execute `08.1 - Criar OS para rejeiÃ§Ã£o`, `08.2 - Gerar OrÃ§amento`, `08.2a - Notificar Cliente sobre OrÃ§amento (rejeiÃ§Ã£o)` e, por fim, `08.3 - Recusar OrÃ§amento por Token (cliente nÃ£o aceita)`. A notificaÃ§Ã£o grava a variÃ¡vel `orcamentoRecusaToken`, usada automaticamente no endpoint pÃºblico de recusa.



### Listagem de ordens de serviÃ§o (â–  OrdenaÃ§Ã£o por status: â–  Em ExecuÃ§Ã£o > Aguardando AprovaÃ§Ã£o > DiagnÃ³stico > Recebida. â–  Mais antigas primeiro. â–  Excluir (lÃ³gica nÃ£o fÃ­sica) da listagem as OS finalizadas e entregues.)

O endpoint `GET /api/ordens-servico/fila` representa a fila operacional da oficina. Ele retorna apenas OS em `EM_EXECUCAO`, `AGUARDANDO_APROVACAO`, `EM_DIAGNOSTICO` ou `RECEBIDA`, nessa ordem de prioridade, e ordena OS do mesmo status por `dataCriacao` ascendente. OS em `FINALIZADA`, `AGUARDANDO_RETIRADA`, `ENTREGUE` ou `CANCELADA` nÃ£o aparecem nessa fila.



### AtualizaÃ§Ã£o de status da OS via alguma ferramenta como e-mail.

A atualizaÃ§Ã£o de status da OS ocorre pelas transiÃ§Ãµes oficiais da mÃ¡quina de estados da aplicaÃ§Ã£o. Como complemento informativo, o sistema envia uma notificaÃ§Ã£o por e-mail ao cliente cadastrado sempre que a OS avanÃ§a para um novo estado operacional relevante: `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA`, `AGUARDANDO_RETIRADA`, `ENTREGUE` ou `CANCELADA`.

A notificaÃ§Ã£o nÃ£o altera o fluxo de negÃ³cio, nÃ£o cria novo endpoint pÃºblico e nÃ£o substitui a consulta de status por API. Ela apenas informa o cliente sobre a situaÃ§Ã£o atual da OS e inclui o link pÃºblico de acompanhamento por nÃºmero da OS: `GET /api/ordens-servico/numero/{numero}/status`.

Por padrÃ£o, o envio fica em modo `LOG`, permitindo testes locais sem SMTP real. Para envio real, configure o modo `SMTP` por variÃ¡veis de ambiente:

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

Caso `OFICINA_EMAIL_ENABLED=false`, as notificaÃ§Ãµes sÃ£o ignoradas de forma controlada, sem bloquear as transiÃ§Ãµes da OS. Falhas de envio tambÃ©m nÃ£o impedem a evoluÃ§Ã£o do status; elas sÃ£o registradas em log para anÃ¡lise operacional.

Para validar esse fluxo no Postman, use a pasta `05 - Notificacao por E-mail ao Cliente (E2E SMTP)` da collection. Antes de executar, altere a variÃ¡vel de collection `emailClienteNotificacao` para o e-mail que deve receber as mensagens no provedor SMTP/Mailtrap. A pasta cria dados prÃ³prios, abre uma OS completa com esse e-mail e percorre o fluxo atÃ© `ENTREGUE`, gerando notificaÃ§Ãµes de status e tambÃ©m a notificaÃ§Ã£o de orÃ§amento por token.

Para garantir que o fluxo chegue atÃ© os e-mails de `AGUARDANDO_RETIRADA` e `ENTREGUE`, configure o gateway mock com aprovaÃ§Ã£o determinÃ­stica no Docker Compose:

```bash
PAGAMENTO_GATEWAY_APPROVAL_RATE=1.0
```

Essa decisÃ£o estÃ¡ registrada na [ADR-011](./docs/ADRS/ADR-011-notificacao-status-os-email.md).



## ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------




## Infraestrutura

Esta fase prepara a aplicaÃ§Ã£o para execuÃ§Ã£o em Kubernetes e AWS com uma arquitetura simples, versionÃ¡vel e econÃ´mica para o contexto do projeto.

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
| Subnets pÃºblicas | Executam os nodes do EKS e o Load Balancer da API. |
| Subnets privadas | Hospedam o RDS PostgreSQL sem IP pÃºblico. |
| Internet Gateway | Permite trÃ¡fego pÃºblico para subnets pÃºblicas. |
| Security Groups | Restringem acesso entre EKS, Load Balancer e RDS. |
| Amazon ECR | Armazena a imagem Docker da API. |
| Amazon EKS | Cluster Kubernetes gerenciado. |
| Managed Node Group | Grupo de nodes EC2 para executar os pods. |
| Amazon RDS PostgreSQL | Banco gerenciado da aplicaÃ§Ã£o. |
| IAM Roles | PermissÃµes necessÃ¡rias para EKS e nodes. |

Para reduzir custo, a arquitetura nÃ£o usa NAT Gateway. Os nodes do EKS ficam em subnets pÃºblicas, enquanto o RDS fica em subnets privadas com `publicly_accessible=false`. O banco aceita conexÃ£o somente a partir do security group associado ao EKS:

```text
EKS cluster/node security group -> RDS security group -> TCP 5432
```

Em produÃ§Ã£o real, a recomendaÃ§Ã£o seria usar nodes em subnets privadas, NAT Gateway ou VPC Endpoints, maior segregaÃ§Ã£o de ambientes, backups mais longos, Multi-AZ e polÃ­ticas IAM mais refinadas.

### Ordem Correta De CriaÃ§Ã£o

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

Para mantenedores com permissao administrativa no repositorio, a publicacao da imagem e o deploy tambem podem ser feitos pela pipeline CI/CD. Nesse caso, execute os passos opcionais de GitHub Actions ao final do roteiro.

### Passo A Passo Completo Para Subida Da Infra E Deploy Manual

Este roteiro consolida a ordem operacional para criar a infraestrutura AWS, preparar o Kubernetes e fazer o deploy manual da API. Esse e o fluxo recomendado para avaliadores, pois nao depende de permissao para alterar GitHub Secrets.

#### 0. Validar PrÃ©-Requisitos Locais 
Execute os comandos no mesmo ambiente onde `aws`, `terraform`, `docker` e `kubectl` estÃ£o instalados. Windows/PowerShell, Git Bash e WSL possuem instalaÃ§Ãµes e `PATH` separados.

```bash
aws --version
terraform --version
kubectl version --client
docker --version
aws sts get-caller-identity
```

Se a identidade AWS nÃ£o estiver configurada:

```bash
aws configure
```

#### 1. Criar O Backend Remoto Do Terraform

Este passo deve ser executado antes de qualquer `terraform init` na pasta `infra`. A infra principal usa um backend S3, entÃ£o o bucket S3 precisa existir primeiro.

```bash
cd infra/backend-bootstrap
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars` e troque o bucket por um nome globalmente Ãºnico:

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

Esse passo cria o bucket S3 para o `terraform.tfstate` remoto. O lock do state serÃ¡ feito por arquivo `.tflock` no prÃ³prio S3 com `use_lockfile = true`.

#### 2. Configurar Backend S3 Na Infra Principal

Siga para este passo somente depois que o passo anterior terminar com sucesso. Se `terraform init` for executado em `infra` antes da criaÃ§Ã£o do bucket, o Terraform retornarÃ¡ erro informando que o bucket S3 do backend nÃ£o existe.

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

#### 3. Configurar VariÃ¡veis Da Infra Principal

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

Neste momento ainda nÃ£o existe URL pÃºblica do Load Balancer. Ela sÃ³ serÃ¡ criada depois que os manifestos Kubernetes forem aplicados. Por enquanto, mantenha `OFICINA_PUBLIC_BASE_URL` com um valor temporÃ¡rio:

```yaml
OFICINA_PUBLIC_BASE_URL: "http://localhost:8080"
```

#### 7. Criar Secret No Kubernetes

Este passo e obrigatorio para deploy manual pelo terminal, antes de aplicar os manifestos Kubernetes.

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

Este passo nao e necessario para o deploy manual. Execute somente se o deploy tambem sera feito pela pipeline do GitHub Actions.

Para o GitHub Actions fazer deploy, ele precisa de dois niveis de permissao:

- permissao IAM na AWS para acessar ECR e descrever o cluster EKS;
- permissao Kubernetes dentro do cluster EKS para executar `kubectl apply`, `kubectl set image` e `kubectl rollout status`.

O usuario deverá ser criado na AWS IAM. Pelo Console AWS, acesse `IAM > Users > Create user` e crie um usuario programatico especifico para a esteira, por exemplo:

```text
github-actions-oficina-dgcar
```

Nao use access key do usuario root. Anexe ao usuario IAM uma policy com permissoes de push no ECR e leitura do cluster EKS. Pode usar as policies gerenciadas:

```text
AmazonEC2ContainerRegistryPowerUser
AmazonEKSClusterPolicy
```

Depois gere uma access key para esse usuario em `IAM > Users > github-actions-oficina-dgcar > Security credentials > Create access key`. Os valores gerados serao cadastrados nos GitHub Secrets `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` no passo 12.

Se o cluster estiver com `Authentication mode = ConfigMap`, habilite tambem o modo de access entries:

```bash
cd infra
CLUSTER_NAME=$(terraform output -raw eks_cluster_name)
cd ..

aws eks update-cluster-config \
  --name $CLUSTER_NAME \
  --region us-east-1 \
  --access-config authenticationMode=API_AND_CONFIG_MAP
```

Aguarde o cluster voltar para `Active`:

```bash
aws eks describe-cluster \
  --name $CLUSTER_NAME \
  --region us-east-1 \
  --query "cluster.status"
```

Depois crie, no EKS, uma access entry para o ARN do usuario IAM da pipeline. Essa access entry e o vinculo que permite ao usuario IAM autenticar no cluster Kubernetes.

O ARN do usuario pode ser copiado no Console AWS em `IAM > Users > github-actions-oficina-dgcar > Summary > ARN`. Pelo terminal, recupere e guarde o ARN em uma variavel:

```bash
PIPELINE_USER_ARN=$(aws iam get-user \
  --user-name github-actions-oficina-dgcar \
  --query "User.Arn" \
  --output text)

echo "$PIPELINE_USER_ARN"
```

O valor exibido deve ter o formato:

```text
arn:aws:iam::<aws-account-id>:user/github-actions-oficina-dgcar
```

Com a variavel preenchida, crie a access entry no cluster EKS:

```bash
aws eks create-access-entry \
  --cluster-name $CLUSTER_NAME \
  --region us-east-1 \
  --principal-arn $PIPELINE_USER_ARN \
  --type STANDARD
```

Pelo Console AWS, o caminho equivalente e `EKS > Clusters > <eks_cluster_name> > Access > IAM access entries > Create`.

Associe a policy de acesso ao cluster:

```bash
aws eks associate-access-policy \
  --cluster-name $CLUSTER_NAME \
  --region us-east-1 \
  --principal-arn $PIPELINE_USER_ARN \
  --policy-arn arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy \
  --access-scope type=cluster
```

Pelo console AWS, o caminho equivalente é:

```text
EKS > Clusters > <eks_cluster_name> > Access > Manage
Authentication mode: API and ConfigMap

EKS > Clusters > <eks_cluster_name> > Access > IAM access entries > Create
IAM principal ARN: arn:aws:iam::<aws-account-id>:user/github-actions-oficina-dgcar
Type: Standard
Access policy: AmazonEKSClusterAdminPolicy
Access scope: Cluster
```

#### 12. Configurar GitHub Secrets (Opcional Para Mantenedores)

Este passo nao e necessario para o deploy manual. Ele É necessario apenas para quem tem permissao administrativa no repositorio e vai executar o deploy pela pipeline do GitHub Actions.

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

Para gerar o valor de `JWT_SECRET` antes de cadastrar no GitHub:

```bash
openssl rand -base64 48
```

#### 13. Commitar E Disparar A Esteira CI/CD (Opcional Para Mantenedores)

Este passo nao e necessario para o deploy manual do avaliador. Use apenas quando os GitHub Secrets ja estiverem configurados e o usuario/role da pipeline ja tiver acesso ao EKS.

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

#### 14. Validar Imagem No ECR E Deploy No EKS

```bash
aws ecr describe-repositories --region us-east-1
aws ecr list-images --region us-east-1 --repository-name oficina-dgcar/oficina-api
kubectl get pods -n oficina
kubectl get svc oficina-api -n oficina
kubectl get hpa -n oficina
kubectl rollout status deployment/oficina-api -n oficina
kubectl logs -n oficina deployment/oficina-api
```

Teste controlado do HPA para demonstrar escalabilidade com baixo custo:

Instale o Metrics Server, necessario para `kubectl top` e para o HPA calcular CPU/memoria:

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl rollout status deployment/metrics-server -n kube-system
kubectl top nodes
```

```bash
kubectl top pods -n oficina
kubectl patch hpa oficina-api -n oficina --type merge -p '{"spec":{"maxReplicas":2}}'

kubectl run hpa-load -n oficina --image=busybox:1.36 --restart=Never -- /bin/sh -c 'for i in $(seq 1 30); do while true; do wget -q -O- http://oficina-api/actuator/health >/dev/null; done & done; sleep 180'

kubectl get hpa oficina-api -n oficina -w
kubectl get pods -n oficina -w
```

Ao final, remova o pod de carga e restaure o limite do HPA:

```bash
kubectl delete pod hpa-load -n oficina
kubectl patch hpa oficina-api -n oficina --type merge -p '{"spec":{"maxReplicas":3}}'
```

#### 15. Encerrar Ambiente Após A Utilização

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
| `main.tf` | Cria bucket S3 para o state remoto. |
| `variables.tf` | Define regiÃ£o, projeto, ambiente e nome do bucket. |
| `outputs.tf` | Exibe os valores usados no backend da infra principal. |
| `terraform.tfvars.example` | Modelo local de variÃ¡veis do bootstrap. |

Recursos criados:

- bucket S3 privado para o state;
- versionamento no bucket;
- criptografia SSE-S3;
- bloqueio de acesso público;
- lock nativo do state por arquivo `.tflock` no S3.

Os comandos de criação do backend remoto estão consolidados no passo a passo completo desta seção.

O bootstrap terá um state local apenas para gerenciar o bucket do backend. Não versionar `terraform.tfvars` nem arquivos `terraform.tfstate`.

### 2. Configurar Backend S3 Na Infra Principal

Depois do bootstrap, a infra principal deve ser configurada para usar o bucket S3 criado anteriormente.

O arquivo real `backend.tf` não deve ser versionado, pois referencia recursos específicos da conta AWS. O repositório mantém apenas [`infra/backend.tf.example`](./infra/backend.tf.example).

Os comandos de inicialização e eventual migração de state estão descritos no passo a passo completo.

### 3. Criar Infraestrutura AWS Principal

Na pasta [`infra`](./infra), a infra principal usa variÃ¡veis locais baseadas em `terraform.tfvars.example`.

Por padrÃ£o, `eks_cluster_version = null`, deixando a AWS escolher uma versÃ£o suportada do EKS no momento da criaÃ§Ã£o. Para travar uma versÃ£o especÃ­fica, informe o valor explicitamente.

Os comandos para criar a infra e configurar o acesso ao cluster estÃ£o no passo a passo completo.

Outputs importantes:

| Output | Uso |
|---|---|
| `ecr_repository_url` | Usado no build/push da imagem Docker e no Kustomize. |
| `eks_cluster_name` | Usado no `aws eks update-kubeconfig` e CI/CD. |
| `rds_endpoint` | Endpoint DNS do PostgreSQL. |
| `spring_datasource_url` | Valor para `SPRING_DATASOURCE_URL` no Kubernetes. |

### 4. EstratÃ©gia De Banco De Dados

O banco de dados Ã© tratado como infraestrutura gerenciada e versionada pela aplicaÃ§Ã£o:

1. O Terraform cria o Amazon RDS PostgreSQL em subnets privadas, sem exposiÃ§Ã£o pÃºblica.
2. O output `spring_datasource_url` gera a JDBC URL do banco.
3. O Kubernetes injeta `SPRING_DATASOURCE_URL` via `ConfigMap`.
4. O Kubernetes injeta `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` via `Secret`.
5. O pod da aplicaÃ§Ã£o Spring Boot inicia no EKS.
6. O Flyway executa automaticamente as migrations versionadas em `src/main/resources/db/migration`.
7. ApÃ³s as migrations, o Hibernate/JPA valida o schema com `ddl-auto=validate`.

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
   | variÃ¡veis de conexÃ£o
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

As alteraÃ§Ãµes estruturais do banco devem ser feitas somente por novas migrations Flyway:

```text
src/main/resources/db/migration/V17__descricao_da_mudanca.sql
```

NÃ£o criar tabelas, colunas ou seeds diretamente no RDS por SQL manual fora do Flyway, salvo necessidade explÃ­cita e documentada. Essa regra mantÃ©m o banco reproduzÃ­vel em ambiente local, Kubernetes e AWS.

### 5. PreparaÃ§Ã£o Da AplicaÃ§Ã£o Para Kubernetes

A aplicaÃ§Ã£o recebe configuraÃ§Ãµes por variÃ¡veis de ambiente. No Kubernetes, valores nÃ£o sensÃ­veis ficam em `ConfigMap`; valores sensÃ­veis ficam em `Secret`.

VariÃ¡veis principais:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://endpoint-rds:5432/oficina
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=senha
JWT_SECRET=chave-forte-para-assinatura-jwt
OFICINA_PUBLIC_BASE_URL=https://url-publica-da-api
OFICINA_EMAIL_ENABLED=false
OFICINA_EMAIL_MODE=LOG
```

O Flyway permanece habilitado no startup. Assim, as migrations em `src/main/resources/db/migration` sÃ£o aplicadas automaticamente no RDS antes da validaÃ§Ã£o do schema pelo Hibernate com `ddl-auto=validate`.

O projeto usa Spring Boot Actuator para probes:

```text
/actuator/health
/actuator/health/liveness
/actuator/health/readiness
/actuator/info
```

Esses endpoints sÃ£o liberados no Spring Security para permitir validaÃ§Ã£o pelo Kubernetes sem JWT.

Por padrÃ£o, `MANAGEMENT_HEALTH_MAIL_ENABLED=false` desabilita o health indicator de SMTP no Actuator. Isso evita que `/actuator/health` fique `DOWN` em ambiente local ou acadÃªmico sem servidor SMTP real, mantendo `/actuator/health/liveness` e `/actuator/health/readiness` como probes oficiais do Kubernetes. Para validar o provedor SMTP pelo health agregado, altere essa variÃ¡vel para `true` no ambiente desejado.

### 6. Manifestos Kubernetes

Os manifestos ficam em [`k8s`](./k8s):

| Arquivo | Finalidade |
|---|---|
| `namespace.yaml` | Cria o namespace `oficina`. |
| `configmap.yaml` | Define variÃ¡veis nÃ£o sensÃ­veis. |
| `secret.example.yaml` | Modelo de `Secret` sem credenciais reais. NÃ£o Ã© aplicado pelo `kustomization.yaml`. |
| `deployment.yaml` | Executa a API com rolling update, probes, resources e security context. |
| `service.yaml` | ExpÃµe a API via `LoadBalancer`, porta `80` para `8080`. |
| `hpa.yaml` | Escala de `1` a `3` pods por CPU e memÃ³ria. |
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
MemÃ³ria: 75%
```

Atualize `k8s/configmap.yaml` com o output `spring_datasource_url`, conforme indicado no passo a passo completo.

No primeiro deploy, `OFICINA_PUBLIC_BASE_URL` pode ficar temporariamente como `http://localhost:8080`, pois o Load Balancer ainda nao existe. Depois que `kubectl get svc oficina-api -n oficina` mostrar o `EXTERNAL-IP`, atualize essa variÃ¡vel com a URL publica real para que notificaÃ§Ãµes e links externos sejam gerados corretamente.

Atualize a imagem pelo Kustomize ou pela pipeline, conforme o fluxo escolhido para o deploy.

### 7. Criar Secrets No Cluster

NÃ£o coloque credenciais reais em arquivos versionados. Para uma execuÃ§Ã£o manual, crie o secret diretamente no cluster seguindo o passo a passo completo.

Em CI/CD, esses valores devem vir de GitHub Secrets ou de um gerenciador de segredos.

### 8. Aplicar E Validar Kubernetes

Renderize, aplique e valide os manifestos seguindo os comandos do passo a passo completo.

Teste de HPA com carga curta e controlada:

Instale e valide o Metrics Server antes do teste. Os comandos do teste controlado de HPA estÃ£o no passo a passo completo.

Ao final, remova o pod de carga e restaure o limite original do HPA.

Depois que o hostname publico aparecer, atualize `OFICINA_PUBLIC_BASE_URL` em `k8s/configmap.yaml`, aplique novamente os manifestos e reinicie o deployment conforme o passo a passo completo.

Em EKS, o `Service` do tipo `LoadBalancer` cria um Load Balancer AWS. Para controlar custos, mantenha apenas um serviÃ§o externo e destrua o ambiente quando nÃ£o estiver em uso.

### 9. Pipeline CI/CD

A esteira fica definida em [`.github/workflows/ci-cd.yml`](./.github/workflows/ci-cd.yml) e executa validaÃ§Ã£o, build, publicaÃ§Ã£o da imagem no Amazon ECR e deploy no Amazon EKS.

Gatilhos:

| Evento | Comportamento |
|---|---|
| `pull_request` para `main` | Executa testes e build Docker local, sem deploy. |
| `push` na `main` | Executa testes, build Docker, push para ECR e deploy no EKS. |
| `workflow_dispatch` | Permite execuÃ§Ã£o manual pelo GitHub Actions. |

Jobs da pipeline:

| Job | Etapas |
|---|---|
| `test` | Checkout, setup Java 17 com cache Maven, `mvn clean test` e upload de relatÃ³rios. |
| `docker-build` | Build local da imagem Docker para validar o Dockerfile. |
| `deploy` | Login AWS/ECR, build da imagem, push para ECR, update kubeconfig, criaÃ§Ã£o do Secret, `kubectl apply -k k8s`, atualizaÃ§Ã£o da imagem e `rollout status`. |

Secrets necessÃ¡rios no GitHub:

| Secret | Uso |
|---|---|
| `AWS_ACCESS_KEY_ID` | AutenticaÃ§Ã£o AWS da pipeline. |
| `AWS_SECRET_ACCESS_KEY` | AutenticaÃ§Ã£o AWS da pipeline. |
| `AWS_REGION` | RegiÃ£o AWS, neste projeto `us-east-1`. |
| `EKS_CLUSTER_NAME` | Nome do cluster EKS, por exemplo `oficina-dgcar-academic-eks`. |
| `ECR_REPOSITORY` | Nome do repositÃ³rio ECR, por exemplo `oficina-dgcar/oficina-api`. |
| `SPRING_DATASOURCE_USERNAME` | UsuÃ¡rio do RDS PostgreSQL. |
| `SPRING_DATASOURCE_PASSWORD` | Senha do RDS PostgreSQL. |
| `JWT_SECRET` | Chave forte de assinatura JWT. |
| `SMTP_USERNAME` | UsuÃ¡rio SMTP, vazio se e-mail real estiver desabilitado. |
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

A pipeline cria ou atualiza o `Secret` `oficina-api-secret` no namespace `oficina` com valores vindos dos GitHub Secrets. NÃ£o hÃ¡ credenciais reais versionadas no repositÃ³rio.

Antes do primeiro deploy pela pipeline, confirme que:

1. A infraestrutura Terraform foi criada.
2. O output `ecr_repository_url` corresponde ao repositÃ³rio configurado em `ECR_REPOSITORY`.
3. O output `spring_datasource_url` foi aplicado no `k8s/configmap.yaml`.
4. O usuÃ¡rio AWS da pipeline tem permissÃ£o para ECR, EKS e leitura do cluster.

### 10. SeguranÃ§a E Boas PrÃ¡ticas

Controles aplicados ou documentados no projeto:

| PrÃ¡tica | AplicaÃ§Ã£o no projeto |
|---|---|
| NÃ£o versionar segredos reais | `.env`, `terraform.tfvars`, `backend.tf`, state Terraform e `k8s/secret.local.yaml` estÃ£o no `.gitignore`. |
| GitHub Secrets | A pipeline lÃª credenciais AWS, banco, JWT e SMTP por GitHub Secrets. |
| Kubernetes Secrets | O Secret `oficina-api-secret` Ã© criado/atualizado pela pipeline ou por comando manual, sem credenciais versionadas. |
| Secret de exemplo | `k8s/secret.example.yaml` Ã© apenas modelo e nÃ£o entra no `kustomization.yaml`, evitando sobrescrever secrets reais. |
| Container nÃ£o-root | O Dockerfile cria `appuser` com UID/GID `10001`, e o Deployment usa `runAsNonRoot`. |
| Hardening do pod | `allowPrivilegeEscalation=false`, capabilities removidas, `seccompProfile=RuntimeDefault` e token de service account desativado. |
| Requests e limits | O Deployment define CPU/memÃ³ria para permitir agendamento previsÃ­vel e HPA. |
| HPA | O HPA escala de `1` a `3` pods por CPU e memÃ³ria. |
| Security groups restritivos | O RDS aceita PostgreSQL apenas a partir do security group associado ao EKS. |
| Flyway para migrations | MudanÃ§as de schema devem ser feitas por arquivos versionados em `src/main/resources/db/migration`. |
| Logs bÃ¡sicos | A aplicaÃ§Ã£o registra eventos no log padrÃ£o do container, acessÃ­veis por `docker compose logs` ou `kubectl logs`. |
| Swagger acadÃªmico | Swagger/OpenAPI fica habilitado para demonstraÃ§Ã£o. Em produÃ§Ã£o real, restringir ou desabilitar. |
| IAM com menor privilÃ©gio possÃ­vel | A infra usa roles separadas para EKS cluster e nodes. Para CI/CD, recomenda-se usuÃ¡rio/role dedicada com escopo mÃ­nimo para ECR, EKS e leitura do cluster. |

RecomendaÃ§Ã£o para Swagger em ambiente produtivo:

```bash
SPRINGDOC_API_DOCS_ENABLED=false
SPRINGDOC_SWAGGER_UI_ENABLED=false
```

RecomendaÃ§Ã£o de permissÃµes para a identidade usada no GitHub Actions:

- push/pull no repositÃ³rio ECR da aplicaÃ§Ã£o;
- `eks:DescribeCluster` para o cluster do projeto;
- permissÃµes Kubernetes via `aws-auth`/access entries restritas ao namespace `oficina`;
- evitar usuÃ¡rio administrador permanente para a pipeline.

### 11. Destruir Ambiente E Evitar Custos

Remova primeiro os recursos Kubernetes que podem manter Load Balancer ativo. Depois destrua a infraestrutura principal e, por Ãºltimo, o backend remoto caso ele nÃ£o seja mais necessÃ¡rio.

Antes de destruir o bucket de state, confirme que nÃ£o hÃ¡ state necessÃ¡rio armazenado nele.

## âš™ï¸ Como rodar

### PrÃ©-requisitos
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

### Com Docker (recomendado)

```bash
# Crie o arquivo local de variÃ¡veis de ambiente
cp .env.example .env

# Sobe aplicaÃ§Ã£o + banco PostgreSQL
docker compose up --build -d

# Acompanhar logs
docker compose logs -f app

# Parar
docker compose down

# Parar e resetar o banco
docker compose down -v
```

> O arquivo `.env` Ã© local e nÃ£o deve ser versionado. Use `.env.example` apenas como modelo e nunca coloque credenciais reais diretamente no `docker-compose.yml`.

### Teste local de notificaÃ§Ãµes por e-mail

Para demonstrar a integraÃ§Ã£o com envio real de e-mails, configure um provedor SMTP de teste, como Mailtrap, no arquivo `.env` local:

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

Depois reinicie os containers para aplicar as variÃ¡veis:

```bash
docker compose down
docker compose up --build -d
docker compose logs -f app
```

No Postman, importe a collection completa em [`postman`](./postman) e execute a pasta `05 - Notificacao por E-mail ao Cliente (E2E SMTP)`. Antes de rodar a pasta, ajuste a variÃ¡vel de collection `emailClienteNotificacao` para o e-mail que deve aparecer no provedor SMTP/Mailtrap.

Fluxo sugerido para demonstraÃ§Ã£o:

1. Mostrar o arquivo `.env` local com as credenciais SMTP preenchidas, sem versionar o arquivo.
2. Subir aplicaÃ§Ã£o e PostgreSQL com `docker compose up --build -d`.
3. Acompanhar logs com `docker compose logs -f app`.
4. Executar a pasta `05 - Notificacao por E-mail ao Cliente (E2E SMTP)` no Postman.
5. Mostrar os e-mails chegando no Mailtrap/provedor SMTP.
6. Mostrar no Swagger ou Postman a consulta de status da OS apÃ³s as transiÃ§Ãµes.

### Sem Docker (perfil dev com H2)

```bash
# Linux / macOS
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Windows (PowerShell)
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

> âš ï¸ No perfil `dev` o banco Ã© em memÃ³ria â€” os dados sÃ£o perdidos ao parar a aplicaÃ§Ã£o.

ApÃ³s subir, acesse:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Base:** http://localhost:8080/api

---

## ðŸ” AutenticaÃ§Ã£o

A API usa **JWT Bearer Token**. FaÃ§a login para obter o token e envie-o no header de cada requisiÃ§Ã£o:

```
Authorization: Bearer <token>
```

### UsuÃ¡rios de demonstraÃ§Ã£o

| UsuÃ¡rio | Senha | Perfil | Acesso |
|---|---|---|---|
| `atendente1` | `senha123` | ATENDENTE | Clientes, veÃ­culos, orÃ§amentos, pagamentos |
| `mecanico1` | `senha123` | MECANICO | DiagnÃ³stico, itens da OS, execuÃ§Ã£o |
| `gestor1` | `senha123` | GESTOR | Acesso completo + KPIs + catÃ¡logo |

```bash
# Exemplo de login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "gestor1", "password": "senha123"}'
```

---

## ðŸ”„ Fluxo da Ordem de ServiÃ§o

```
RECEBIDA â†’ EM_DIAGNOSTICO â†’ AGUARDANDO_APROVACAO â†’ EM_EXECUCAO â†’ FINALIZADA â†’ AGUARDANDO_RETIRADA â†’ ENTREGUE
                                      â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â†’ CANCELADA
```

| Etapa | Endpoint | Perfil |
|---|---|---|
| Abrir OS | `POST /api/ordens-servico` | ATENDENTE |
| Abrir OS completa | `POST /api/ordens-servico/completa` | ATENDENTE |
| Listar fila operacional | `GET /api/ordens-servico/fila` | ATENDENTE / MECANICO / GESTOR |
| Iniciar diagnÃ³stico | `PATCH /{id}/iniciar-diagnostico` | MECANICO |
| Gerar orÃ§amento | `POST /{id}/orcamento` | ATENDENTE |
| Aprovar / Rejeitar | `PATCH /{id}/aprovar` Â· `/{id}/rejeitar` | ATENDENTE |
| Notificar cliente sobre orÃ§amento | `POST /{id}/orcamento/notificar-cliente` | ATENDENTE / GESTOR |
| DecisÃ£o externa do cliente | `POST /api/orcamentos/decisoes-cliente/{token}/aprovar` Â· `/recusar` | PÃºblico com token opaco |
| Finalizar serviÃ§o | `PATCH /{id}/finalizar` | MECANICO |
| Registrar pagamento | `POST /{id}/pagamento` | ATENDENTE |
| Entregar veÃ­culo | `PATCH /{id}/entregar` | ATENDENTE |



---

## ðŸ—‚ï¸ Estrutura do projeto

```
src/main/java/br/com/oficina/
â”œâ”€â”€ domain/           # Regras de negÃ³cio puras: modelos, VOs e exceÃ§Ãµes de domÃ­nio
â”‚   â”œâ”€â”€ model/        # Entidades e agregados do domÃ­nio
â”‚   â”œâ”€â”€ valueobject/  # Objetos de valor com validaÃ§Ã£o prÃ³pria
â”‚   â””â”€â”€ exception/    # ExceÃ§Ãµes especÃ­ficas do domÃ­nio
â”œâ”€â”€ application/      # Casos de uso, comandos, queries e ports
â”‚   â”œâ”€â”€ usecase/      # OrquestraÃ§Ã£o dos fluxos de negÃ³cio
â”‚   â”œâ”€â”€ port/in/      # Contratos de entrada usados pelos adapters inbound
â”‚   â”œâ”€â”€ port/out/     # Contratos de saÃ­da implementados pelos adapters outbound
â”‚   â”œâ”€â”€ command/      # Objetos de comando para escrita
â”‚   â””â”€â”€ query/        # Objetos de consulta/leitura
â”œâ”€â”€ adapters/         # Adapters de entrada e saÃ­da
â”‚   â”œâ”€â”€ in/web/       # Controllers REST, requests, responses e mappers web
â”‚   â””â”€â”€ out/          # PersistÃªncia, seguranÃ§a, ViaCEP e pagamento mock
â””â”€â”€ infrastructure/   # ConfiguraÃ§Ãµes Spring, seguranÃ§a JWT e clientes tÃ©cnicos
```

---

## ðŸ§ª Testes

A suÃ­te segue uma pirÃ¢mide de testes para demonstrar robustez em diferentes nÃ­veis:

- **DomÃ­nio:** valida modelos, value objects, invariantes e transiÃ§Ãµes de estado sem Spring.
- **AplicaÃ§Ã£o/use cases:** valida a orquestraÃ§Ã£o dos fluxos com mocks dos ports de saÃ­da.
- **Adapters web:** valida controllers REST, payloads JSON, status HTTP, autenticaÃ§Ã£o e RBAC com MockMvc.
- **SeguranÃ§a e integraÃ§Ãµes simuladas:** valida JWT, regras 401/403, gateway de pagamento mock e cliente ViaCEP isolado.

| MÃ©trica | Valor atual | Fonte |
|---|---:|---|
| Classes de teste | 45 | `src/test/**/*.java` |
| Testes executados | 361 | Ãšltima execuÃ§Ã£o local de `mvn clean test` |
| Cobertura de instruÃ§Ãµes | 96,23% | `target/site/jacoco/jacoco.csv` |
| Cobertura `br.com.oficina.application.usecase` | 92,02% | `target/site/jacoco/jacoco.csv` |
| Cobertura `br.com.oficina.application.exception` | 100% | `target/site/jacoco/jacoco.csv` |
| Limite mÃ­nimo no build | 95% | `jacoco-maven-plugin` |

O JaCoCo estÃ¡ integrado ao ciclo Maven e a build falha se a cobertura total de instruÃ§Ãµes ficar abaixo de 95%. A suÃ­te atual usa H2 em memÃ³ria no perfil `test` para velocidade e isolamento; Testcontainers estÃ¡ declarado como extensÃ£o futura para cenÃ¡rios de integraÃ§Ã£o com PostgreSQL real.

```bash
# Executar a suÃ­te e validar cobertura mÃ­nima
mvn clean test

# Gerar relatÃ³rio HTML de cobertura (JaCoCo)
mvn clean test jacoco:report
# â†’ target/site/jacoco/index.html

# Gerar relatÃ³rio Allure (Linux/macOS ou caminhos sem caracteres especiais)
mvn test allure:report
# â†’ target/site/allure-maven-plugin/index.html

# Windows PowerShell (recomendado neste projeto)
.\allure-report.ps1
# â†’ roda testes, gera target/allure-report e abre em http://localhost:9090
# O script evita problemas do allure.bat com caminhos contendo caracteres especiais, como '&'.

```
---

## ðŸ“ DocumentaÃ§Ã£o adicional

| Recurso | LocalizaÃ§Ã£o |
|---|---|
| Diagramas DDD (PlantUML) | [`docs/diagramas`](./docs/diagramas) |
| ADRs (decisÃµes de arquitetura) | [`docs/ADRS`](./docs/ADRS) |
| Collection Postman (280 requests) | [`postman`](./postman) |
| Baseline de contrato da API | [`docs/api-contract-baseline.md`](./docs/api-contract-baseline.md) |
| RequisiÃ§Ãµes HTTP (VS Code) | [`api-requests.http`](./api-requests.http) |
| Requisitos Funcionais e NÃ£o Funcionais | [`docs/requisitos`](./docs/requisitos) |
| RelatÃ³rio de Vulnerabilidades OWASP | [`docs/ReportOWASP`](./docs/ReportOWASP) |
| Plano de deploy AWS da Fase 2 | [`docs/fase-2/plano-deploy-aws.md`](./docs/fase-2/plano-deploy-aws.md) |
| Arquitetura da Fase 2 | [`docs/fase-2/arquitetura-fase-2.md`](./docs/fase-2/arquitetura-fase-2.md) |
| VÃ­deo demonstrativo da Fase 2 | `TODO: adicionar link pÃºblico ou nÃ£o listado do YouTube/Vimeo` |
| BrainStorming | Miro link abaixo |
| Domain Storytelling | Miro link abaixo |
| Diagrama de Linguagem UbÃ­qua | Miro link abaixo |
| EventStorming | Miro link abaixo |
| Diagrama de Contexto Limitado | Miro link abaixo |

https://miro.com/app/board/uXjVHc0alo8=/?share_link_id=611826904943


## ðŸ“„ LicenÃ§a

Projeto desenvolvido para fins acadÃªmicos â€” **PÃ³s-GraduaÃ§Ã£o FIAP**.
