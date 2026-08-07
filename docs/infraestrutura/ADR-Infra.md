# ADR-Infra

## Status
Aceito

## Contexto
A aplicação foi preparada para evoluir de um ambiente local para uma execução em nuvem, com foco em reprodutibilidade, segurança, isolamento de segredos e custo controlado. Para isso, a infraestrutura precisa ser tratada como parte do projeto, com decisões explícitas sobre armazenamento de estado, provisionamento de recursos, banco de dados, deploy no Kubernetes e proteção de credenciais.

## Decisões de infraestrutura

### 1. Backend remoto do Terraform
**Decisão:** utilizar um bucket S3 como backend remoto do Terraform.

**Por quê:**
- o estado do Terraform precisa ser armazenado de forma centralizada e persistente;
- o uso de um backend remoto evita perda de informações entre execuções e ambientes;
- o bucket S3 permite versionamento e lock, reduzindo riscos de sobrescrita simultânea;
- essa abordagem torna a infraestrutura mais segura e mais adequada para equipes e pipelines.

**Consequência:** a criação do backend ocorre antes da infraestrutura principal, pois o estado remoto precisa de um local válido para ser armazenado.

### 2. Configurar o backend S3 na infraestrutura principal
**Decisão:** manter a configuração do backend separada da infraestrutura principal.

**Por quê:**
- o backend remoto depende de recursos específicos da conta AWS e não deve ser tratado como parte do mesmo fluxo de criação da infraestrutura de aplicação;
- essa separação reduz risco de dependência circular e melhora a clareza de responsabilidade;
- facilita a reutilização do mesmo backend em diferentes ambientes ou ciclos de provisionamento.

**Consequência:** a infraestrutura principal passa a depender de um estado remoto estável e compartilhado, em vez de trabalhar apenas com estado local.

### 3. Criar a infraestrutura AWS principal
**Decisão:** provisionar a infraestrutura principal na AWS com VPC, EKS, ECR, RDS e grupos de segurança.

**Por quê:**
- a aplicação precisa de um ambiente controlado para execução em produção acadêmica e demonstração;
- o EKS oferece um ambiente Kubernetes gerenciado, reduzindo a complexidade operacional;
- o ECR centraliza a imagem Docker da aplicação;
- o RDS garante um banco gerenciado e mais confiável do que uma instância local;
- a VPC e os security groups permitem um ambiente mais seguro e isolado.

**Consequência:** a aplicação passa a ser executada em uma arquitetura compatível com deploy automatizado e escalabilidade.

### 4. Estratégia de banco de dados
**Decisão:** tratar o banco de dados como um recurso gerenciado e evoluir seu schema por migrations versionadas.

**Por quê:**
- o banco deve ser reprodutível em ambientes locais, Kubernetes e AWS;
- mudanças de schema precisam ser rastreáveis e auditáveis;
- o uso de migrations evita alterações manuais e inconsistentes no banco;
- o Flyway permite integração natural com a aplicação Spring Boot e com o ciclo de deploy.

**Consequência:** alterações estruturais devem ser introduzidas por novas migrations, preservando consistência e segurança.

### 5. Preparação da aplicação para Kubernetes
**Decisão:** externalizar configurações da aplicação por variáveis de ambiente e separar dados sensíveis dos não sensíveis.

**Por quê:**
- a aplicação precisa adaptar-se a diferentes ambientes sem alterar o código;
- valores sensíveis como credenciais e chaves devem permanecer fora do repositório;
- o Kubernetes funciona melhor quando a configuração é recebida por ConfigMap e Secret;
- probes e health checks são fundamentais para garantir disponibilidade e integração com o orquestrador.

**Consequência:** a aplicação fica preparada para execução em cluster com configuração dinâmica, sem depender de hardcoded values.

### 6. Manifestos Kubernetes
**Decisão:** usar manifests declarativos para orquestrar a aplicação no EKS.

**Por quê:**
- Kubernetes é mais previsível quando a infraestrutura é definida de forma declarativa;
- os manifests permitem versionar a configuração da aplicação junto ao código;
- a separação entre namespace, deployment, service, configmap, secret e HPA melhora manutenção e entendimento do ambiente;
- isso facilita validação, rollback e evolução do deploy.

**Consequência:** a aplicação passa a ter uma implantação padronizada, observável e compatível com automação.

### 7. Componentes principais de Kubernetes e Terraform
**Decisão:** estruturar a infraestrutura com componentes bem definidos em Kubernetes e Terraform para garantir rastreabilidade, reuso e manutenção.

**Por quê:**
- o Terraform é responsável por provisionar os recursos de infraestrutura base, como VPC, EKS, ECR, RDS e security groups;
- o Kubernetes é responsável por orquestrar a aplicação, por meio de namespace, deployment, service, configmap, secret e HPA;
- essa separação deixa claro o que pertence à camada de provisionamento e o que pertence à camada de execução da aplicação;
- a divisão facilita manutenção, diagnóstico de problemas e evolução da solução sem misturar responsabilidades.

**Consequência:** a solução fica mais organizada, com responsabilidades bem definidas entre provisionamento de infraestrutura e execução da aplicação.

### 8. Artefatos da pasta Kubernetes
**Decisão:** organizar os manifests Kubernetes em uma pasta dedicada, com arquivos separados para namespace, configuração, deployment, exposição de serviço, autoscaling e secrets de exemplo.

