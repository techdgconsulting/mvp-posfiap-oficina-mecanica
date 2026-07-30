# Especificação de Requisitos 

**Autor:** Diego Gonzalez
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## 1. Introdução

### Objetivo do Sistema
Desenvolver um sistema integrado para gestão de uma oficina mecânica, automatizando e modernizando os processos de atendimento, execução de serviços, controle de estoque, orçamentos, pagamentos e relacionamento com clientes.

### Contexto do Projeto
O projeto faz parte de um desafio de pós-graduação, com o objetivo de propor uma solução tecnológica que resolva os principais gargalos operacionais de oficinas mecânicas, como retrabalho, perda de informações, demora no atendimento e falta de rastreabilidade. O sistema foi concebido para ser um MVP (Produto Mínimo Viável), mas já contempla os principais fluxos de negócio reais do segmento.

### Visão Geral
O sistema cobre todo o ciclo de vida de uma ordem de serviço, desde o cadastro do cliente e do veículo, passando pela geração de orçamento, aprovação, execução, controle de peças e serviços, até o pagamento e entrega do veículo. Inclui autenticação segura, integração com serviços externos (ex: ViaCEP, gateway de pagamento), documentação automática da API e arquitetura baseada em DDD (Domain-Driven Design) com separação clara de camadas.

Principais benefícios esperados:
- Redução de erros e retrabalho
- Maior agilidade no atendimento
- Rastreabilidade e histórico das ordens de serviço
- Melhor experiência para o cliente e para a equipe da oficina


## 2. Requisitos Funcionais

### 2.1 Gestão de Clientes
- **RF01:** O sistema deve permitir o cadastro de novos clientes, informando nome, documento (CPF/CNPJ), telefone e endereço.
- **RF02:** O sistema deve permitir a busca de clientes por ID e por CPF/CNPJ.
- **RF03:** O sistema deve permitir a listagem de todos os clientes cadastrados.
- **RF04:** O sistema deve permitir a atualização dos dados de um cliente.
- **RF05:** O sistema deve permitir a exclusão de clientes, desde que não possuam veículos ou ordens de serviço vinculadas. Tentativa de excluir cliente com vínculos deve retornar erro HTTP 422.

### 2.2 Gestão de Veículos
- **RF06:** O sistema deve permitir o cadastro de veículos, vinculando-os a um cliente. Não é permitido cadastrar dois veículos com a mesma placa (validação Mercosul: `ABC1D23`).
- **RF07:** O sistema deve permitir a busca de veículos por ID.
- **RF08:** O sistema deve permitir a listagem de todos os veículos cadastrados.
- **RF09:** O sistema deve permitir a listagem de veículos de um cliente específico.
- **RF10:** O sistema deve permitir a atualização dos dados de um veículo.
- **RF11:** O sistema deve permitir a exclusão de veículos, desde que não possuam ordens de serviço vinculadas. Tentativa de excluir veículo com vínculo deve retornar erro HTTP 422.

### 2.3 Gestão de Peças e Insumos
- **RF12:** O sistema deve permitir o cadastro de peças e insumos, informando nome, descrição, quantidade em estoque, valor unitário e estoque mínimo (padrão: 5 unidades).
- **RF13:** O sistema deve permitir a busca de peças por ID.
- **RF14:** O sistema deve permitir a listagem de todas as peças.
- **RF15:** O sistema deve permitir a atualização dos dados de uma peça.
- **RF16:** O sistema deve permitir a exclusão de peças.
- **RF17:** O sistema deve permitir a reposição de estoque de uma peça, informando a quantidade a ser adicionada.
- **RF18:** O sistema deve listar peças com estoque abaixo do mínimo configurado.

### 2.4 Gestão de Serviços
- **RF19:** O sistema deve permitir o cadastro de serviços, informando nome, descrição e valor.
- **RF20:** O sistema deve permitir a busca de serviços por ID.
- **RF21:** O sistema deve permitir a listagem de todos os serviços.
- **RF22:** O sistema deve permitir a atualização dos dados de um serviço.
- **RF23:** O sistema deve permitir a exclusão de serviços.

