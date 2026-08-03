# Bootstrap do Backend Terraform

Este diretório cria os recursos usados para armazenar o `terraform.tfstate` da infraestrutura principal em S3.

Recursos criados:

- S3 bucket privado para o state.
- Versionamento no bucket.
- Criptografia SSE-S3 (`AES256`).
- Bloqueio de acesso público.
- Lock nativo do state por arquivo `.tflock` no S3.

## Por Que Separar O Bootstrap?

O backend remoto precisa existir antes de ser usado pela infraestrutura principal. Por isso, este diretório é aplicado primeiro com state local. Depois, a pasta `infra/` passa a usar o bucket S3 criado aqui.

## Como Criar

Copie o arquivo de exemplo:

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edite `terraform.tfvars` e escolha um nome globalmente único para o bucket:

```hcl
state_bucket_name = "oficina-dgcar-academic-tfstate-seu-sufixo-unico"
```

Inicialize:

```bash
terraform init
```

Revise:

```bash
terraform plan
```

Crie:

```bash
terraform apply
```

Veja os valores para configurar o backend principal:

```bash
terraform output backend_config_example
```

## Observação

Este bootstrap também terá um state local. Como ele só gerencia o bucket do backend, isso é aceitável para o projeto acadêmico. Não versionar `terraform.tfvars` nem `terraform.tfstate`.