**Por quê:**
- cada artefato tem uma responsabilidade específica e deve ser mantido de forma isolada;
- a estrutura facilita a leitura, a manutenção e a evolução do ambiente de execução;
- a separação entre configuração e segredo evita misturar dados sensíveis com dados públicos;
- o uso de um ponto único de composição, via Kustomize, simplifica a aplicação dos manifests no cluster.

**Artefatos principais:**
- `namespace.yaml`: cria o namespace `oficina`, isolando os recursos da aplicação em um ambiente dedicado.
- `configmap.yaml`: define variáveis não sensíveis da aplicação, como URL do banco, URL pública, modo de e-mail e configurações de health check.
- `deployment.yaml`: descreve o Deployment da aplicação, incluindo réplicas, estratégia de rollout, recursos, probes, env vars, security context e imagem do container.
- `service.yaml`: expõe a aplicação internamente e externamente por meio de um Service do tipo `LoadBalancer`, permitindo acesso via IP público do provedor de nuvem.
- `hpa.yaml`: define o Horizontal Pod Autoscaler para ajustar automaticamente a quantidade de pods conforme uso de CPU e memória.
- `secret.example.yaml`: serve como modelo de Secret, com valores de exemplo para credenciais sensíveis, sem expor segredos reais.
- `kustomization.yaml`: agrupa os manifests e permite aplicar o conjunto de recursos de forma organizada, além de parametrizar a imagem usada no deployment.

**Consequência:** os recursos do cluster ficam bem organizados, reutilizáveis e fáceis de aplicar, validar e evoluir.

### 9. Criar secrets no cluster
**Decisão:** não versionar segredos reais e armazená-los no cluster por meio de Secret.

**Por quê:**
- credenciais não devem estar em arquivos versionados no repositório;
- o cluster precisa de um meio seguro para receber valores sensíveis como senha do banco, JWT e SMTP;
- essa prática reduz riscos de vazamento e melhora a segurança operacional.

**Consequência:** a aplicação continua funcional sem expor informações sensíveis no código-fonte ou nos manifests versionados.

### 10. Aplicar e validar Kubernetes
**Decisão:** validar a aplicação após o deploy no cluster, incluindo readiness, liveness, serviços e escalabilidade.

**Por quê:**
- um deploy só é considerado completo quando a aplicação realmente responde corretamente;
- a validação de probes e serviços evita falhas silenciosas no ambiente;
- o HPA precisa ser testado para garantir que a aplicação escala de forma previsível sob carga;
- essa etapa garante confiança na solução antes de considerar o ambiente estável.

**Consequência:** o ambiente passa a ser verificado de forma mais completa, reduzindo risco de falhas em demonstração ou operação.

### 11. Pipelines CI/CD separadas
**Decisão:** separar a automação em duas esteiras no GitHub Actions: uma para infraestrutura (`infra.yml`) e outra para aplicação (`app-cd.yml`).

**Por quê:**
- a automação reduz esforço manual e erros humanos;
- a pipeline garante que mudanças passam por validação antes de chegar ao ambiente;
- o push para o ECR e o deploy no EKS tornam o processo mais consistente e repetível;
- commits de aplicação não devem executar `terraform apply` nem alterar a infraestrutura por acidente;
- a esteira da aplicação pode consumir `terraform output` do state remoto sem recriar VPC, EKS, RDS, ECR ou IAM;
- esse fluxo é alinhado com a proposta de uma solução moderna e preparada para evolução.

**Consequência:** a infraestrutura passa a evoluir por um fluxo controlado e a aplicação ganha um caminho de entrega mais profissional, com rastreabilidade, menor risco operacional e maior previsibilidade.

### 12. Segurança e boas práticas
**Decisão:** adotar boas práticas de segurança desde o início do provisionamento e do deploy.

**Por quê:**
- a aplicação e a infraestrutura devem respeitar princípios de menor privilégio, isolamento e proteção de dados;
- segredos, permissões e acessos precisam ser controlados para evitar vazamentos ou uso indevido;
- o uso de usuário não-root, hardening do pod e restrição de acessos melhora a postura de segurança;
- o ambiente acadêmico e de demonstração também precisa ser tratado com responsabilidade.

**Consequência:** a solução fica mais segura, mesmo em um contexto de estudo ou demonstração.

### 13. Destruir o ambiente e evitar custos
**Decisão:** remover recursos Kubernetes e a infraestrutura provisionada ao final do uso.

**Por quê:**
- ambientes em nuvem geram custos contínuos;
- recursos como Load Balancer, EKS, RDS e ECR podem permanecer ativos mesmo sem uso;
- a destruição ordenada evita dependências residuais e facilita a limpeza completa do ambiente;
- essa prática é importante para manter a solução econômica e sustentável.

**Consequência:** o projeto pode ser demonstrado sem gerar custo desnecessário após a fase de validação.

## Resumo executivo
A infraestrutura foi definida para ser segura, reprodutível, automatizada e compatível com uma execução real em AWS. As decisões priorizam a separação entre estado, configuração, deploy e segredos, além de reduzir riscos operacionais e custos. Esse conjunto de escolhas torna a aplicação mais adequada para demonstração acadêmica, validação técnica e evolução futura.