### 2.5 Gestão de Ordens de Serviço (OS)
- **RF24:** O sistema deve permitir a criação de ordens de serviço, vinculando cliente, veículo, serviços e peças.
- **RF25:** O sistema deve permitir a busca de OS por ID e por número (ex: OS-2026-00001).
- **RF26:** O sistema deve permitir a listagem de todas as ordens de serviço.
- **RF27:** O sistema deve permitir a listagem de ordens de serviço de um cliente.
- **RF28:** O sistema deve permitir a consulta pública do status de uma ordem de serviço.
- **RF29:** O sistema deve permitir a atualização do status da OS conforme a máquina de estados: `RECEBIDA` → `EM_DIAGNOSTICO` → `AGUARDANDO_APROVACAO` → `EM_EXECUCAO` → `FINALIZADA` → `AGUARDANDO_RETIRADA` → `ENTREGUE`. Status terminal alternativo: `CANCELADA` (acionado pela rejeição do orçamento pelo cliente). Transições inválidas devem retornar erro HTTP 422.
- **RF34:** O sistema deve permitir a adição de itens (peças e serviços) a uma OS existente nos status `RECEBIDA`, `EM_DIAGNOSTICO` ou `EM_EXECUCAO` (novo problema identificado durante execução).
- **RF35:** O sistema deve permitir a listagem de ordens de serviço filtradas por status.
- **RF36:** O sistema deve permitir a entrega do veículo ao cliente (status `AGUARDANDO_RETIRADA` → `ENTREGUE`), criando automaticamente o registro de `Entrega` e o `Encerramento` da OS.
- **RF37:** O sistema deve expor métricas de tempo das OS: (a) média global (`GET /metricas/tempo-medio`) retornando `tempoMedioExecucao` (dataCriacao → dataFinalizacao) e `tempoMedioAtendimento` (dataCriacao → dataEntrega) em formato legível (ex: "2h 30min", "1d 4h"); (b) breakdown individual por OS (`GET /{id}/metricas`) com todas as datas e tempos calculados. Acesso restrito ao perfil GESTOR.
- **RF39:** O sistema deve permitir a listagem da fila operacional de OS via `GET /api/ordens-servico/fila`, incluindo apenas `EM_EXECUCAO`, `AGUARDANDO_APROVACAO`, `EM_DIAGNOSTICO` e `RECEBIDA`, ordenadas por prioridade operacional e, dentro do mesmo status, por `dataCriacao` ascendente. OS em `FINALIZADA`, `AGUARDANDO_RETIRADA`, `ENTREGUE` e `CANCELADA` devem ser excluídas da fila.
- **RF40:** O sistema deve permitir decisão externa de orçamento pelo cliente por meio de token opaco enviado ao e-mail cadastrado. A notificação é disparada por usuário interno autorizado via `POST /api/ordens-servico/{id}/orcamento/notificar-cliente`; a aprovação ou recusa ocorre por endpoints públicos `POST /api/orcamentos/decisoes-cliente/{token}/aprovar` e `POST /api/orcamentos/decisoes-cliente/{token}/recusar`, sem JWT, mas com validação de token, expiração e uso único.
- **RF41:** O sistema deve enviar notificação informativa por e-mail ao cliente cadastrado quando a OS tiver mudança relevante de status, sem substituir a consulta de status por API e sem bloquear a transição caso o envio falhe.

### 2.6 Segurança e Autenticação
- **RF30:** O sistema deve permitir autenticação de usuários via JWT.
- **RF31:** O sistema deve restringir operações sensíveis a usuários autenticados e autorizados.
- **RF38:** O sistema deve controlar o acesso baseado em perfis (RBAC) com três níveis:
  - **ATENDENTE:** criar OS, gerar/aprovar/rejeitar orçamentos, registrar pagamento, entregar veículo, gerenciar clientes e veículos.
  - **MECANICO:** iniciar diagnóstico, adicionar itens à OS, finalizar serviço.
  - **GESTOR:** acesso completo — tudo do ATENDENTE e MECANICO, mais CRUD de peças/serviços e métricas de KPI.
  O perfil é validado no registro (`POST /api/auth/registro`) e transportado no token JWT.

### 2.7 Integrações
- **RF32:** O sistema deve permitir integração com serviços externos para consulta de CEP.
- **RF33:** O sistema deve integrar-se com gateway de pagamento para processar cobranças, avançando a OS de `FINALIZADA` para `AGUARDANDO_RETIRADA` em caso de aprovação. Em caso de recusa, a OS permanece `FINALIZADA` para nova tentativa. O `transactionId` e a mensagem do gateway são persistidos.



## 3. Requisitos Não Funcionais

- **RNF01:** O sistema deve validar todos os dados de entrada, rejeitando requisições inválidas.
- **RNF02:** O sistema deve fornecer respostas HTTP adequadas (201 para criação, 200 para sucesso, 204 para deleção, 400 para erros de validação, 401/403 para acesso não autorizado).
- **RNF03:** O sistema deve estar documentado via Swagger/OpenAPI, facilitando o consumo das APIs.
- **RNF04:** O sistema deve garantir autenticação e autorização nas operações sensíveis, utilizando JWT.
- **RNF05:** O sistema deve seguir o padrão RESTful para suas APIs.
- **RNF06:** O sistema deve permitir integração com sistemas externos (ex: consulta de CEP, gateway de pagamento).
- **RNF07:** O sistema deve suportar múltiplos usuários simultâneos sem perda de desempenho.
- **RNF08:** O sistema deve registrar e tratar exceções de forma centralizada, retornando mensagens amigáveis ao usuário.
- **RNF09:** O sistema deve ser facilmente testável, com cobertura de testes automatizados para as principais funcionalidades.
- **RNF10:** O sistema deve ser compatível com bancos de dados relacionais (ex: PostgreSQL).
- **RNF11:** O sistema deve ser implantável em ambiente Docker.
- **RNF12:** O tempo de resposta para operações críticas (cadastro, consulta, atualização) deve ser inferior a 2 segundos.
- **RNF13:** O sistema deve estar disponível pelo menos 99% do tempo durante o horário comercial.
- **RNF14:** A API deve ser consumível por qualquer cliente HTTP (navegadores, ferramentas como Postman, aplicações móveis). Interface web não faz parte do escopo deste MVP.


## 4. Restrições

- **Restrição Técnica 1:** O sistema deve utilizar banco de dados relacional PostgreSQL.
- **Restrição Técnica 2:** O backend deve ser implementado em Java, utilizando Spring Boot.
- **Restrição Técnica 3:** O sistema deve ser implantado em containers Docker.
- **Restrição Técnica 4:** Integrações externas (CEP, pagamento) devem ser realizadas apenas via APIs REST públicas e documentadas.
- **Restrição Técnica 5:** O sistema não deve permitir exclusão física de ordens de serviço, apenas cancelamento lógico.
- **Restrição de Negócio 1:** Cada cliente pode possuir múltiplos veículos, mas cada veículo pertence a apenas um cliente.
- **Restrição de Negócio 2:** Não é permitido iniciar execução de serviço sem aprovação prévia do orçamento pelo cliente.
- **Restrição de Integração 1:** O sistema deve respeitar limites de requisições das APIs externas utilizadas.



## 5. Critérios de Aceitação

Exemplos de critérios para requisitos principais:

- **RF01 (Cadastro de Cliente):**
	- O sistema deve validar obrigatoriamente nome, documento e telefone.
	- Não deve ser possível cadastrar dois clientes com o mesmo CPF/CNPJ.
	- Após cadastro, o cliente deve aparecer na listagem geral.

- **RF24 (Criação de OS):**
	- Não deve ser possível criar OS sem vincular cliente e veículo.
	- O número da OS deve ser gerado automaticamente e ser único.
	- A OS criada deve iniciar no status "Recebida".

- **RF28 (Consulta Pública de Status):**
	- Qualquer usuário (sem autenticação) deve conseguir consultar o status de uma OS pelo número legível via `GET /api/ordens-servico/numero/{numero}/status`, informando o número da OS (ex: `OS-2026-00001`).
	- O status retornado deve refletir o estado real da OS no sistema.
	- O campo `mecanicoNome` deve ser retornado no tracking pelo número, identificando o profissional que realizou o serviço (nulo enquanto o diagnóstico não tiver sido iniciado).

- **RF39 (Fila Operacional de OS):**
	- A fila operacional deve ser consultada via `GET /api/ordens-servico/fila`.
	- A listagem deve incluir somente OS nos status `EM_EXECUCAO`, `AGUARDANDO_APROVACAO`, `EM_DIAGNOSTICO` e `RECEBIDA`.
	- A ordenação deve seguir a prioridade `EM_EXECUCAO` → `AGUARDANDO_APROVACAO` → `EM_DIAGNOSTICO` → `RECEBIDA`; dentro do mesmo status, OS mais antigas devem aparecer primeiro.
	- OS em `FINALIZADA`, `AGUARDANDO_RETIRADA`, `ENTREGUE` ou `CANCELADA` não devem aparecer na fila.

- **RF40 (Decisão Externa de Orçamento):**
	- A decisão externa deve ser iniciada por usuário `ATENDENTE` ou `GESTOR`, após existir orçamento ativo para a OS.
	- O sistema deve gerar token opaco, armazenar apenas seu hash, definir expiração e enviar os links de aprovação/recusa ao e-mail do cliente pelo adapter configurado (`LOG` ou `SMTP`).
	- Os endpoints públicos de decisão não devem exigir JWT, mas devem aceitar somente token válido, pendente e não expirado.
	- A aprovação externa deve reaproveitar a mesma regra de negócio da aprovação interna, incluindo avanço da OS para `EM_EXECUCAO` e baixa de estoque das peças.
	- A recusa externa deve reaproveitar a mesma regra de negócio da rejeição interna, cancelando a OS conforme máquina de estados.

- **RF41 (Notificação Informativa de Status da OS):**
	- A notificação deve ocorrer de forma automática após transições persistidas da OS para `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA`, `AGUARDANDO_RETIRADA`, `ENTREGUE` ou `CANCELADA`.
	- O e-mail deve ser informativo, contendo número da OS, status atual e link público de acompanhamento por número da OS.
	- O envio deve usar a porta `EmailNotificacaoPort`, permitindo modo local `LOG`, modo real `SMTP` e desativação controlada por configuração.
	- Falhas de envio não devem impedir a criação da OS nem a atualização do status; o erro deve ser registrado em log.

- **RNF04 (Segurança):**
	- Operações de cadastro, atualização e exclusão só podem ser realizadas por usuários autenticados.
	- O token JWT inválido ou expirado deve bloquear o acesso.



## 6. Rastreabilidade

| Requisito | Caso de Uso / Endpoint / Classe Relacionada |
|-----------|--------------------------------------------|
| RF01      | POST /api/clientes, ClienteController, ClienteService |
| RF06      | POST /api/veiculos, VeiculoController, VeiculoService |
| RF12      | POST /api/pecas, PecaController, PecaService |
| RF19      | POST /api/servicos, ServicoController, ServicoService |
| RF24      | POST /api/ordens-servico, OrdemDeServicoController, OrdemDeServicoService |
| RF28      | GET /api/ordens-servico/numero/{numero}/status (público), GET /api/ordens-servico/{id}/status (autenticado), OrdemDeServicoController, OrdemServicoResponse.mecanicoNome |
| RF39      | GET /api/ordens-servico/fila, ListarFilaOrdensServicoInputPort, OrdemDeServicoRepositoryPort.listarFilaOperacional, SpringDataOrdemDeServicoRepository.findFilaOperacional |
| RF40      | POST /api/ordens-servico/{id}/orcamento/notificar-cliente, POST /api/orcamentos/decisoes-cliente/{token}/aprovar, POST /api/orcamentos/decisoes-cliente/{token}/recusar, OrcamentoDecisaoClienteUseCase, OrcamentoDecisaoClienteRepositoryPort, EmailNotificacaoPort, TokenSeguroPort |
| RF41      | NotificarStatusOrdemServicoInputPort, NotificarStatusOrdemServicoUseCase, EmailNotificacaoPort, LogEmailNotificacaoAdapter, SmtpEmailNotificacaoAdapter, DisabledEmailNotificacaoAdapter |
| RF30, RF31 | POST /api/auth/login, POST /api/auth/registro, AuthController, JwtService |
| RF38      | SecurityConfig (AntPathRequestMatcher + RBAC), PerfilUsuario, @PreAuthorize nos controllers, V13 migration (usuários de demonstração) |



---

